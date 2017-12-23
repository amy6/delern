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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.Query;

import org.dasfoo.delern.R;
import org.dasfoo.delern.addupdatecard.AddEditCardActivity;
import org.dasfoo.delern.di.Injector;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.DeckAccess;
import org.dasfoo.delern.models.ParcelableDeckAccess;
import org.dasfoo.delern.previewcard.PreEditCardActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity to view and edit all cards in the deck.
 */
public class EditCardListActivity extends AppCompatActivity implements
        SearchView.OnQueryTextListener, OnCardViewHolderClick {

    /**
     * IntentExtra deck to edit.
     */
    public static final String DECK_ACCESS = "deck_access";

    private static final Logger LOGGER = LoggerFactory.getLogger(EditCardListActivity.class);

    private static final int DEFAULT_CARD_SIZE = 180;

    @BindView(R.id.recycler_view)
    /* default */ RecyclerView mRecyclerView;

    @BindView(R.id.number_of_cards)
    /* default */ TextView mNumberOfCards;

    @Inject
    /* default */ EditCardListActivityPresenter mPresenter;

    private CardRecyclerViewAdapter mFirebaseAdapter;
    private RecyclerView.AdapterDataObserver mFirebaseAdapterDataObserver;
    private DeckAccess mDeckAccess;

    /**
     * Method starts EditCardListActivity.
     *
     * @param context    context from where it was called.
     * @param deckAccess deck information which cards to show.
     */
    public static void startActivity(final Context context, final DeckAccess deckAccess) {
        Intent intent = new Intent(context, EditCardListActivity.class);
        intent.putExtra(EditCardListActivity.DECK_ACCESS, new ParcelableDeckAccess(deckAccess));
        LOGGER.debug("Write deckAccess: {}", deckAccess);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_deck_activity);
        configureToolbar();
        Intent intent = getIntent();
        DeckAccess deckAccess = ParcelableDeckAccess.get(intent.getParcelableExtra(DECK_ACCESS));
        if (deckAccess == null || deckAccess.getDeck() == null) {
            finish();
        } else {
            mDeckAccess = deckAccess;
            this.setTitle(deckAccess.getDeck().getName());
            ButterKnife.bind(this);
            Injector.getEditCardListActivityInjector(deckAccess.getDeck()).inject(this);
            // use a grid layout manager
            RecyclerView.LayoutManager mLayoutManager =
                    new GridLayoutManager(this, calculateNumberOfColumns());
            mRecyclerView.setLayoutManager(mLayoutManager);
            // For better performance. The size of views won't be changed.
            mRecyclerView.setHasFixedSize(true);
            // The FirebaseRecyclerAdapter asynchronously synchronizes data from the database.
            // To know whenever the data in an adapter changes, you can register
            // an AdapterDataObserver.
            // https://stackoverflow.com/questions/37937497/getitemcount-on-adapter-is-returning-0
            mFirebaseAdapterDataObserver = new RecyclerView.AdapterDataObserver() {

                @Override
                public void onItemRangeInserted(final int positionStart, final int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    mNumberOfCards.setText(String.format(getString(R.string.number_of_cards),
                            mFirebaseAdapter.getItemCount()));
                }
            };
        }
    }

    /**
     * Calculate number of columns for GreedLayoutManager.
     * Default size of card is equivalent of DEFAULT_CARD_SIZE
     *
     * @return number of columns in GridLayout;
     */
    // https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
    private int calculateNumberOfColumns() {
        // Describes general information about a display,
        // such as its size, density, and font scaling.
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        // widthPixels  - The absolute width of the available display size in pixels.
        // density - The logical density of the display.
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) Math.max(1, dpWidth / DEFAULT_CARD_SIZE);
    }

    @OnClick(R.id.f_add_card_button)
    /* default */ void addCards() {
        if (getResources().getString(R.string.read_access).equals(mDeckAccess.getAccess())) {
            Toast.makeText(this, R.string.add_cards_with_read_access_user_warning,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        AddEditCardActivity.startAddCardActivity(this, mPresenter.getDeck());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRecyclerView.setAdapter(createAdapter(null));
    }

    @Override
    protected void onStop() {
        cleanup();
        super.onStop();
    }

    private void cleanup() {
        if (mFirebaseAdapter != null) {
            mFirebaseAdapter.unregisterAdapterDataObserver(mFirebaseAdapterDataObserver);
            mFirebaseAdapter.stopListening();
            mFirebaseAdapter = null;
        }
        mRecyclerView.setAdapter(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.edit_card_list_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.search_action);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Called when the user submits the query. This could be due to a key press on the
     * keyboard or due to pressing a submit button.
     * The listener can override the standard behavior by returning true
     * to indicate that it has handled the submit request. Otherwise return false to
     * let the SearchView handle the submission by launching any associated intent.
     *
     * @param query the query text that is to be submitted
     * @return true if the query has been handled by the listener, false to let the
     * SearchView perform the default action.
     */
    @Override
    public boolean onQueryTextSubmit(final String query) {
        return false;
    }

    /**
     * Called when the query text is changed by the user.
     *
     * @param newText the new content of the query text field.
     * @return false if the SearchView should perform the default action of showing any
     * suggestions if available, true if the action was handled by the listener.
     */
    @Override
    public boolean onQueryTextChange(final String newText) {
        mRecyclerView.setAdapter(search(newText));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCardClick(final Card card) {
        PreEditCardActivity.startActivity(this, card, mDeckAccess);
    }

    private CardRecyclerViewAdapter createAdapter(@Nullable final Query query) {
        // Before creating new Adapter, check whether we have previous one.
        // If Adapter already exists, unregister DataObserver and clean listener.
        cleanup();

        if (query == null) {
            mFirebaseAdapter = new CardRecyclerViewAdapter(mPresenter.getDeck(),
                    mPresenter.getQuery(), this);
        } else {
            mFirebaseAdapter = new CardRecyclerViewAdapter(mPresenter.getDeck(), query, this);
        }
        mFirebaseAdapter.startListening();

        // If it was got 0 cards, AdapterDataObserver won't run. Therefore it will be shown
        // the previous value.
        mNumberOfCards.setText(String.format(getString(R.string.number_of_cards),
                mFirebaseAdapter.getItemCount()));
        mFirebaseAdapter.registerAdapterDataObserver(mFirebaseAdapterDataObserver);
        return mFirebaseAdapter;
    }

    /**
     * Called when user searches in list of cards.
     *
     * @param text text to be searched
     * @return Adapter with appropriate list of cards.
     */
    @SuppressWarnings(
        /* ignore \uf8ff (largest possible) char */ "checkstyle:AvoidEscapedUnicodeCharacters"
    )
    private CardRecyclerViewAdapter search(final String text) {
        if (mPresenter.getQuery() != null) {
            return createAdapter(mPresenter.getQuery().orderByChild("front").startAt(text)
                    .endAt(text + "\uf8ff"));
        }
        return createAdapter(null);
    }
}

