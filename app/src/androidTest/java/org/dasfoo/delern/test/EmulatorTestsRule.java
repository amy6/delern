/*
 * Copyright (C) 2017 Katarina Sheremet
 * This file is part of Delern.
 *
 * Delern is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Delern is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with  Delern.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.dasfoo.delern.test;

import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;

import org.dasfoo.delern.models.helpers.MultiWrite;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class EmulatorTestsRule implements TestRule {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmulatorTestsRule.class);
    private static final int RETRY_COUNT = 4;

    private final IdlingResource mFirebaseOperationIdlingResource = new IdlingResource() {
        private ResourceCallback mResourceCallback;
        private boolean mIsIdle = false;

        @Override
        public String getName() {
            return "Firebase Operation";
        }

        @Override
        public boolean isIdleNow() {
            if (MultiWrite.getOperationsInFlight() == 0) {
                if (!mIsIdle && mResourceCallback != null) {
                    mResourceCallback.onTransitionToIdle();
                }
                mIsIdle = true;
            } else {
                mIsIdle = false;
            }
            return mIsIdle;
        }

        @Override
        public void registerIdleTransitionCallback(final ResourceCallback callback) {
            mResourceCallback = callback;
        }
    };

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                // Raise Idling policy timeout because emulator or network can be really slow.
                IdlingPolicies.setIdlingResourceTimeout(2, TimeUnit.MINUTES);
                IdlingPolicies.setMasterPolicyTimeout(2, TimeUnit.MINUTES);
                for (int i = 0; ; ++i) {
                    try {
                        IdlingRegistry.getInstance().register(mFirebaseOperationIdlingResource);
                        base.evaluate();
                        break;
                    } catch (Throwable t) {
                        IdlingRegistry.getInstance().unregister(mFirebaseOperationIdlingResource);
                        if (i == RETRY_COUNT) {
                            throw t;
                        }
                        LOGGER.error("Test {} failed ({} / {}), retrying",
                                description.getDisplayName(), i, RETRY_COUNT, t);
                    }
                }
            }
        };
    }
}
