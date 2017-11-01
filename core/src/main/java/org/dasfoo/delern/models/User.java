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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by katarina on 10/12/16.
 * Model class for users.
 */
@SuppressWarnings({"checkstyle:MemberName", "checkstyle:HiddenField"})
public final class User extends Model {

    @Exclude
    private FirebaseDatabase mDatabase;

    private String name;
    private String photoUrl;

    /**
     * Create a new user to save to the database later.
     *
     * @param db Firebase database instance this user (and its child models) are bound to.
     */
    public User(final FirebaseDatabase db) {
        super(null, null);
        mDatabase = db;
    }

    /**
     * An empty constructor is required for Firebase deserialization.
     */
    private User() {
        super(null, null);
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
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }

    /**
     * {@inheritDoc}
     */
    @Exclude
    @Override
    public DatabaseReference getReference() {
        DatabaseReference reference = getChildReference(User.class, getKey());
        reference.keepSynced(true);
        return reference;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setReference(final DatabaseReference ref) {
        mDatabase = ref.getDatabase();
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
            return mDatabase.getReference().child("cards");
        }
        if (childClass == DeckAccess.class) {
            // DeckAccess has Deck key first and then User (which is built into DeckAccess), so
            // we also skip the key and do not keep them synced to save space.
            return mDatabase.getReference().child("deck_access");
        }
        if (childClass == View.class) {
            // Intentionally not keeping views synced to save space and bandwidth.
            return mDatabase.getReference().child("views").child(getKey());
        }

        if (childClass == Deck.class) {
            DatabaseReference reference = mDatabase.getReference().child("decks").child(getKey());
            reference.keepSynced(true);
            return reference;
        }
        if (childClass == ScheduledCard.class) {
            DatabaseReference reference = mDatabase.getReference().child("learning")
                    .child(getKey());
            reference.keepSynced(true);
            return reference;
        }
        if (childClass == FCMToken.class) {
            DatabaseReference reference = mDatabase.getReference().child("fcm")
                    .child(getKey());
            reference.keepSynced(true);
            return reference;
        }
        if (childClass == User.class) {
            // TODO(dotdoom): invalid level of child()
            return mDatabase.getReference().child("users");
        }

        return super.getChildReference(childClass);
    }

    /**
     * {@inheritDoc}
     */
    @Exclude
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        if (!name.equals(user.name)) {
            return false;
        }
        if (photoUrl != null) {
            return photoUrl.equals(user.photoUrl);
        }
        return user.photoUrl == null;
    }

    /**
     * {@inheritDoc}
     */
    @Exclude
    @Override
    public int hashCode() {
        int result = name.hashCode();
        if (photoUrl != null) {
            result = 31 * result + photoUrl.hashCode();
        }
        return result;
    }
}
