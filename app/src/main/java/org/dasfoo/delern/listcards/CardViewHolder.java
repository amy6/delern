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

package org.dasfoo.delern.listcards;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import org.dasfoo.delern.R;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.util.CardColor;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.Nullable;

/**
 * Created by katarina on 11/14/16.
 * Describes an item view and metadata about its place within the RecyclerView.
 */
public class CardViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.front_textview)
    /* default */ TextView mFrontTextView;
    @BindView(R.id.back_textview)
    /* default */ TextView mBackTextView;
    @BindView(R.id.card_edit_click)
    /* default */ CardView mCardView;
    private Card mCard;

    /**
     * Constructor. It initializes variable that describe how to place card.
     *
     * @param parent                parent view.
     * @param onCardViewHolderClick handles clicks on cards.
     */
    public CardViewHolder(final ViewGroup parent,
                          final OnCardViewHolderClick onCardViewHolderClick) {
        super(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_text_view_for_deck, parent, false));
        ButterKnife.bind(this, itemView);
        mCardView.setOnClickListener(v -> onCardViewHolderClick.onCardClick(mCard));
    }

    /**
     * Set Card object currently associated with this ViewHolder.
     *
     * @param card Card or null if ViewHolder is being recycled.
     */
    @SuppressWarnings("deprecation" /* fromHtml(String, int) not available before API 24 */)
    public void setCard(@Nullable final Card card) {
        mCard = card;
        if (card != null) {
            if (card.getDeck().isMarkdown()) {
                mFrontTextView.setText(Html.fromHtml(card.getFront()));
                mBackTextView.setText(Html.fromHtml(card.getBack()));
            } else {
                mFrontTextView.setText(card.getFront());
                mBackTextView.setText(card.getBack());
            }
            mCardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(),
                    CardColor.getColor(card.specifyContentGender())));
        }
    }
}
