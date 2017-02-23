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
    String cardId;
    String levelBefore;
    String reply;
    Object timestamp;

    public View(String cardId, String levelBefore, String reply, Object timestamp) {
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
    public static void removeViewsFromDeck(String deckId) {
        getViewDatabaseReference().child(deckId).removeValue();
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getLevelBefore() {
        return levelBefore;
    }

    public void setLevelBefore(String levelBefore) {
        this.levelBefore = levelBefore;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
