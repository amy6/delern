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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import org.dasfoo.delern.R;
import org.dasfoo.delern.models.DeckAccess;
import org.dasfoo.delern.models.User;
import org.dasfoo.delern.models.helpers.FirebaseSnapshotParser;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Places information about all users who can see and edit deck. Sets settings
 * for changing user's permissions.
 */
public class UserDeckAccessRecyclerViewAdapter
        extends FirebaseRecyclerAdapter<DeckAccess, UserDeckAccessRecyclerViewAdapter.ViewHolder> {

    private final ShareDeckActivityPresenter mPresenter;

    public UserDeckAccessRecyclerViewAdapter(final int modelLayout,
                                             final ShareDeckActivityPresenter presenter) {
        super(new FirebaseSnapshotParser<>(DeckAccess.class, presenter.getDeck()),
                modelLayout, ViewHolder.class, presenter.getReference());
        this.mPresenter = presenter;
    }

    @Override
    @SuppressWarnings("CheckReturnValue")
    protected void populateViewHolder(final ViewHolder viewHolder, final DeckAccess deckAccess,
                                      final int position) {
        deckAccess.fetchChild(deckAccess.getChildReference(User.class), User.class)
                .subscribe((final User user) -> {
                    System.out.println("User:" + user);
                    viewHolder.mNameTextView.setText(user.getName());
                    Context context = viewHolder.itemView.getContext();
                    if ("owner".equals(deckAccess.getAccess())) {
                        viewHolder.mSharingPermissionsSpinner
                                .setAdapter(new ShareSpinnerAdapter(context,
                                        R.array.owner_access_spinner_text,
                                        R.array.owner_access_spinner_img));
                    } else {
                        viewHolder.mSharingPermissionsSpinner
                                .setAdapter(new ShareSpinnerAdapter(context,
                                        R.array.user_permissions_spinner_text,
                                        R.array.share_permissions_spinner_img));
                        viewHolder.mSharingPermissionsSpinner
                                .setSelection(mPresenter.getDefaultUserAccess(deckAccess));
                        viewHolder.mSharingPermissionsSpinner
                                .setOnItemSelectedListener(
                                        setPermissionChangedListener(deckAccess));
                    }
                });
    }

    private AdapterView.OnItemSelectedListener setPermissionChangedListener(
            final DeckAccess deckAccess) {
        return new AdapterView.OnItemSelectedListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view,
                                       final int position, final long l) {
                Context context = view.getContext();
                String[] sharingArrayOption = context.getResources()
                        .getStringArray(R.array.user_permissions_spinner_text);
                if (sharingArrayOption[position].equals(context
                        .getResources()
                        .getString(R.string.can_edit_text))) {
                    mPresenter.changeUserPermission("write", deckAccess);
                }
                if (sharingArrayOption[position].equals(context.getResources()
                        .getString(R.string.can_view_text))) {
                    mPresenter.changeUserPermission("read", deckAccess);
                }
                if (sharingArrayOption[position].equals(context
                        .getResources().getString(R.string.no_access_text))) {
                    mPresenter.changeUserPermission("", deckAccess);
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
                //No need for implementation
            }
        };
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
