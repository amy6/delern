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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * A class to handle authentication with Firebase.
 */
public final class Auth {
    private static final Logger LOGGER = LoggerFactory.getLogger(Auth.class);

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
        FirebaseAuth.getInstance().addAuthStateListener(
                (@NonNull final FirebaseAuth firebaseAuth) ->
                        setCurrentUser(firebaseAuth.getCurrentUser()));
    }

    /**
     * Sign in a Firebase user and populate the database.
     *
     * @param credential null for anonymous sign-in.
     * @param callback   invoked when sign-in is finished, either successfully or with failure.
     */
    public static void signIn(@Nullable final AuthCredential credential,
                              @Nullable final Consumer<User> callback) {
        Task<AuthResult> task;
        if (credential == null) {
            task = FirebaseAuth.getInstance().signInAnonymously();
        } else {
            task = FirebaseAuth.getInstance().signInWithCredential(credential);
        }
        if (callback != null) {
            task.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull final Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // OnAuthStateChange may fire too late, override right here.
                        setCurrentUser(task.getResult().getUser());
                        callback.accept(getCurrentUser());
                    } else {
                        LOGGER.error("Failed to sign in with {}", credential, task.getException());
                    }
                }
            });
        }
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

    private static void setCurrentUser(@Nullable final FirebaseUser user) {
        if (user == null) {
            Crashlytics.setUserIdentifier(null);
            // Clear out existing object.
            sCurrentUser.setKey(null);
            sCurrentUser.setName("Signed Out");
            sCurrentUser.setEmail(null);
            sCurrentUser.setPhotoUrl(null);
        } else {
            Crashlytics.setUserIdentifier(user.getUid());
            sCurrentUser.setKey(user.getUid());
            if (org.dasfoo.delern.BuildConfig.ENABLE_ANONYMOUS_SIGNIN) {
                // Anonymous users don't have any data, which means saving them to Firebase creates
                // an empty record, stripping the access and confusing the MainActivity. Fake it.
                sCurrentUser.setName("Anonymous User");
                sCurrentUser.setEmail("anonymous@example.com");
            } else {
                sCurrentUser.setName(user.getDisplayName());
                sCurrentUser.setEmail(user.getEmail());
                if (user.getPhotoUrl() == null) {
                    sCurrentUser.setPhotoUrl(null);
                } else {
                    sCurrentUser.setPhotoUrl(user.getPhotoUrl().toString());
                }
            }
            sCurrentUser.save();
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
