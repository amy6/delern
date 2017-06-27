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

package org.dasfoo.delern.adapters;

import android.util.Log;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

import org.dasfoo.delern.handlers.OnCardViewHolderClick;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.util.LogUtil;
import org.dasfoo.delern.viewholders.CardViewHolder;

/**
 * Created by katarina on 11/19/16.
 */

public class CardRecyclerViewAdapter extends FirebaseRecyclerAdapter<Card, CardViewHolder> {

    private static final String TAG = LogUtil.tagFor(CardRecyclerViewAdapter.class);

    private final OnCardViewHolderClick mOnCardViewHolderClick;

    private final Deck mDeck;

    /**
     * Create a new FirebaseRecyclerAdapter.
     *
     * @param builder inner class with all the properties
     */
    public CardRecyclerViewAdapter(final Builder builder) {
        super(builder.mNestedModelClass, builder.mNestedLayout, builder.mNestedViewHolderClass,
                builder.mNestedQuery);
        this.mOnCardViewHolderClick = builder.mNestedOnClickListener;
        this.mDeck = builder.mNestedDeck;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void populateViewHolder(final CardViewHolder viewHolder, final Card card,
                                      final int position) {
        viewHolder.getFrontTextView().setText(card.getFront());
        viewHolder.getBackTextView().setText(card.getBack());
        viewHolder.setOnViewClick(mOnCardViewHolderClick);
    }

    /**
     * {@inheritDoc}
     * Overriding to add key to the model.
     */
    @Override
    protected Card parseSnapshot(final DataSnapshot snapshot) {
        return Card.fromSnapshot(snapshot, Card.class, mDeck);
    }

    /**
     * Builder class for easy creation of CardRecyclerViewAdapter.
     */
    public static class Builder {
        private final Class<Card> mNestedModelClass;
        private final int mNestedLayout;
        private final Class<CardViewHolder> mNestedViewHolderClass;
        private final Query mNestedQuery;
        private OnCardViewHolderClick mNestedOnClickListener;
        private final Deck mNestedDeck;

        /**
         * Constructor with required parameters.
         *
         * @param nestedModelClass ViewAdapter model class
         * @param nestedLayout     ViewAdapter layout
         * @param nestedViewHolder ViewAdapter holder
         * @param nestedQuery      ViewAdapter query
         * @param deck             deck from which cards are shown
         */
        public Builder(final Class<Card> nestedModelClass, final int nestedLayout,
                       final Class<CardViewHolder> nestedViewHolder, final Query nestedQuery,
                       final Deck deck) {
            // TODO(refactoring): Review builder necessity.
            this.mNestedModelClass = nestedModelClass;
            this.mNestedLayout = nestedLayout;
            this.mNestedViewHolderClass = nestedViewHolder;
            this.mNestedQuery = nestedQuery;
            this.mNestedDeck = deck;
        }

        /**
         * Sets the onAddUpdateButtonClick listener of this view.
         *
         * @param nestedOnClickListener callback
         * @return this
         */
        public Builder setOnClickListener(final OnCardViewHolderClick nestedOnClickListener) {
            this.mNestedOnClickListener = nestedOnClickListener;
            return this;
        }

        /**
         * Build a new instance based on the fields in this builder.
         *
         * @return ViewAdapter with all the necessary fields set
         * @throws InstantiationException if not all required fields are set
         */
        public CardRecyclerViewAdapter build() throws InstantiationException {
            if (this.mNestedOnClickListener == null) {
                Log.e(TAG, "Set OnClickListener");
                throw new InstantiationException("OnClickListener is required");
            }
            return new CardRecyclerViewAdapter(this);
        }
    }
}
