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
import android.support.annotation.Nullable;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Parcelable wrapper.
 */
public class ParcelableUser implements Parcelable {

    /**
     * Classes implementing the Parcelable interface must also have a non-null static
     * field called CREATOR of a type that implements the Parcelable.Creator interface.
     * https://developer.android.com/reference/android/os/Parcelable.html
     */
    public static final Parcelable.Creator<ParcelableUser> CREATOR =
            new Parcelable.Creator<ParcelableUser>() {
                @Override
                public ParcelableUser createFromParcel(final Parcel in) {
                    return new ParcelableUser(in);
                }

                @Override
                public ParcelableUser[] newArray(final int size) {
                    return new ParcelableUser[size];
                }
            };
    private final User mUser;

    /**
     * Create a Parcelable wrapper around User.
     *
     * @param u User.
     */
    public ParcelableUser(final User u) {
        mUser = u;
    }

    /**
     * Parcelable deserializer.
     *
     * @param in parcel.
     */
    protected ParcelableUser(final Parcel in) {
        mUser = new User(FirebaseDatabase.getInstance());
        mUser.setKey(in.readString());
        mUser.setName(in.readString());
        mUser.setEmail(in.readString());
        mUser.setPhotoUrl(in.readString());
    }

    /**
     * Cast parcel to object.
     *
     * @param parcel getParcelableExtra() / readParcelable() return value.
     * @return casted object.
     */
    @Nullable
    public static User get(final Object parcel) {
        if (parcel == null) {
            return null;
        }
        return ((ParcelableUser) parcel).mUser;
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
        dest.writeString(mUser.getKey());
        dest.writeString(mUser.getName());
        dest.writeString(mUser.getEmail());
        dest.writeString(mUser.getPhotoUrl());
    }
}
