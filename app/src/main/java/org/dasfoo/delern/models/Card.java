package org.dasfoo.delern.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.dasfoo.delern.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by katarina on 10/4/16.
 */

public class Card implements Parcelable {

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
    @Exclude
    private static final String CARDS = "cards";
    private static final String TAG = LogUtil.tagFor(Card.class);
    @Exclude
    private String cId;
    private String back;
    private String front;
    private String level;
    private long repeatAt;

    public Card() {

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

    @Exclude
    public static DatabaseReference getFirebaseCardsRef() {
        return FirebaseDatabase.getInstance().getReference().child(CARDS);
    }

    @Exclude
    public static Query fetchCardsFromDeckToRepeat(String deckId) {
        long time = System.currentTimeMillis();
        Log.v(TAG, String.valueOf(time));

        return getFirebaseCardsRef()
                .child(deckId)
                .orderByChild("repeatAt")
                .endAt(time);
    }

    @Exclude
    public static void createNewCard(Card newCard, String deckId) {
        String cardKey = getFirebaseCardsRef()
                .child(deckId)
                .push()
                .getKey();
        getFirebaseCardsRef()
                .child(deckId)
                .child(cardKey)
                .setValue(newCard);
    }

    @Exclude
    public static void updateCard(Card card, String deckId) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + card.getcId(), card);
        getFirebaseCardsRef()
                .child(deckId)
                .updateChildren(childUpdates);
    }

    @Exclude
    public static Query fetchAllCardsForDeck(String deckId) {
        return getFirebaseCardsRef()
                .child(deckId);
    }

    @Exclude
    public static void deleteCardsFromDeck(String deckId) {
        getFirebaseCardsRef()
                .child(deckId).removeValue();
    }

    @Exclude
    public String getcId() {
        return cId;
    }

    @Exclude
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
