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

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.dasfoo.delern.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    private final OnCardViewHolderClick mOnViewClick;

    /**
     * Constructor. It initializes variable that describe how to place card.
     *
     * @param itemView              item view.
     * @param onCardViewHolderClick handles clicks on cards.
     */
    public CardViewHolder(final View itemView, final OnCardViewHolderClick onCardViewHolderClick) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mOnViewClick = onCardViewHolderClick;
    }

    /**
     * Getter for front side of card. It references to R.id.front_textview.
     *
     * @return reference to R.id.front_textview.
     */
    public TextView getFrontTextView() {
        return mFrontTextView;
    }

    /**
     * Getter for back side of card. It references to R.id.back_textview.
     *
     * @return reference to R.id.back_textview.
     */
    public TextView getBackTextView() {
        return mBackTextView;
    }

    /**
     * Getter for card view that contains front and back sides of card.
     *
     * @return view of card.
     */
    public CardView getCardView() {
        return mCardView;
    }

    /**
     * Called when recyclerview card has been clicked.
     */
    @OnClick(R.id.card_edit_click)
    public void onClick() {
        mOnViewClick.onCardClick(getAdapterPosition());
    }
}
