package org.dasfoo.delern.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by katarina on 10/4/16.
 */

public class Card implements Parcelable {
    private String uid;
    private String backside;
    private String frontside;

    public Card(){

    }

    public Card(String backSide, String frontSide) {
        this.backside = backSide;
        this.frontside = frontSide;
    }

    public Card(String uid, String backSide, String frontSide) {
        this.uid = uid;
        this.backside = backSide;
        this.frontside = frontSide;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBackSide() {
        return backside;
    }

    public void setBackSide(String backSide) {
        this.backside = backSide;
    }

    public String getFrontSide() {
        return frontside;
    }

    public void setFrontSide(String frontSide) {
        this.frontside = frontSide;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     * @see #CONTENTS_FILE_DESCRIPTOR
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.backside);
        dest.writeString(this.frontside);
    }

    @Override
    public String toString() {
        return "Card{" +
                "uid='" + uid + '\'' +
                ", backside='" + backside + '\'' +
                ", frontside='" + frontside + '\'' +
                '}';
    }
}
