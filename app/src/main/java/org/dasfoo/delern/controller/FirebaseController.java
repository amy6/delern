package org.dasfoo.delern.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Deck;

/**
 * Created by katarina on 10/19/16.
 */
public final class FirebaseController {
    private static final String TAG = FirebaseController.class.getSimpleName();
    private static final String DECKS = "decks";
    private static final String USERS = "users";
    private static final String CARDS = "cards";

    private static FirebaseController ourInstance;

    private static FirebaseAuth mFirebaseAuth;

    // Firebase realtime database instance variables
    private static DatabaseReference mFirebaseDatabaseReference;

    private FirebaseController() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Instance
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static FirebaseController getInstance() {
        if (ourInstance != null) {
            return ourInstance;
        }
        ourInstance = new FirebaseController();
        return ourInstance;
    }

    public FirebaseAuth getFirebaseAuth() {
        return mFirebaseAuth;
    }

    public DatabaseReference getFirebaseDecksRef() {
        return mFirebaseDatabaseReference.child(DECKS);
    }

    public DatabaseReference getFirebaseUsersRef() {
        return mFirebaseDatabaseReference.child(USERS);
    }

    public DatabaseReference getFirebaseCardsRef() {
        return mFirebaseDatabaseReference.child(CARDS);
    }

    public Query getUsersDecks() {
        return getFirebaseDecksRef()
                .orderByChild("users/" + (mFirebaseAuth.getCurrentUser().getUid()))
                .equalTo("true");
    }

    public Query getCardsFromDeck(String deckId) {
        return getFirebaseCardsRef()
                .orderByChild(DECKS + "/" + deckId)
                .equalTo(true);
    }

    /**
     * Creates new Card in Firebase.
     *
     * @param newCard model of card
     * @return key of record
     */
    public String createCard(Card newCard, String fbPath) {
        String cardKey = getFirebaseCardsRef()
                .push()
                .getKey();
        getFirebaseCardsRef()
                .child(cardKey)
                .setValue(newCard);
        // Add deck to card
        getFirebaseCardsRef()
                .child(cardKey)
                .child(DECKS)
                .child(fbPath)
                .setValue(true);
        return cardKey;
    }

    public void writeCardToDesktop(Card newCard, String deckId) {
        String cardKey = createCard(newCard, deckId);
        getFirebaseDecksRef()
                .child(deckId)
                .child(CARDS)
                .child(cardKey)
                .setValue(true);
    }

    public void createNewDeck(Deck deck) {
        DatabaseReference reference = getFirebaseDecksRef().push();
        reference.setValue(deck);
        String key = reference.getKey();
        addDeckToUser(key);
        addUserToDeck(key);
    }

    public void addDeckToUser(String deckKey) {
        mFirebaseDatabaseReference.child(USERS)
                .child(mFirebaseAuth.getCurrentUser().getUid())
                .child(DECKS)
                .child(deckKey)
                .setValue(true);
    }

    public void addUserToDeck(String deckKey) {
        // Add user to deck
        getFirebaseDecksRef()
                .child(deckKey)
                .child(USERS)
                .child(mFirebaseAuth.getCurrentUser().getUid())
                .setValue("true");
    }
}
