package org.dasfoo.delern.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import org.dasfoo.delern.viewholders.DeckViewHolder;

/**
 * Created by katarina on 2/22/17.
 */

public class DeckAccess {
    @Exclude
    private static final String DECK_ACCESS = "deck_access";

    private String access;

    public DeckAccess(final String access) {
        this.access = access;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(final String access) {
        this.access = access;
    }

    @Exclude
    public static DatabaseReference getFirebaseDeckAccessRef(final String deckId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(DECK_ACCESS).child(deckId).child(User.getCurrentUser().getUid());
        //databaseReference.keepSynced(true);
        return databaseReference;
    }

    @Exclude
    public static void writeDeckAccessToFB(final DeckAccess access, final String deckId) {
        DatabaseReference databaseReference = getFirebaseDeckAccessRef(deckId);
        databaseReference.setValue(access.getAccess());
    }

    public static void deleteDeckAccess(final String deckId) {
        getFirebaseDeckAccessRef(deckId).removeValue();
    }
}
