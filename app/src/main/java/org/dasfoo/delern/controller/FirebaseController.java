package org.dasfoo.delern.controller;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by katarina on 10/19/16.
 */
public final class FirebaseController {
    /**
     * Class information for logging.
     */
    private static final String TAG = LogUtil.tagFor(FirebaseController.class);
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
                .orderByChild("user")
                .equalTo(mFirebaseAuth.getCurrentUser().getUid());
    }

    public Query getCardsFromDeckToRepeat(String deckId) {
        long time = System.currentTimeMillis();
        Log.v(TAG, String.valueOf(time));

        return getFirebaseCardsRef()
                .child(deckId)
                .orderByChild("repeatAt")
                .endAt(time);
    }

    public void createNewCard(Card newCard, String deckId) {
        String cardKey = getFirebaseCardsRef()
                .child(deckId)
                .push()
                .getKey();
        getFirebaseCardsRef()
                .child(deckId)
                .child(cardKey)
                .setValue(newCard);
    }

    public void createNewDeck(Deck deck) {
        DatabaseReference reference = getFirebaseDecksRef().push();
        reference.setValue(deck);
        String key = reference.getKey();
        addUserToDeck(key);
    }

    public void updateCard(Card card, String deckId) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/"+ card.getcId(), card);
        getFirebaseCardsRef()
                .child(deckId)
                .updateChildren(childUpdates);
    }

    private void addUserToDeck(String deckKey) {
        // Add user to deck
        getFirebaseDecksRef()
                .child(deckKey)
                .child("user")
                .setValue(mFirebaseAuth.getCurrentUser().getUid());
    }
}
