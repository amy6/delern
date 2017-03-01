package org.dasfoo.delern.models.listener;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.dasfoo.delern.handlers.OnLearningCardAvailable;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.ScheduledCard;

/**
 * Created by katarina on 3/1/17.
 */

public class LearningCardListener extends UserMessageValueEventListener {

    private Context mContext;
    private String mDeckId;
    private OnLearningCardAvailable mCardAvailable;
    private ValueEventListener mCurrentCardListener;

    private Query mCurrentCardQuery;

    private Card mCurrentCard;
    private ScheduledCard mScheduledCard;

    public LearningCardListener(final Context context, final String deckId,
                                final OnLearningCardAvailable cardAvailable) {
        super(context);
        this.mContext = context;
        this.mDeckId = deckId;
        this.mCardAvailable = cardAvailable;
        mCurrentCardListener = new UserMessageValueEventListener(mContext) {

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for (DataSnapshot cardSnapshot : dataSnapshot.getChildren()) {
                    mCurrentCard = cardSnapshot.getValue(Card.class);
                    mCurrentCard.setcId(cardSnapshot.getKey());
                }
                mCardAvailable.onNewCardAvailable();
            }
        };
    }

    public Card getCurrentCard() {
        return mCurrentCard;
    }

    public ScheduledCard getScheduledCard() {
        return mScheduledCard;
    }

    public String getDeckId() {
        return mDeckId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (!dataSnapshot.hasChildren()) {
            mCardAvailable.onNoCardsAvailable();
        }
        // It has only 1 card because of limit(1)
        for (DataSnapshot cardSnapshot : dataSnapshot.getChildren()) {
            mScheduledCard = cardSnapshot.getValue(ScheduledCard.class);
            mScheduledCard.setcId(cardSnapshot.getKey());
            // Check if current card has already old listener, remove it.
            if (mCurrentCardQuery != null) {
                mCurrentCardQuery.removeEventListener(mCurrentCardListener);
            }
            // Init query by Id
            mCurrentCardQuery = Card.getCardById(mDeckId, mScheduledCard.getcId());
            mCurrentCardQuery.addValueEventListener(mCurrentCardListener);
        }
    }

    /**
     * Releases used resources.
     */
    public void clean() {
        if (mCurrentCardQuery != null) {
            mCurrentCardQuery.removeEventListener(mCurrentCardListener);
        }
    }
}
