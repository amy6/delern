package org.dasfoo.delern.models;

import android.text.TextUtils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import org.dasfoo.delern.util.LogUtil;

/**
 * Created by katarina on 2/22/17.
 * Model class for accessing deck_access/userId node.
 */

@SuppressWarnings({"checkstyle:MemberName", "checkstyle:HiddenField"})
public class DeckAccess {
    @Exclude
    private static final String TAG = LogUtil.tagFor(DeckAccess.class);
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
     * Gets deck_access node of user. deck_access/deckId/userId
     *
     * @param deckId id of deck.
     * @return path to deck_access user node
     */
    @Exclude
    public static String getDeckAccessNodeByDeckId(final String deckId) {
        return TextUtils.join("/", new String[]{DeckAccess.DECK_ACCESS, deckId,
                User.getCurrentUser().getUid(), });
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
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "DeckAccess{" +
                "access='" + access + '\'' +
                '}';
    }
}
