package org.dasfoo.delern.controller;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.User;

import java.util.ArrayList;
import java.util.List;

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

    private static FirebaseUser mFirebaseUser;
    // Firebase realtime database instance variables
    private static DatabaseReference mFirebaseDatabaseReference;

    public static FirebaseController getInstance() {
        if (ourInstance != null) {
            return ourInstance;
        }
       return new FirebaseController();
    }

    private FirebaseController() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

    }

    public DatabaseReference getFirebaseRefFromUrl(String url) {
        return FirebaseDatabase.getInstance().getReferenceFromUrl(url);
    }

    public DatabaseReference getFirebaseDesktopRef() {
        return mFirebaseDatabaseReference.child(USERS).child(mFirebaseUser.getUid()).child(DESKTOPS);
    }

    public DatabaseReference getFirebaseUsersRef(){
        return mFirebaseDatabaseReference.child(USERS);
    }

    public FirebaseAuth getmFirebaseAuth() {
        return mFirebaseAuth;
    }

    public FirebaseUser getmFirebaseUser() {
        return mFirebaseUser;
    }

    public DatabaseReference getCardsRefFromDesktopUrl (String url) {
        return getFirebaseRefFromUrl(url).child(CARDS);
    }

    public void writeCardToDesktop(Card newCard, String fbPath) {
        FirebaseDatabase.getInstance().getReferenceFromUrl(fbPath)
                .child(CARDS)
                .push()
                .setValue(newCard);
    }

}
