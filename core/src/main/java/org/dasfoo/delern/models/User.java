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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import org.dasfoo.delern.models.helpers.MultiWrite;

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

    private static FirebaseDatabase sDatabase;

    private String name;
    private String email;
    private String photoUrl;

    /**
     * Create a new user to save to the database later.
     */
    public User() {
        super(null, null);
    }

    /**
     * Parcelable deserializer.
     *
     * @param in parcel.
     */
    protected User(final Parcel in) {
        super(null, in.readString());
        setName(in.readString());
        setEmail(in.readString());
        setPhotoUrl(in.readString());
    }

    /**
     * Get database reference, enable persistence, set necessary listeners.
     *
     * @param persistenceEnabled enable persistence (only available on certain platforms).
     */
    public static void initializeDatabase(final boolean persistenceEnabled) {
        sDatabase = FirebaseDatabase.getInstance();

        /* Firebase apps automatically handle temporary network interruptions. Cached data will
        still be available while offline and your writes will be resent when network connectivity is
        recovered. Enabling disk persistence allows our app to also keep all of its state even after
        an app restart.
        https://firebase.google.com/docs/database/android/offline-capabilities */
        sDatabase.setPersistenceEnabled(persistenceEnabled);

        MultiWrite.initializeOfflineListener(sDatabase);
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
