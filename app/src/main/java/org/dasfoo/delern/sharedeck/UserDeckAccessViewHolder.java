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
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.dasfoo.delern.R;
import org.dasfoo.delern.models.DeckAccess;
import org.dasfoo.delern.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;

/**
 * Represents 1 item of recycler view.
 */
public class UserDeckAccessViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.sharing_user_permissions)
    /* default */ Spinner mSharingPermissionsSpinner;
    @BindView(R.id.user_name_textview)
    /* default */ TextView mNameTextView;
    @BindView(R.id.circle_profile_photo)
    /* default */ CircleImageView mProfilePhoto;
    private final ShareDeckActivityPresenter mPresenter;
    private Disposable mUserDisposable;

    /**
     * Constructor for one item of recyclerview.
     *
     * @param parent    parent view.
     * @param presenter presenter to use callbacks from.
     */
    public UserDeckAccessViewHolder(final ViewGroup parent,
                                    final ShareDeckActivityPresenter presenter) {
        super(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_deck_access_layout, parent, false));
        ButterKnife.bind(this, itemView);
        mPresenter = presenter;
    }

    /**
     * Set DeckAccess for this item.
     *
     * @param deckAccess DeckAccess or null if the view is being recycled.
     */
    public void setDeckAccess(@Nullable final DeckAccess deckAccess) {
        if (deckAccess == null) {
            mUserDisposable.dispose();
        } else {
            mUserDisposable = deckAccess.fetchChild(
                    deckAccess.getChildReference(User.class), User.class)
                    .subscribe((final User user) -> {
                        mNameTextView.setText(user.getName());
                        Context context = itemView.getContext();
                        Picasso.with(context)
                                .load(user.getPhotoUrl())
                                .error(android.R.color.holo_green_dark)
                                .placeholder(R.drawable.splash_screen)
                                .into(mProfilePhoto);
                        if ("owner".equals(deckAccess.getAccess())) {
                            mSharingPermissionsSpinner
                                    .setAdapter(new ShareSpinnerAdapter(context,
                                            R.array.owner_access_spinner_text,
                                            R.array.owner_access_spinner_img));
                        } else {
                            mSharingPermissionsSpinner
                                    .setAdapter(new ShareSpinnerAdapter(context,
                                            R.array.user_permissions_spinner_text,
                                            R.array.share_permissions_spinner_img));
                            mSharingPermissionsSpinner
                                    .setSelection(mPresenter
                                            .setUserAccessPositionForSpinner(deckAccess));
                            mSharingPermissionsSpinner
                                    .setOnItemSelectedListener(
                                            setPermissionChangedListener(deckAccess));
                        }
                    });
        }
    }

    // TODO(dotdoom): create a separate view for SharingPermissionsSpinner
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
}

