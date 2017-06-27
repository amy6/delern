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

package org.dasfoo.delern.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.dasfoo.delern.R;
import org.dasfoo.delern.handlers.OnCardViewHolderClick;

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
    private OnCardViewHolderClick mOnViewClick;

    /**
     * Constructor. It initializes variable that describe how to place card.
     *
     * @param itemView item view.
     */
    public CardViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
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
     * Setter for mOnViewClick. It listeners clicks on cards.
     *
     * @param onViewClick onCardViewHolderClick.
     */
    public void setOnViewClick(final OnCardViewHolderClick onViewClick) {
        this.mOnViewClick = onViewClick;
    }

    /**
     * Called when recyclerview card has been clicked.
     */
    @OnClick(R.id.card_edit_click)
    public void onClick() {
        mOnViewClick.onCardClick(getAdapterPosition());
    }
}
