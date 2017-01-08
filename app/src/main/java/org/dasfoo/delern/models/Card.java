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

@SuppressWarnings({"checkstyle:MemberName", "checkstyle:HiddenField"})
public class Card implements Parcelable {

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(final Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(final int size) {
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

    /**
     * The empty constructor is required for Firebase de-serialization.
     */
    public Card() {
        // This constructor is intentionally left empty.
    }

    protected Card(final Parcel in) {
        cId = in.readString();
        back = in.readString();
        front = in.readString();
        level = in.readString();
        repeatAt = in.readLong();
    }

    @Exclude
    public static DatabaseReference getFirebaseCardsRef() {
        DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference()
                .child(CARDS);
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    @Exclude
    public static Query fetchCardsFromDeckToRepeat(final String deckId) {
        long time = System.currentTimeMillis();
        Log.v(TAG, String.valueOf(time));

        return getFirebaseCardsRef()
                .child(deckId)
                .orderByChild("repeatAt")
                .endAt(time);
    }

    @Exclude
    public static void createNewCard(final Card newCard, final String deckId) {
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
    public static Query getCardById(final String deckId, final String cardId) {
        return getFirebaseCardsRef()
                .child(deckId)
                .orderByKey()
                .equalTo(cardId);
    }

    @SuppressWarnings("PMD.UseConcurrentHashMap")
    @Exclude
    public static void updateCard(final Card card, final String deckId) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + card.getcId(), card);
        getFirebaseCardsRef()
                .child(deckId)
                .updateChildren(childUpdates);
    }

    @Exclude
    public static Query fetchAllCardsForDeck(final String deckId) {
        return getFirebaseCardsRef()
                .child(deckId);
    }

    @Exclude
    public static void deleteCardsFromDeck(final String deckId) {
        getFirebaseCardsRef()
                .child(deckId).removeValue();
    }

    @Exclude
    public static void deleteCardFromDeck(final String deckId, final Card card) {
        getFirebaseCardsRef()
                .child(deckId)
                .child(card.getcId())
                .removeValue();
    }

    @Exclude
    public String getcId() {
        return cId;
    }

    @Exclude
    public void setcId(final String cId) {
        this.cId = cId;
    }

    public String getBack() {
        return back;
    }

    public void setBack(final String backSide) {
        this.back = backSide;
    }

    public String getFront() {
        return front;
    }

    public void setFront(final String frontSide) {
        this.front = frontSide;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(final String level) {
        this.level = level;
    }

    public long getRepeatAt() {
        return repeatAt;
    }

    public void setRepeatAt(final long repeatAt) {
        this.repeatAt = repeatAt;
    }

    /** {@inheritDoc} */
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
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(this.cId);
        dest.writeString(this.back);
        dest.writeString(this.front);
        dest.writeString(this.level);
        dest.writeLong(this.repeatAt);
    }

    @Override
    public String toString() {
        return "Card{" +
                "cId='" + cId + '\'' +
                ", back='" + back + '\'' +
                ", front='" + front + '\'' +
                ", level='" + level + '\'' +
                ", repeatAt=" + repeatAt +
                '}';
    }
}
