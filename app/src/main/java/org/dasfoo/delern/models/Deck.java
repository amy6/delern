package org.dasfoo.delern.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.dasfoo.delern.models.Card.getFirebaseCardsRef;

/**
 * Created by katarina on 10/11/16.
 */

public class Deck {
    @Exclude
    private static final String DECKS = "decks";

    @Exclude
    private String uid;
    private String name;

    public Deck() {

    }

    public Deck(String name) {
        this.name = name;
    }

    @Exclude
    public String getUid() {
        return uid;
    }

    @Exclude
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Deck{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Exclude
    public static DatabaseReference getFirebaseDecksRef() {
        return FirebaseDatabase.getInstance().getReference().child(DECKS);
    }

    @Exclude
    public static Query getUsersDecks() {
        return getFirebaseDecksRef()
                .orderByChild("user")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    @Exclude
    public static String createNewDeck(Deck deck) {
        DatabaseReference reference = getFirebaseDecksRef().push();
        reference.setValue(deck);
        String key = reference.getKey();
        addUserToDeck(key);
        return key;
    }

    @Exclude
    public static void deleteDeck(String deckId) {
        // Remove deck
        DatabaseReference reference = getFirebaseDecksRef();
        reference.child(deckId).removeValue();
        Card.deleteCardsFromDeck(deckId);
    }

    @Exclude
    public static void renameDeck(Deck deck) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + deck.getUid(), deck);
        getFirebaseDecksRef().updateChildren(childUpdates);
        addUserToDeck(deck.getUid());
    }

    @Exclude
    private static void addUserToDeck(String deckKey) {
        // Add user to deck
        getFirebaseDecksRef()
                .child(deckKey)
                .child("user")
                .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }
}
