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

/**
 * Parcelable wrapper.
 */
public class ParcelableDeck implements Parcelable {

    /**
     * Classes implementing the Parcelable interface must also have a non-null static
     * field called CREATOR of a type that implements the Parcelable.Creator interface.
     * https://developer.android.com/reference/android/os/Parcelable.html
     */
    public static final Parcelable.Creator<ParcelableDeck> CREATOR =
            new Parcelable.Creator<ParcelableDeck>() {
                @Override
                public ParcelableDeck createFromParcel(final Parcel in) {
                    return new ParcelableDeck(in);
                }

                @Override
                public ParcelableDeck[] newArray(final int size) {
                    return new ParcelableDeck[size];
                }
            };

    private final Deck mDeck;

    /**
     * Create a Parcelable wrapper around User.
     *
     * @param d Deck.
     */
    public ParcelableDeck(final Deck d) {
        mDeck = d;
    }

    /**
     * Parcelable deserializer.
     *
     * @param in parcel.
     */
    protected ParcelableDeck(final Parcel in) {
        mDeck = new Deck(ParcelableUser.get(in.readParcelable(
                Thread.currentThread().getContextClassLoader())));
        mDeck.setKey(in.readString());
        mDeck.setName(in.readString());
        mDeck.setDeckType(in.readString());
        mDeck.setCategory(in.readString());
        mDeck.setLastSyncAt(in.readLong());
        // Reading and writing boolean for parcelable
        // https://goo.gl/PLRLWY
        mDeck.setAccepted(in.readByte() != 0);
        mDeck.setMarkdown(in.readByte() != 0);
    }

    /**
     * Cast parcel to object.
     *
     * @param parcel getParcelableExtra() / readParcelable() return value.
     * @return casted object.
     */
    @Nullable
    public static Deck get(final Object parcel) {
        if (parcel == null) {
            return null;
        }
        return ((ParcelableDeck) parcel).mDeck;
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
    public void writeToParcel(final Parcel parcel, final int flags) {
        parcel.writeParcelable(new ParcelableUser(mDeck.getUser()), flags);
        parcel.writeString(mDeck.getKey());
        parcel.writeString(mDeck.getName());
        parcel.writeString(mDeck.getDeckType());
        parcel.writeString(mDeck.getCategory());
        parcel.writeLong(mDeck.getLastSyncAt());
        if (mDeck.isAccepted()) {
            parcel.writeByte((byte) 1);
        } else {
            parcel.writeByte((byte) 0);
        }
        if (mDeck.isMarkdown()) {
            parcel.writeByte((byte) 1);
        } else {
            parcel.writeByte((byte) 0);
        }
    }
}
