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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirebaseSignInRule extends ExternalResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseSignInRule.class);

    private final boolean mAutoSignIn;

    public FirebaseSignInRule(final boolean autoSignIn) {
        mAutoSignIn = autoSignIn;
    }

    @Override
    protected void before() throws Throwable {
        if (mAutoSignIn) {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                signIn("bob");
            }
        } else {
            FirebaseAuth.getInstance().signOut();
        }
    }

    private static String idCompatibleString(final String src) {
        return src.replaceAll("[^a-zA-Z0-9]", "-").toLowerCase();
    }

    public String signIn(final String deviceLocalUserId) {
        String email = idCompatibleString(deviceLocalUserId) + "@" +
                idCompatibleString(FirebaseInstanceId.getInstance().getId()) +
                ".example.com";
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, email)
                .addOnFailureListener(error -> FirebaseAuth.getInstance()
                        // If we can't sign in, try creating account.
                        .createUserWithEmailAndPassword(email, email)
                        .addOnSuccessListener(authResult ->
                                authResult.getUser().updateProfile(
                                        new UserProfileChangeRequest.Builder()
                                                .setDisplayName(deviceLocalUserId)
                                                .build())));
        return email;
    }
}
