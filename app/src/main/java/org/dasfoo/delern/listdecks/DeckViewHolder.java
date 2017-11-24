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

package org.dasfoo.delern.listdecks;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.dasfoo.delern.R;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Katarina Sheremet on 9/22/16 1:11 AM.
 * Provide a reference to the views for each data item
 * Complex data items may need more than one view per item, and
 * you provide access to all the views for a data item in a view holder
 */

public class DeckViewHolder extends RecyclerView.ViewHolder implements
        PopupMenu.OnMenuItemClickListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeckViewHolder.class);
    private static final String NULL_CARDS = "0";
    private static final int CARDS_COUNTER_LIMIT = 200;

    @BindView(R.id.deck_text_view)
    /* default */ TextView mDeckTextView;
    @BindView(R.id.count_to_learn_textview)
    /* default */ TextView mCountToLearnTextView;
    private DeckAccess mDeckAccess;
    private Deck mDeck;
    private final CompositeDisposable mResources = new CompositeDisposable();
    private final OnDeckAction mOnViewClick;

    /**
     * Constructor. It initializes variable that describe how to place deck.
     *
     * @param parent      parent view
     * @param onViewClick action listener
     */
    public DeckViewHolder(final ViewGroup parent, final OnDeckAction onViewClick) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.deck_text_view,
                parent, false));
        ButterKnife.bind(this, itemView);
        mOnViewClick = onViewClick;
    }

    @OnClick(R.id.deck_view_holder)
    /* default */ void onDeckClick(final View view) {
        // if number of cards is 0, show message to user
        String cardCount = mCountToLearnTextView.getText().toString();
        if (NULL_CARDS.equals(cardCount)) {
            Toast.makeText(view.getContext(), R.string.no_card_message,
                    Toast.LENGTH_SHORT).show();
        } else {
            mOnViewClick.learnDeck(mDeck);
        }
    }

    /**
     * Method shows Popup menu on chosen deck menu.
     * Access to method is package-private due to ButterKnife
     *
     * @param v clicked view
     */
    @OnClick(R.id.deck_popup_menu)
    /* default */ void showPopup(final View v) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.deck_menu, popup.getMenu());
        managePopupMenu(popup.getMenu());
        popup.show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onMenuItemClick(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_deck_menu:
                mOnViewClick.editDeck(mDeck);
                return true;
            case R.id.deck_settings:
                // TODO(dotdoom): mDeckAccess can be null!
                mOnViewClick.editDeckSettings(mDeckAccess);
                return true;
            case R.id.deck_share:
                mOnViewClick.shareDeck(mDeck);
                return true;
            default:
                LOGGER.info("Menu Item {} is not implemented yet", item.getItemId());
                return false;
        }
    }

    /**
     * Disables menu settings regarding user's access.
     * menu.getMenu(0) - Edit Cards,
     * menu.getMenu(1) - Settings,
     * menu.getMenu(2) - Sharing.
     *
     * @param menu Popup menu
     */
    private void managePopupMenu(final Menu menu) {
        // TODO(dotdoom): mDeckAccess can be null!
        switch (mDeckAccess.getAccess()) {
            case "read":
                menu.getItem(0).setEnabled(false);
                menu.getItem(2).setEnabled(false);
                break;
            case "write":
                menu.getItem(2).setEnabled(false);
                break;
            default:
                break;
        }
    }

    /**
     * Set deck object currently associated with this ViewHolder.
     *
     * @param deck Deck or null if ViewHolder is being recycled.
     */
    public void setDeck(@Nullable final Deck deck) {
        mDeck = deck;

        if (deck == null) {
            mResources.clear();
        } else {
            mDeckTextView.setText(deck.getName());
            mResources.add(deck.fetchDeckAccessOfUser().subscribe(v -> mDeckAccess = v));
            mResources.add(Deck.fetchCount(
                    mDeck.fetchCardsToRepeatWithLimitQuery(CARDS_COUNTER_LIMIT + 1))
                    .subscribe((final Long cardsCount) -> {
                        if (cardsCount <= CARDS_COUNTER_LIMIT) {
                            mCountToLearnTextView.setText(String.valueOf(cardsCount));
                        } else {
                            String tooManyCards = CARDS_COUNTER_LIMIT + "+";
                            mCountToLearnTextView.setText(tooManyCards);
                        }
                    }));
        }
    }
}
