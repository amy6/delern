package org.dasfoo.delern.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by katarina on 2/22/17.
 */

@SuppressWarnings({"checkstyle:MemberName", "checkstyle:HiddenField"})
public class DeckAccess {
    @Exclude
    private static final String DECK_ACCESS = "deck_access";

    private String access;

    /**
     * Constructor for setting access for deck. Access for user can be owner, read or write
     *
     * @param acs sets access for deck.
     */
    public DeckAccess(final String acs) {
        this.access = acs;
    }

    /**
     * Getter for access of deck for the user.
     *
     * @return access of deck.
     */
    public String getAccess() {
        return access;
    }

    /**
     * Setter of access of deck for the user.
     *
     * @param acs sets access for deck.
     */
    public void setAccess(final String acs) {
        this.access = acs;
    }

    /**
     * References to deck_access by deck id for current user.
     * deck_access/deckId/userId
     *
     * @param deckId id of deck.
     * @return reference to deck access for deck by deck id.
     */
    @Exclude
    public static DatabaseReference getFirebaseDeckAccessRef(final String deckId) {
        return FirebaseDatabase.getInstance().getReference()
                .child(DECK_ACCESS).child(deckId).child(User.getCurrentUser().getUid());
        //databaseReference.keepSynced(true);
    }

    /**
     * Writes access to deck for current user. It can be "owner", "read", "write".
     *
     * @param access access to deck for current user.
     * @param deckId id of deck.
     */
    @Exclude
    public static void writeDeckAccessToFB(final DeckAccess access, final String deckId) {
        DatabaseReference databaseReference = getFirebaseDeckAccessRef(deckId);
        databaseReference.setValue(access.getAccess());
    }

    /**
     * Removes information about deck access for deck by deck ID.
     *
     * @param deckId id of deck.
     */
    public static void deleteDeckAccess(final String deckId) {
        getFirebaseDeckAccessRef(deckId).removeValue();
    }
}
