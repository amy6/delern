package org.dasfoo.delern.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * Created by katarina on 2/20/17.
 */

public class ScheduledCard {

    private String cId;
    private String level;
    private long repeatAt;

    @Exclude
    private static final String LEARNING = "learning";

    public ScheduledCard(String level, long repeatAt) {
        this.level = level;
        this.repeatAt = repeatAt;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public long getRepeatAt() {
        return repeatAt;
    }

    public void setRepeatAt(long repeatAt) {
        this.repeatAt = repeatAt;
    }

    /**
     * Gets reference to learning cards in Firebase.
     *
     * @return reference to cards.
     */
    @Exclude
    public static DatabaseReference getFirebaseScheduledCardRef() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(LEARNING).child(User.getCurrentUser().getUid());
        //databaseReference.keepSynced(true);
        return databaseReference;
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


}
