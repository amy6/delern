package org.dasfoo.delern.models;

import android.text.TextUtils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

/**
 * Created by katarina on 2/23/17.
 */

@SuppressWarnings({"checkstyle:MemberName", "checkstyle:HiddenField"})
public class View {
    @Exclude
    private static final String VIEWS = "views";
    @Exclude
    private static final String DELIMITER = "/";
    @Exclude
    private String cardId;
    private String levelBefore;
    private String reply;
    private Object timestamp;

    /**
     * Constructor for View.
     *
     * @param cardId id of card.
     * @param levelBefore lever of card before learning.
     * @param reply reply on card by learning (Y/N).
     */
    public View(final String cardId, final String levelBefore, final String reply) {
        this.cardId = cardId;
        this.levelBefore = levelBefore;
        this.reply = reply;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    /**
     * Gets reference to views for current user. It is views/userId.
     *
     * @return reference to views for user.
     */
    @Exclude
    public static DatabaseReference getViewDatabaseReference() {
        // keep sync special location for offline use
        // https://firebase.google.com/docs/database/android/offline-capabilities
        DatabaseReference viewDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child(VIEWS)
                .child(User.getCurrentUser().getUid());
        viewDatabaseReference.keepSynced(true);
        return viewDatabaseReference;
    }

    /**
     * Gets views node by deckId. views/userId/deckId
     *
     * @param deckId id of deck.
     * @return views node of deck.
     */
    @Exclude
    public static String getViewsNodeByDeckId(final String deckId) {
        return TextUtils.join(DELIMITER, new String[]{
                VIEWS,
                User.getCurrentUser().getUid(),
                deckId,
        });
    }

    /**
     * Adds View to Firebase.
     * Path is views/userId/deckId/cardId.
     *
     * @param deckId id of deck.
     * @param view view object.
     */
    @Exclude
    public static void addView(final String deckId, final View view) {
        String key = getViewDatabaseReference()
                .child(deckId)
                .child(view.getCardId())
                .push()
                .getKey();
        getViewDatabaseReference().child(deckId).child(view.getCardId()).child(key).setValue(view);
    }

    /**
     * Getter for card Id.
     *
     * @return id of card.
     */
    public String getCardId() {
        return cardId;
    }

    /**
     * Setter for card Id.
     *
     * @param cardId id of card.
     */
    public void setCardId(final String cardId) {
        this.cardId = cardId;
    }

    /**
     * Getter for card level before learning card.
     *
     * @return level of card before leaning it.
     */
    public String getLevelBefore() {
        return levelBefore;
    }

    /**
     * Sets level of card before reply(Y/N) on it.
     *
     * @param levelBefore level of card before learning it.
     */
    public void setLevelBefore(final String levelBefore) {
        this.levelBefore = levelBefore;
    }

    /**
     * Getter for user reply by learning card. It can be (Y or N).
     *
     * @return user reply
     */
    public String getReply() {
        return reply;
    }

    /**
     * Setter for user reply by learning card.
     *
     * @param reply user reply (Y or N)
     */
    public void setReply(final String reply) {
        this.reply = reply;
    }

    /**
     * Getter for time when user looked at card.
     *
     * @return time
     */
    public Object getTimestamp() {
        return timestamp;
    }

    /**
     * Sets time to View when user looked at card.
     *
     * @param timestamp time (ServerValue.Timestamt)
     */
    public void setTimestamp(final Object timestamp) {
        this.timestamp = timestamp;
    }
}
