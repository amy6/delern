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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;


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

    private String name;
    private String email;
    private String photoUrl;

    /**
     * An empty constructor is required for Firebase deserialization.
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
        setName(in.readString());
        setEmail(in.readString());
        setPhotoUrl(in.readString());
    }

    /**
     * Checks whether user is signed in.
     * It uses Firebase Auth to check whether user is signed in.
     *
     * @return true if user is signed in, false if not.
     */
    @Exclude
    public static boolean isSignedIn() {
        return getCurrentUser() != null;
    }

    /**
     * Gets current user using FirebaseAuth.
     *
     * @return returns current user.
     */
    @Exclude
    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        // TODO(refactoring): for sharing, should be actual key
        return getCurrentUser().getUid();
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
     * {@inheritDoc}
     */
    @Exclude
    @Override
    public <T> DatabaseReference getChildReference(final Class<T> childClass) {
        if (childClass == Deck.class) {
            DatabaseReference decks = FirebaseDatabase.getInstance().getReference("/decks");
            decks.keepSynced(true);
            return decks.child(getKey());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Exclude
    @Override
    public DatabaseReference getReference() {
        // TODO(refactoring): create Root model?
        DatabaseReference user = FirebaseDatabase.getInstance().getReference("/users")
                .child(getKey());
        user.keepSynced(true);
        return user;
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
        dest.writeString(getName());
        dest.writeString(getEmail());
        dest.writeString(getPhotoUrl());
    }
}
