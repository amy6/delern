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

package org.dasfoo.delern.billing.row;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.dasfoo.delern.R;

/**
 * ViewHolder for quick access to row's views.
 */
public final class RowViewHolder extends RecyclerView.ViewHolder {
    private final TextView mTitle;
    private final TextView mDescription;
    private final TextView mPrice;
    private final Button mPayButton;
    private final ImageView mSkuIcon;

    /**
     * Constructor for initializing a row.
     * Sets on click listener for button.
     *
     * @param itemView      item view to initialize.
     * @param clickListener on click listener that hanles on pay click.
     */
    public RowViewHolder(final View itemView, final OnButtonClickListener clickListener) {
        super(itemView);
        mTitle = itemView.findViewById(R.id.title);
        mPrice = itemView.findViewById(R.id.price);
        mDescription = itemView.findViewById(R.id.description);
        mSkuIcon = itemView.findViewById(R.id.sku_icon);
        mPayButton = itemView.findViewById(R.id.pay_button);
        if (mPayButton != null) {
            mPayButton.setOnClickListener(view ->
                    clickListener.onPayButtonClicked(getAdapterPosition()));
        }
    }

    /**
     * Getter for title of product.
     *
     * @return title.
     */
    public TextView getTitle() {
        return mTitle;
    }

    /**
     * Getter for description of product.
     *
     * @return description.
     */
    public TextView getDescription() {
        return mDescription;
    }

    /**
     * Getter for price of product.
     *
     * @return price
     */
    public TextView getPrice() {
        return mPrice;
    }

    /**
     * Getter for pay button.
     *
     * @return pay button
     */
    public Button getPayButton() {
        return mPayButton;
    }

    /**
     * Getter for image that will be displayed with product.
     *
     * @return image.
     */
    public ImageView getSkuIcon() {
        return mSkuIcon;
    }

    /**
     * Handler for a button click on particular row.
     */
    public interface OnButtonClickListener {
        /**
         * Method that fired when button clicked.
         *
         * @param position position of button in Recycler View.
         */
        void onPayButtonClicked(int position);
    }
}
