package org.dasfoo.delern.models;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
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
public class Deck {
    @Exclude
    private static final String TAG = LogUtil.tagFor(Deck.class);
    @Exclude
    private static final String DECKS = "decks";
    @Exclude
    private static final String USER = "user";

    @Exclude
    private String dId;
    private String name;

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
    public Deck(final String name) {
        this.name = name;
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

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Deck{" +
                "dId='" + dId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    /**
     * Gets reference to decks in Firebase.
     *
     * @return firebase database reference.
     */
    @Exclude
    public static DatabaseReference getFirebaseDecksRef() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(DECKS);
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    /**
     * Returns decks of user.
     *
     * @return query decks of user.
     */
    @Exclude
    public static Query getUsersDecks() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Log.v(TAG, "User is not signed in");
            return null;
        } else {
            return getFirebaseDecksRef()
                    .orderByChild(USER)
                    .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
        reference.setValue(deck);
        String key = reference.getKey();
        addUserToDeck(key);
        return key;
    }

    /**
     * Remove deck by ID.
     *
     * @param deckId deck ID for removing.
     */
    @Exclude
    public static void deleteDeck(final String deckId) {
        // Remove deck
        DatabaseReference reference = getFirebaseDecksRef();
        reference.child(deckId).removeValue();
        Card.deleteCardsFromDeck(deckId);
    }

    /**
     * Renames deck.
     *
     * @param deck deck to rename.
     */
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    @Exclude
    public static void renameDeck(final Deck deck) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + deck.getdId(), deck);
        getFirebaseDecksRef().updateChildren(childUpdates);
        addUserToDeck(deck.getdId());
    }

    @Exclude
    private static void addUserToDeck(final String deckKey) {
        // Add user to deck
        getFirebaseDecksRef()
                .child(deckKey)
                .child(USER)
                .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }
}
