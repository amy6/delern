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

package org.dasfoo.delern.sharedeck;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import org.dasfoo.delern.R;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckAccess;
import org.dasfoo.delern.models.User;
import org.dasfoo.delern.models.helpers.FirebaseSnapshotParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserDeckAccessRecyclerViewAdapter
        extends FirebaseRecyclerAdapter<DeckAccess, UserDeckAccessRecyclerViewAdapter.ViewHolder> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(UserDeckAccessRecyclerViewAdapter.class);


    public UserDeckAccessRecyclerViewAdapter(final int modelLayout, final Deck deck) {
        super(new FirebaseSnapshotParser<>(DeckAccess.class, deck),
                modelLayout, ViewHolder.class, deck.getChildReference(DeckAccess.class));
    }

    @Override
    @SuppressWarnings("CheckReturnValue")
    protected void populateViewHolder(ViewHolder viewHolder, DeckAccess deckAccess, int position) {
        deckAccess.fetchChild(deckAccess.getChildReference(User.class), User.class)
                .subscribe((final User user) -> {
                    LOGGER.debug("Adapter" + user.toString());
                    viewHolder.mNameTextView.setText(user.getName());
                    viewHolder.mSharingPermissionsSpinner
                            .setAdapter(new ShareSpinnerAdapter(viewHolder.itemView.getContext()));
                });
    }

    /**
     * Represents 1 item of recycler view.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sharing_user_permissions)
    /* default */ Spinner mSharingPermissionsSpinner;
        @BindView(R.id.user_name_textview)
    /* default */ TextView mNameTextView;

        /**
         * Constructor for one item of recyclerview.
         *
         * @param itemView view.
         */
        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
