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
public class ParcelableCard implements Parcelable {

    /**
     * Classes implementing the Parcelable interface must also have a non-null static
     * field called CREATOR of a type that implements the Parcelable.Creator interface.
     * https://developer.android.com/reference/android/os/Parcelable.html
     */
    public static final Parcelable.Creator<ParcelableCard> CREATOR =
            new Parcelable.Creator<ParcelableCard>() {
                @Override
                public ParcelableCard createFromParcel(final Parcel in) {
                    return new ParcelableCard(in);
                }

                @Override
                public ParcelableCard[] newArray(final int size) {
                    return new ParcelableCard[size];
                }
            };

    private final Card mCard;

    /**
     * Create a Parcelable wrapper around User.
     *
     * @param c Card.
     */
    public ParcelableCard(final Card c) {
        mCard = c;
    }

    /**
     * Parcelable deserializer.
     *
     * @param in parcel.
     */
    // When running instrumented tests (2 APK in a single process) Thread class loader is empty.
    @SuppressWarnings("PMD.UseProperClassLoader")
    protected ParcelableCard(final Parcel in) {
        mCard = new Card(ParcelableDeck.get(in.readParcelable(
                ParcelableCard.class.getClassLoader())));
        mCard.setKey(in.readString());
        mCard.setBack(in.readString());
        mCard.setFront(in.readString());
    }

    /**
     * Cast parcel to object.
     *
     * @param parcel getParcelableExtra() / readParcelable() return value.
     * @return casted object.
     */
    @Nullable
    public static Card get(final Object parcel) {
        if (parcel == null) {
            return null;
        }
        return ((ParcelableCard) parcel).mCard;
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
        parcel.writeParcelable(new ParcelableDeck(mCard.getDeck()), flags);
        parcel.writeString(mCard.getKey());
        parcel.writeString(mCard.getBack());
        parcel.writeString(mCard.getFront());
    }
}
