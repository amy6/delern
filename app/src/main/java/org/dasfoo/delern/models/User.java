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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import org.dasfoo.delern.listeners.AbstractDataAvailableListener;


/**
 * Created by katarina on 10/12/16.
 * Model class for users.
 */
@SuppressWarnings({"checkstyle:MemberName", "checkstyle:HiddenField"})
public final class User extends AbstractModel implements Parcelable {

    /**
     * Classes implementing the Parcelable interface must also have a non-null static
     * field called CREATOR of a type that implements the Parcelable.Creator interface.
     * https://developer.android.com/reference/android/os/Parcelable.html
     */
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(final Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(final int size) {
            return new User[size];
        }
    };
    
    private static final User CURRENT_USER = new User();

    private static FirebaseDatabase sDatabase;

    private String name;
    private String email;
    private String photoUrl;

    /**
     * Create a new user to save to the database later.
     */
    public User() {
        super(null);
    }

    /**
     * Parcelable deserializer.
     * @param in parcel.
     */
    protected User(final Parcel in) {
        super(null);
        setKey(in.readString());
        setName(in.readString());
        setEmail(in.readString());
        setPhotoUrl(in.readString());
    }

    /**
     * Get database reference, enable persistence, set necessary listeners.
     */
    public static void initializeDatabase() {
        sDatabase = FirebaseDatabase.getInstance();

        /* Firebase apps automatically handle temporary network interruptions. Cached data will
        still be available while offline and your writes will be resent when network connectivity is
        recovered. Enabling disk persistence allows our app to also keep all of its state even after
        an app restart.
        https://firebase.google.com/docs/database/android/offline-capabilities */
        sDatabase.setPersistenceEnabled(true);

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Crashlytics.setUserIdentifier(null);
                    // Clear out existing object.
                    CURRENT_USER.setKey(null);
                    CURRENT_USER.setName("Signed Out");
                    CURRENT_USER.setEmail(null);
                    CURRENT_USER.setPhotoUrl(null);
                } else {
                    setCurrentUser(user);
                }
            }
        });
    }

    private static void setCurrentUser(final FirebaseUser user) {
        Crashlytics.setUserIdentifier(user.getUid());
        CURRENT_USER.setKey(user.getUid());
        CURRENT_USER.setName(user.getDisplayName());
        CURRENT_USER.setEmail(user.getEmail());
        if (user.getPhotoUrl() == null) {
            CURRENT_USER.setPhotoUrl(null);
        } else {
            CURRENT_USER.setPhotoUrl(user.getPhotoUrl().toString());
        }
        CURRENT_USER.save(null);
    }

    /**
     * Getter for name of user.
     *
     * @return name of user.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name of user.
     *
     * @param name name of user.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Getter of email of user.
     *
     * @return email of user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter for email of user.
     *
     * @param email email of user.
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Getter for photo url of user.
     *
     * @return photo url of user.
     */
    public String getPhotoUrl() {
        return photoUrl;
    }

    /**
     * Setter of photo url of user.
     *
     * @param photoUrl photo url.
     */
    public void setPhotoUrl(final String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "User{" + super.toString() +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }

    /**
     * Sign in a Firebase user and populate the database.
     * @param credential null for anonymous sign-in.
     * @param callback   invoked when sign-in is finished, either successfully or with failure.
     */
    public static void signIn(@Nullable final AuthCredential credential,
                              @Nullable final AbstractDataAvailableListener<User> callback) {
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
                        callback.onData(getCurrentUser());
                    } else {
                        callback.onError(task.getException());
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
     * @return User model with all the fields set if signed in, or exists() false if isn't.
     */
    public static User getCurrentUser() {

        return CURRENT_USER;
    }

    /**
     * CHeck if the user is signed in.
     * @return true if the user is signed in (equal to getCurrentUser().exists()).
     */
    public static boolean isSignedIn() {
        return getCurrentUser().exists();
    }

    /**
     * {@inheritDoc}
     */
    @Exclude
    @Override
    public DatabaseReference getReference() {
        DatabaseReference reference = sDatabase.getReference("users").child(getKey());
        reference.keepSynced(true);
        return reference;
    }

    /**
     * {@inheritDoc}
     */
    @Exclude
    @Override
    public <T> DatabaseReference getChildReference(final Class<T> childClass) {
        if (childClass == Card.class) {
            // We skip User key in Card (they belong directly to decks), and therefore do not keep
            // them synced at this level.
            return sDatabase.getReference().child("cards");
        }
        if (childClass == DeckAccess.class) {
            // DeckAccess has Deck key first and then User (which is built into DeckAccess), so
            // we also skip the key and do not keep them synced to save space.
            return sDatabase.getReference().child("deck_access");
        }
        if (childClass == View.class) {
            // Intentionally not keeping views synced to save space and bandwidth.
            return sDatabase.getReference().child("views").child(getKey());
        }

        if (childClass == Deck.class) {
            DatabaseReference reference = sDatabase.getReference().child("decks").child(getKey());
            reference.keepSynced(true);
            return reference;
        }
        if (childClass == ScheduledCard.class) {
            DatabaseReference reference = sDatabase.getReference().child("learning")
                    .child(getKey());
            reference.keepSynced(true);
            return reference;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(getKey());
        dest.writeString(getName());
        dest.writeString(getEmail());
        dest.writeString(getPhotoUrl());
    }
}
