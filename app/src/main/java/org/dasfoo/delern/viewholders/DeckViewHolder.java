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

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.dasfoo.delern.R;
import org.dasfoo.delern.handlers.OnDeckViewHolderClick;
import org.dasfoo.delern.listeners.TextWatcherStub;
import org.dasfoo.delern.models.DeckType;
import org.dasfoo.delern.util.LogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Katarina Sheremet on 9/22/16 1:11 AM.
 * Provide a reference to the views for each data item
 * Complex data items may need more than one view per item, and
 * you provide access to all the views for a data item in a view holder
 */

public class DeckViewHolder extends RecyclerView.ViewHolder implements
        PopupMenu.OnMenuItemClickListener {

    private static final String TAG = LogUtil.tagFor(DeckViewHolder.class);
    private static final String NULL_CARDS = "0";

    @BindView(R.id.deck_text_view)
    TextView mDeckTextView;
    @BindView(R.id.count_to_learn_textview)
    TextView mCountToLearnTextView;
    private OnDeckViewHolderClick mOnViewClick;
    private String mCheckedDeckType;

    /**
     * Constructor. It initializes variable that describe how to place deck.
     *
     * @param viewHolder item view
     */
    public DeckViewHolder(final View viewHolder) {
        super(viewHolder);
        ButterKnife.bind(this, viewHolder);
        mCountToLearnTextView.setText(R.string.n_a);
    }

    /**
     * Getter to reference to R.id.deck_text_view.
     *
     * @return textview of deck
     */
    public TextView getDeckTextView() {
        return mDeckTextView;
    }

    /**
     * Getter for number of cards to learn. mCountToLearnTextView references to
     * R.id.count_to_learn_textview.
     *
     * @return textview with number of cards to learn
     */
    public TextView getCountToLearnTextView() {
        return mCountToLearnTextView;
    }

    /**
     * Setter for mOnViewClick. It listeners clicks on deck name and
     * popup menu.
     *
     * @param onViewClick onDeckViewClickListener
     */
    public void setOnViewClick(final OnDeckViewHolderClick onViewClick) {
        this.mOnViewClick = onViewClick;
    }

    @OnClick(R.id.deck_view_holder)
    void onDeckClick(final View view) {
        // if number of cards is 0, show message to user
        String cardCount = mCountToLearnTextView.getText().toString();
        if (NULL_CARDS.equals(cardCount)) {
            Toast.makeText(view.getContext(), R.string.no_card_message,
                    Toast.LENGTH_SHORT).show();
        } else {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                mOnViewClick.learnDeck(position);
            }
        }
    }

    @OnClick(R.id.deck_popup_menu)
    void showPopup(final View v) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.deck_menu, popup.getMenu());
        popup.show();
        setDeckType(popup);
    }

    /**
     * Sets current deck type in popup menu and make disable this deck type.
     * Disabled menuItem doesn't allow to write in Firebase the same deck type
     * if user touch it.
     *
     * @param popup popup menu
     */
    private void setDeckType(final PopupMenu popup) {
        MenuItem menuItem = popup.getMenu().findItem(R.id.basic_type);
        if (DeckType.SWISS.name().equalsIgnoreCase(mCheckedDeckType)) {
            menuItem = popup.getMenu().findItem(R.id.swissgerman_type);
        }
        if (DeckType.GERMAN.name().equalsIgnoreCase(mCheckedDeckType)) {
            menuItem = popup.getMenu().findItem(R.id.german_type);
        }
        menuItem.setChecked(true);
        menuItem.setEnabled(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onMenuItemClick(final MenuItem item) {

        int position = getAdapterPosition();
        if (position == RecyclerView.NO_POSITION) {
            // ViewHolder was either removed or the view has been changed.
            // Rather than failing, ignore the click.
            return false;
        }
        switch (item.getItemId()) {
            case R.id.rename_deck_menu:
                showRenameDialog();
                return true;
            case R.id.edit_deck_menu:
                mOnViewClick.editDeck(position);
                return true;
            case R.id.delete_deck_menu:
                showDeleteDialog();
                return true;
            case R.id.basic_type:
                mOnViewClick.changeDeckType(position, DeckType.BASIC);
                return true;
            case R.id.german_type:
                mOnViewClick.changeDeckType(position, DeckType.GERMAN);
                return true;
            case R.id.swissgerman_type:
                mOnViewClick.changeDeckType(position, DeckType.SWISS);
                return true;
            default:
                Log.i(TAG, "Menu Item is not implemented yet");
                return false;
        }
    }

    /**
     * Setter for deck type.
     *
     * @param checkedDeckType deck type
     */
    public void setDeckCardType(final String checkedDeckType) {
        this.mCheckedDeckType = checkedDeckType;
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(getDeckTextView().getContext())
                .setMessage(R.string.delete_deck)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        mOnViewClick.deleteDeck(getAdapterPosition());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void showRenameDialog() {
        final EditText input = new EditText(getDeckTextView().getContext());
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(getDeckTextView().getText().toString());

        final AlertDialog dialog = new AlertDialog.Builder(getDeckTextView().getContext())
                .setTitle(R.string.deck)
                .setView(input)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.rename, new DialogInterface.OnClickListener() {
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        mOnViewClick.renameDeck(getAdapterPosition(),
                                input.getText().toString().trim());
                    }
                })
                .create();
        input.addTextChangedListener(new TextWatcherStub() {
            @Override
            public void afterTextChanged(final Editable s) {
                // Check if edittext is empty, disable button. Not allow deck that
                // contains only spaces in name
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setEnabled(!TextUtils.isEmpty(s.toString().trim()));
            }
        });
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }
}
