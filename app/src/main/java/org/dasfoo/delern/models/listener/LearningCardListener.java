/*
 * Copyright (C) 2017 Katarina Sheremet
 * This file is part of Delern.
 *
 * Delern is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Delern is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with  Delern.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.dasfoo.delern.models.listener;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.dasfoo.delern.controller.RepetitionIntervals;
import org.dasfoo.delern.handlers.OnLearningCardAvailable;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Level;
import org.dasfoo.delern.models.ScheduledCard;
import org.dasfoo.delern.models.View;

/**
 * Created by katarina on 3/1/17.
 * Provides operation for current card to learn. Gets current card and updates
 * the card if card was viewed by user.
 */

public class LearningCardListener extends AbstractUserMessageValueEventListener {

    /**
     * Answer on card if user knows it.
     */
    public static final String KNOW_CARD = "Y";

    /**
     * Answer on card if user doesn't know it.
     */
    public static final String DO_NOT_KNOW_CARD = "N";

    private final String mDeckId;
    private final OnLearningCardAvailable mCardAvailable;
    private final ValueEventListener mCurrentCardListener;

    private Query mCurrentCardQuery;

    private Card mCurrentCard;
    private ScheduledCard mScheduledCard;

    /**
     * Constructor.
     *
     * @param context       context for super class.
     * @param deckId        id of deck.
     * @param cardAvailable callbacks on available card.
     */
    public LearningCardListener(final Context context, final String deckId,
                                final OnLearningCardAvailable cardAvailable) {
        super(context);
        this.mDeckId = deckId;
        this.mCardAvailable = cardAvailable;
        mCurrentCardListener = new AbstractUserMessageValueEventListener(context) {

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for (DataSnapshot cardSnapshot : dataSnapshot.getChildren()) {
                    mCurrentCard = cardSnapshot.getValue(Card.class);
                    mCurrentCard.setcId(cardSnapshot.getKey());
                }
                mCardAvailable.onNewCard();
            }
        };
    }

    /**
     * Getter for current card.
     *
     * @return current card.
     */
    public Card getCurrentCard() {
        return mCurrentCard;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDataChange(final DataSnapshot dataSnapshot) {
        if (!dataSnapshot.hasChildren()) {
            mCardAvailable.onNoCards();
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
     * Handlers if card was viewed by user.
     *
     * @param answer answer on viewed card (Y/N)
     */
    public void viewedCard(final String answer) {
        String newCardLevel;
        if (KNOW_CARD.equals(answer)) {
            newCardLevel = Level.getNextLevel(mScheduledCard.getLevel());
        } else {
            newCardLevel = Level.L0.name();
        }
        View view = new View(mScheduledCard.getcId(), mScheduledCard.getLevel(), answer);
        mScheduledCard.setLevel(newCardLevel);
        mScheduledCard.setRepeatAt(RepetitionIntervals.getInstance()
                .getNextTimeToRepeat(newCardLevel));
        View.addView(mDeckId, view);
        ScheduledCard.updateCard(mScheduledCard, mDeckId);
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
