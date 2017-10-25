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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.dasfoo.delern.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by katarina on 10/24/17.
 */

public class UserDeckAccessRecyclerViewAdapter
        extends RecyclerView.Adapter<UserDeckAccessRecyclerViewAdapter.ViewHolder> {

    private Context mContext;

    private final String[] mName = {"Katarina Sheremet",
            "Artem Sheremet",
    };

    private final String[] mEmail = {"kate@sheremet.ch",
            "artem@sheremet.ch",
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_deck_access_layout, parent, false));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mNameTextView.setText(mName[position]);
        holder.mEmaiTextView.setText(mEmail[position]);
        holder.mSharingPermissionsSpinner.setAdapter(new ShareSpinnerAdapter(mContext));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return mName.length;
    }

    /**
     * Represents 1 item of recycler view.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sharing_user_permissions)
    /* default */ Spinner mSharingPermissionsSpinner;
        @BindView(R.id.user_name_textview)
    /* default */ TextView mNameTextView;
        @BindView(R.id.user_email_textview)
    /* default */ TextView mEmaiTextView;

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
