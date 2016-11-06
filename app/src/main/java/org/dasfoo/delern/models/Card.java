package org.dasfoo.delern.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by katarina on 10/4/16.
 */

public class Card implements Parcelable {
    private String cId;
    private String back;
    private String front;
    private String level;
    private long repeatAt;

    public Card(){

    }

    public Card(String backSide, String frontSide) {
        this.back = backSide;
        this.front = frontSide;
    }

    public Card(String cId, String backSide, String frontSide) {
        this.cId = cId;
        this.back = backSide;
        this.front = frontSide;
    }

    protected Card(Parcel in) {
        cId = in.readString();
        back = in.readString();
        front = in.readString();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String backSide) {
        this.back = backSide;
    }

    public String getFront() {
        return front;
    }

    public void setFront(String frontSide) {
        this.front = frontSide;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public long getRepeatAt() {
        return repeatAt;
    }

    public void setRepeatAt(long repeatAt) {
        this.repeatAt = repeatAt;
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
        dest.writeString(this.cId);
        dest.writeString(this.back);
        dest.writeString(this.front);
    }

    @Override
    public String toString() {
        return "Card{" +
                "cId='" + cId + '\'' +
                ", back='" + back + '\'' +
                ", front='" + front + '\'' +
                '}';
    }
}
