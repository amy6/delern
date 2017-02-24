package org.dasfoo.delern.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by katarina on 2/23/17.
 */

@SuppressWarnings({"checkstyle:MemberName", "checkstyle:HiddenField"})
public class View {
    @Exclude
    private static final String VIEWS = "views";
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
     * @param timestamp time of learning card.
     */
    public View(final String cardId, final String levelBefore, final String reply,
                final Object timestamp) {
        this.cardId = cardId;
        this.levelBefore = levelBefore;
        this.reply = reply;
        this.timestamp = timestamp;
    }

    /**
     * Gets reference to views for current user. It is views/userId.
     *
     * @return reference to views for user.
     */
    @Exclude
    public static DatabaseReference getViewDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference().child(VIEWS)
                .child(User.getCurrentUser().getUid());
    }

    /**
     * Deletes all views for given deck.
     *
     * @param deckId id of deck.
     */
    @Exclude
    public static void deleteViewsFromDeck(final String deckId) {
        getViewDatabaseReference().child(deckId).removeValue();
    }

    /**
     * Deletes vies from Firebase using deck ID and card ID.
     *
     * @param deckId id of deck.
     * @param cardId id of card.
     */
    @Exclude
    public static void deleteViewById(final String deckId, final String cardId) {
        getViewDatabaseReference().child(deckId).child(cardId).removeValue();
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
