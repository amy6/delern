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

import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.dasfoo.delern.notifications.DelernFirebaseInstanceIdService;

/**
 * A class to handle authentication with Firebase.
 */
public final class Auth {
    // TODO(dotdoom): make it mCurrentUser, non-static methods, and create once: in SplashActivity
    private static User sCurrentUser;

    /**
     * Hide utility class default constructor.
     */
    private Auth() {
    }

    /**
     * Initialize a listener to set current user.
     *
     * @param db Firebase database instance to fetch current user for.
     */
    public static void initializeCurrentUser(final FirebaseDatabase db) {
        sCurrentUser = new User(db);

        // TODO(dotdoom): refactor into a non-static method?
        FirebaseAuth.getInstance().addAuthStateListener(auth ->
                setCurrentUser(auth.getCurrentUser()));
    }

    /**
     * Sign the user out using FirebaseAuth.getInstance().signOut().
     */
    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * Get signed in User.
     *
     * @return User model with all the fields set if signed in, or exists() false if isn't.
     */
    public static User getCurrentUser() {
        return sCurrentUser;
    }

    /**
     * Set FirebaseUser as the current signed in user. Except for situations where timing is
     * critical, the code should rely on auth listener built into this class.
     *
     * @param user FirebaseUser currently logged in.
     */
    public static void setCurrentUser(@Nullable final FirebaseUser user) {
        if (user == null) {
            Crashlytics.setUserIdentifier(null);
            // Clear out existing object.
            sCurrentUser.setKey(null);
            sCurrentUser.setName("Signed Out");
            sCurrentUser.setPhotoUrl(null);
        } else {
            Crashlytics.setUserIdentifier(user.getUid());
            sCurrentUser.setKey(user.getUid());
            if (user.isAnonymous()) {
                // Anonymous users don't have any data, which means saving them to Firebase creates
                // an empty record, stripping the access and confusing the MainActivity. Fake it.
                sCurrentUser.setName("Anonymous User");
            } else {
                sCurrentUser.setName(user.getDisplayName());
                if (user.getPhotoUrl() == null) {
                    sCurrentUser.setPhotoUrl(null);
                } else {
                    sCurrentUser.setPhotoUrl(user.getPhotoUrl().toString());
                }
            }
            sCurrentUser.save();
            DelernFirebaseInstanceIdService.saveCurrentToken();
        }
    }

    /**
     * Check if the user is signed in.
     *
     * @return true if the user is signed in (equal to getCurrentUser().exists()).
     */
    public static boolean isSignedIn() {
        return getCurrentUser().exists();
    }
}
