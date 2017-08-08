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

package org.dasfoo.delern.models;

import org.dasfoo.delern.models.helpers.AbstractTrackingFunction;
import org.dasfoo.delern.models.helpers.AbstractTrackingProcedure;
import org.dasfoo.delern.models.helpers.TaskAdapter;
import org.dasfoo.delern.test.FirebaseServerUnitTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for User model.
 */
public class UserTest extends FirebaseServerUnitTest {

    private User mUser;

    @Before
    public void createUser() throws Exception {
        mUser = signIn();
    }

    @Test
    public void save_succeeds() throws Exception {
        mUser.save().onResult(new AbstractTrackingProcedure<Void>() {
            @Override
            public void call(Void parameter) {
                testSucceeded();
            }
        });
    }

    @Test
    public void user_savedAndFetched() throws Exception {
        mUser.save().continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<User>>() {
            @Override
            public TaskAdapter<User> call(Void parameter) {
                return mUser.watch(User.class);
            }
        }).onResult(new AbstractTrackingProcedure<User>() {
            @Override
            public void call(final User user) {
                if (user.getName().startsWith("Bob ") && user.getEmail().startsWith("bob-")) {
                    testSucceeded();
                }
            }
        });
    }
}
