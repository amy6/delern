package org.dasfoo.delern.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by katarina on 2/23/17.
 */

public class View {
    @Exclude
    private static final String VIEWS = "views";
    @Exclude
    private String cardId;
    private String levelBefore;
    private String reply;
    private Object timestamp;

    public View(final String cardId, final String levelBefore, final String reply,
                final Object timestamp) {
        this.cardId = cardId;
        this.levelBefore = levelBefore;
        this.reply = reply;
        this.timestamp = timestamp;
    }

    @Exclude
    public static DatabaseReference getViewDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference().child(VIEWS)
                .child(User.getCurrentUser().getUid());
    }

    @Exclude
    public static void deleteViewsFromDeck(final String deckId) {
        getViewDatabaseReference().child(deckId).removeValue();
    }

    @Exclude
    public static void deleteViewById(final String deckId, final String cardId) {
        getViewDatabaseReference().child(deckId).child(cardId).removeValue();
    }

    @Exclude
    public static void addView(final String deckId, final View view) {
        String key = getViewDatabaseReference()
                .child(deckId)
                .child(view.getCardId())
                .push()
                .getKey();
        getViewDatabaseReference().child(deckId).child(view.getCardId()).child(key).setValue(view);
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(final String cardId) {
        this.cardId = cardId;
    }

    public String getLevelBefore() {
        return levelBefore;
    }

    public void setLevelBefore(final String levelBefore) {
        this.levelBefore = levelBefore;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(final String reply) {
        this.reply = reply;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Object timestamp) {
        this.timestamp = timestamp;
    }
}
