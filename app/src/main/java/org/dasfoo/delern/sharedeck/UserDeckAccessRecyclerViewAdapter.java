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

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.squareup.picasso.Picasso;

import org.dasfoo.delern.R;
import org.dasfoo.delern.models.DeckAccess;
import org.dasfoo.delern.models.User;
import org.dasfoo.delern.models.helpers.FirebaseSnapshotParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Places information about all users who can see and edit deck. Sets settings
 * for changing user's permissions.
 */
public class UserDeckAccessRecyclerViewAdapter
        extends FirebaseRecyclerAdapter<DeckAccess, UserDeckAccessRecyclerViewAdapter.ViewHolder> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(UserDeckAccessRecyclerViewAdapter.class);

    private final ShareDeckActivityPresenter mPresenter;

    /**
     * Constructor for Adapter. Adapter places information about users, that can
     * use a deck.
     *
     * @param activity  Activity that manages this RecyclerView.
     * @param presenter presenter for performing operations.
     */
    public UserDeckAccessRecyclerViewAdapter(final LifecycleOwner activity,
                                             final ShareDeckActivityPresenter presenter) {
        super(new FirebaseRecyclerOptions.Builder<DeckAccess>()
                .setQuery(presenter.getReference(),
                        new FirebaseSnapshotParser<>(DeckAccess.class, presenter.getDeck()))
                .setLifecycleOwner(activity).build());
        this.mPresenter = presenter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_deck_access_layout, parent, false);
        return new ViewHolder(view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings(/* TODO(dotdoom): garbage collection */ "CheckReturnValue")
    protected void onBindViewHolder(final ViewHolder viewHolder, final int position,
                                    final DeckAccess deckAccess) {
        deckAccess.fetchChild(deckAccess.getChildReference(User.class), User.class)
                .subscribe((final User user) -> {
                    viewHolder.mNameTextView.setText(user.getName());
                    Context context = viewHolder.itemView.getContext();
                    Picasso.with(context)
                            .load(user.getPhotoUrl())
                            .error(android.R.color.holo_green_dark)
                            .placeholder(R.drawable.splash_screen)
                            .into(viewHolder.mProfilePhoto);
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
                                .setSelection(mPresenter
                                        .setUserAccessPositionForSpinner(deckAccess));
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
     * {@inheritDoc}
     */
    @Override
    public void onError(final DatabaseError error) {
        LOGGER.error("Error in Adapter: ", error.toException());
        super.onError(error);
    }

    /**
     * Represents 1 item of recycler view.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sharing_user_permissions)
        /* default */ Spinner mSharingPermissionsSpinner;
        @BindView(R.id.user_name_textview)
        /* default */ TextView mNameTextView;
        @BindView(R.id.circle_profile_photo)
        /* default */ CircleImageView mProfilePhoto;

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
