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
 * Created by katarina on 10/11/16.
 */

@SuppressWarnings({"checkstyle:MemberName", "checkstyle:HiddenField"})
public class Deck implements Parcelable {

    /**
     * Classes implementing the Parcelable interface must also have a non-null static
     * field called CREATOR of a type that implements the Parcelable.Creator interface.
     * https://developer.android.com/reference/android/os/Parcelable.html
     */
    public static final Creator<Deck> CREATOR = new Creator<Deck>() {
        @Override
        public Deck createFromParcel(final Parcel in) {
            return new Deck(in);
        }

        @Override
        public Deck[] newArray(final int size) {
            return new Deck[size];
        }
    };

    @Exclude
    private static final String TAG = LogUtil.tagFor(Deck.class);
    @Exclude
    private static final String DECKS = "decks";

    @Exclude
    private String dId;
    private String name;
    private String deckType;
    private String category;
    private boolean accepted;

    /**
     * The empty constructor is required for Firebase de-serialization.
     */
    public Deck() {
        // This constructor is intentionally left empty.
    }

    /**
     * Constructor with parameter name of deck.
     *
     * @param name name of deck.
     */
    public Deck(final String name, final String dType, final boolean userAccepted) {
        this.name = name;
        this.deckType = dType;
        this.accepted = userAccepted;
    }

    protected Deck(final Parcel in) {
        dId = in.readString();
        name = in.readString();
        deckType = in.readString();
        category = in.readString();
        // Reading and writing boolean for parceable
        // http://stackoverflow.com/questions/6201311/how-to-read-write-a-boolean-when-implementing-the-parcelable-interface
        accepted = in.readByte() != 0;
    }

    /**
     * Gets reference to decks in Firebase.
     *
     * @return firebase database reference.
     */
    @Exclude
    public static DatabaseReference getFirebaseDecksRef() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(DECKS).child(User.getCurrentUser().getUid());
        //databaseReference.keepSynced(true);
        return databaseReference;
    }

    /**
     * Returns decks of user.
     *
     * @return query decks of user.
     */
    @Exclude
    public static Query getUsersDecks() {
        if (User.getCurrentUser() == null) {
            Log.v(TAG, "User is not signed in");
            return null;
        } else {
            return getFirebaseDecksRef();
        }

    }

    /**
     * Creates new deck in Firebase.
     *
     * @param deck new deck.
     * @return key of created deck.
     */
    @Exclude
    public static String createNewDeck(final Deck deck) {
        DatabaseReference reference = getFirebaseDecksRef().push();
        String key = reference.getKey();
        // Write deckAccess
        DeckAccess deckAccess = new DeckAccess("owner");
        DeckAccess.writeDeckAccessToFB(deckAccess, key);

        reference.setValue(deck);
        return reference.getKey();
    }

    /**
     * Remove deck by ID.
     *
     * @param deckId deck ID for removing.
     */
    @Exclude
    public static void deleteDeck(final String deckId) {
        // Delete deck
        getFirebaseDecksRef().child(deckId).removeValue();
        // Delete cards. It must be run before deleting deckAccess. If user is not owner of cards,
        // they won't be deleted.
        Card.deleteCardsFromDeck(deckId);
        DeckAccess.deleteDeckAccess(deckId);
        ScheduledCard.deleteCardsByDeckId(deckId);
        View.removeViewsFromDeck(deckId);
    }

    /**
     * Renames deck.
     *
     * @param deck deck to rename.
     */
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    @Exclude
    public static void updateDeck(final Deck deck) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + deck.getdId(), deck);
        getFirebaseDecksRef().updateChildren(childUpdates);
    }

    /**
     * Getter for ID of deck.
     *
     * @return ID of deck.
     */
    @Exclude
    public String getdId() {
        return dId;
    }

    /**
     * Setter for deck ID.
     *
     * @param dId ID of deck.
     */
    @Exclude
    public void setdId(final String dId) {
        this.dId = dId;
    }

    /**
     * Getter for name of deck.
     *
     * @return name of deck.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name of deck.
     *
     * @param name name of deck.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Getter for cards type in deck.
     *
     * @return type of cards in deck.
     */
    public String getDeckType() {
        if (deckType == null) {
            return DeckType.BASIC.name().toLowerCase();
        }
        return deckType;
    }

    /**
     * Setter for cards type in deck.
     *
     * @param deckType type of cards in deck.
     */
    public void setDeckType(final String deckType) {
        this.deckType = deckType;
    }

    /**
     * Getter for category of deck.
     *
     * @return category of deck.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Setter for category of deck.
     *
     * @param category category of deck.
     */
    public void setCategory(final String category) {
        this.category = category;
    }

    /**
     * @return
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * User can accept shared deck or not.
     *
     * @param accepted true of false
     */
    public void setAccepted(final boolean accepted) {
        this.accepted = accepted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Deck{" +
                "dId='" + dId + '\'' +
                ", name='" + name + '\'' +
                ", deckType='" + deckType + '\'' +
                ", categoty='" + category + '\'' +
                ", accepted='" + accepted + '\'' +
                '}';
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
    public void writeToParcel(final Parcel parcel, final int i) {
        parcel.writeString(this.dId);
        parcel.writeString(this.name);
        parcel.writeString(this.deckType);
        parcel.writeString(this.category);
        parcel.writeByte((byte) (accepted ? 1 : 0));
    }
}
