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
    private Object createdAt;

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
        //createdAt = in.readLong();
    }

    /**
     * Gets reference to cards in Firebase.
     *
     * @return reference to cards.
     */
    @Exclude
    public static DatabaseReference getFirebaseCardsRef() {
        return FirebaseDatabase.getInstance().getReference()
                .child(CARDS);
        // TODO(ksheremet): keep synced only cards of current user (after changing FB)
        //databaseReference.keepSynced(true);
    }

    /**
     * Creates new card in deck in Firebase.
     *
     * @param newCard card for writing to deck.
     * @param deckId  deck ID where to create card.
     */
    @Exclude
    public static void createNewCard(final Card newCard, final String deckId,
                                     final ScheduledCard scheduledCard) {
        String cardKey = getFirebaseCardsRef()
                .child(deckId)
                .push()
                .getKey();
        getFirebaseCardsRef()
                .child(deckId)
                .child(cardKey)
                .setValue(newCard);

        ScheduledCard.writeScheduleForCard(deckId, cardKey, scheduledCard);
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
        childUpdates.put("front", card.getFront());
        childUpdates.put("back", card.getBack());
        getFirebaseCardsRef()
                .child(deckId)
                .child(card.getcId())
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
        View.deleteViewById(deckId, card.getcId());
        ScheduledCard.deleteCardbyId(deckId, card.getcId());
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
     * Getter for time im milliseconds when card should be repeated in the next time.
     *
     * @return time in milliseconds when to repeat card.
     */
    public Object getCreatedAt() {
        return createdAt;
    }

    /**
     * Setter for time im milliseconds when card should be repeated in the next time.
     *
     * @param createdAt time in milliseconds when to repeat card.
     */
    public void setCreatedAt(final Object createdAt) {
        this.createdAt = createdAt;
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
        //dest.writeString(this.createdAt);
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
                ", createdAt=" + createdAt +
                '}';
    }
}
