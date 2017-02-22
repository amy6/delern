package org.dasfoo.delern.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by katarina on 2/20/17.
 */

public class Learning {

    private String cId;
    private String level;
    private long repeatAt;

    @Exclude
    private static final String LEARNING = "learning";

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
    public static DatabaseReference getFirebaseLearningRef() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(LEARNING).child(User.getCurrentUser().getUid());
        //databaseReference.keepSynced(true);
        return databaseReference;
    }


}
