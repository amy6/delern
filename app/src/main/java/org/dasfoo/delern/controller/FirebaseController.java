package org.dasfoo.delern.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.dasfoo.delern.models.Card;

/**
 * Created by katarina on 10/19/16.
 */
public final class FirebaseController {
    private static final String TAG = FirebaseController.class.getSimpleName();
    private static final String DESKTOPS = "desktops";
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

    public DatabaseReference getFirebaseRefFromUrl(String url) {
        return FirebaseDatabase.getInstance().getReferenceFromUrl(url);
    }

    public DatabaseReference getFirebaseDesktopRef() {
        return mFirebaseDatabaseReference.child(USERS).child(mFirebaseAuth.getCurrentUser().getUid()).child(DESKTOPS);
    }

    public DatabaseReference getFirebaseUsersRef() {
        return mFirebaseDatabaseReference.child(USERS);
    }

    public FirebaseAuth getFirebaseAuth() {
        return mFirebaseAuth;
    }


    public DatabaseReference getCardsRefFromDesktopUrl(String url) {
        return getFirebaseRefFromUrl(url).child(CARDS);
    }

    public void writeCardToDesktop(Card newCard, String fbPath) {
        FirebaseDatabase.getInstance().getReferenceFromUrl(fbPath)
                .child(CARDS)
                .push()
                .setValue(newCard);
    }

}
