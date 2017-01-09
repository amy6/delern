package org.dasfoo.delern.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by katarina on 10/4/16.
 * Model class for card.
 */

@SuppressWarnings({"checkstyle:MemberName", "checkstyle:HiddenField"})
public class Card implements Parcelable {

    /**
     * Classes implementing the Parcelable interface must also have a non-null static
     * field called CREATOR of a type that implements the Parcelable.Creator interface.
     * https://developer.android.com/reference/android/os/Parcelable.html
     */
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

    /**
     * Gets reference to cards in Firebase.
     *
     * @return reference to cards.
     */
    @Exclude
    public static DatabaseReference getFirebaseCardsRef() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(CARDS);
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    /**
     * Method gets all cards to repeat calculating current time im milliseconds.
     *
     * @param deckId deck ID where to get cards.
     * @return query of cards to repeat.
     */
    @Exclude
    public static Query fetchCardsFromDeckToRepeat(final String deckId) {
        long time = System.currentTimeMillis();
        return getFirebaseCardsRef()
                .child(deckId)
                .orderByChild("repeatAt")
                .endAt(time);
    }

    /**
     * Gets one card to learn from deck.
     *
     * @param deckId deck ID.
     * @return query of card.
     */
    @Exclude
    public static Query fetchNextCardToRepeat(final String deckId) {
        return fetchCardsFromDeckToRepeat(deckId).limitToFirst(1);
    }

    /**
     * Creates new card in deck in Firebase.
     *
     * @param newCard card for writing to deck.
     * @param deckId deck ID where to create card.
     */
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

    /**
     * Returns query of card by ID of card.
     *
     * @param deckId deck ID where to look for a card.
     * @param cardId card ID
     * @return query of card.
     */
    @Exclude
    public static Query getCardById(final String deckId, final String cardId) {
        return getFirebaseCardsRef()
                .child(deckId)
                .orderByKey()
                .equalTo(cardId);
    }

    /**
     * Updates card using deck ID. Card ID is the same.
     *
     * @param card   new card
     * @param deckId deck ID where to update card.
     */
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    @Exclude
    public static void updateCard(final Card card, final String deckId) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + card.getcId(), card);
        getFirebaseCardsRef()
                .child(deckId)
                .updateChildren(childUpdates);
    }

    /**
     * Gets all cards from deck using deck ID.
     *
     * @param deckId deck ID for getting cards.
     * @return query of all cards in deck.
     */
    @Exclude
    public static Query fetchAllCardsForDeck(final String deckId) {
        return getFirebaseCardsRef()
                .child(deckId);
    }

    /**
     * Removes all cards from deck.
     *
     * @param deckId deck ID where to remove cards.
     */
    @Exclude
    public static void deleteCardsFromDeck(final String deckId) {
        getFirebaseCardsRef()
                .child(deckId).removeValue();
    }

    /**
     * Removes card from deck.
     *
     * @param deckId deck ID where to remove card.
     * @param card   card to remove
     */
    @Exclude
    public static void deleteCardFromDeck(final String deckId, final Card card) {
        getFirebaseCardsRef()
                .child(deckId)
                .child(card.getcId())
                .removeValue();
    }

    /**
     * Getter for card ID.
     *
     * @return id of card
     */
    @Exclude
    public String getcId() {
        return cId;
    }

    /**
     * Setter for card ID.
     *
     * @param cId id of card
     */
    @Exclude
    public void setcId(final String cId) {
        this.cId = cId;
    }

    /**
     * Getter for back side of card.
     *
     * @return back of card.
     */
    public String getBack() {
        return back;
    }

    /**
     * Setter for back side of card.
     *
     * @param backSide back of card.
     */
    public void setBack(final String backSide) {
        this.back = backSide;
    }

    /**
     * Getter for front side of card.
     *
     * @return front side of card
     */
    public String getFront() {
        return front;
    }

    /**
     * Setter for front side of card.
     *
     * @param frontSide front side of card.
     */
    public void setFront(final String frontSide) {
        this.front = frontSide;
    }

    /**
     * Getter for level of card. Level is used for repetition intervals.
     *
     * @return level of card.
     */
    public String getLevel() {
        return level;
    }

    /**
     * Setter for level of card. Level is used for repetition intervals.
     *
     * @param level level of card.
     */
    public void setLevel(final String level) {
        this.level = level;
    }

    /**
     * Getter for time im milliseconds when card should be repeated in the next time.
     *
     * @return time in milliseconds when to repeat card.
     */
    public long getRepeatAt() {
        return repeatAt;
    }

    /**
     * Setter for time im milliseconds when card should be repeated in the next time.
     *
     * @param repeatAt time in milliseconds when to repeat card.
     */
    public void setRepeatAt(final long repeatAt) {
        this.repeatAt = repeatAt;
    }

    /**
     * {@inheritDoc}
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
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(this.cId);
        dest.writeString(this.back);
        dest.writeString(this.front);
        dest.writeString(this.level);
        dest.writeLong(this.repeatAt);
    }

    /**
     * {@inheritDoc}
     */
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
