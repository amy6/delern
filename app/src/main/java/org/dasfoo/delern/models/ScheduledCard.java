package org.dasfoo.delern.models;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.dasfoo.delern.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by katarina on 2/20/17.
 */

public class ScheduledCard {

    @Exclude
    private String cId;
    private String level;
    private long repeatAt;

    @Exclude
    private static final String LEARNING = "learning";

    @Exclude
    private static final String TAG = LogUtil.tagFor(ScheduledCard.class);

    public ScheduledCard() {
        // Empty constructor is needed for casting DataSnaphot to current class.
    }

    public ScheduledCard(final String level, final long repeatAt) {
        this.level = level;
        this.repeatAt = repeatAt;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(final String cId) {
        this.cId = cId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(final String level) {
        this.level = level;
    }

    public long getRepeatAt() {
        return repeatAt;
    }

    public void setRepeatAt(final long repeatAt) {
        this.repeatAt = repeatAt;
    }

    /**
     * Gets reference to learning cards in Firebase.
     *
     * @return reference to cards.
     */
    @Exclude
    public static DatabaseReference getFirebaseScheduledCardRef() {
        return FirebaseDatabase.getInstance().getReference()
                .child(LEARNING).child(User.getCurrentUser().getUid());
        //databaseReference.keepSynced(true);
    }

    @Exclude
    public static void writeScheduleForCard(final String deckId,final String cardId,
                                            final ScheduledCard scheduledCard ) {
        DatabaseReference databaseReference = getFirebaseScheduledCardRef();
        databaseReference.child(deckId).child(cardId).setValue(scheduledCard);
    }

    /**
     * Method gets all cards to repeat calculating current time im milliseconds.
     *
     * @param deckId deck ID where to get cards.
     * @return query of cards to repeat.
     */
    @Exclude
    public static Query fetchCardsFromDeckToRepeat(final String deckId) {
        long time = System.currentTimeMillis();
        return getFirebaseScheduledCardRef()
                .child(deckId)
                .orderByChild("repeatAt")
                .endAt(time);
    }

    @Exclude
    public static Query fetchCardsToRepeatWithLimit(final String deckId, final int limit) {
        return fetchCardsFromDeckToRepeat(deckId).limitToFirst(limit);
    }

    @Exclude
    public static void deleteCardsByDeckId(final String deckId) {
        //TODO(ksheremet): Add listeners on success and failure
        getFirebaseScheduledCardRef().child(deckId).removeValue();
    }

    @Exclude
    public static void deleteCardbyId(final String deckId, final  String cardId) {
        //TODO(ksheremet): Add listeners on success and failure
        getFirebaseScheduledCardRef().child(deckId).child(cardId).removeValue();
    }

    /**
     * Updates scheduledCard using deck ID. Card ID is the same.
     *
     * @param scheduledCard   new card
     * @param deckId deck ID where to update card.
     */
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    @Exclude
    public static void updateCard(final ScheduledCard scheduledCard, final String deckId) {
        Log.v(TAG, scheduledCard.toString());
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("level", scheduledCard.getLevel());
        childUpdates.put("repeatAt", scheduledCard.getRepeatAt());
        Log.v(TAG, childUpdates.toString());

        getFirebaseScheduledCardRef()
                .child(deckId)
                .child(scheduledCard.getcId())
                .updateChildren(childUpdates);
    }

    @Override
    public String toString() {
        return "ScheduledCard{" +
                "cId='" + cId + '\'' +
                ", level='" + level + '\'' +
                ", repeatAt=" + repeatAt +
                '}';
    }
}
