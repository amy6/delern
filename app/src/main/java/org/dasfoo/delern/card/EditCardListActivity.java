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

package org.dasfoo.delern.card;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.Query;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.dasfoo.delern.R;
import org.dasfoo.delern.adapters.CardRecyclerViewAdapter;
import org.dasfoo.delern.handlers.OnCardViewHolderClick;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.util.LogUtil;
import org.dasfoo.delern.viewholders.CardViewHolder;

/**
 * Activity to view and edit all cards in the deck.
 */
public class EditCardListActivity extends AppCompatActivity implements OnCardViewHolderClick,
        SearchView.OnQueryTextListener {

    /**
     * IntentExtra deck to edit.
     */
    public static final String DECK = "deck";

    private static final String TAG = LogUtil.tagFor(EditCardListActivity.class);
    private CardRecyclerViewAdapter mFirebaseAdapter;
    private RecyclerView mRecyclerView;
    private Query mQuery;

    private Deck mDeck;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_deck_activity);
        configureToolbar();
        getParameters();
        this.setTitle(mDeck.getName());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.f_add_card_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startAddCardsActivity();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .build());
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mQuery = mDeck.getChildReference(Card.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        configureFirebaseAdapter();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAdapter.cleanup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.edit_card_list_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.search_action);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getParameters() {
        Intent intent = getIntent();
        mDeck = intent.getParcelableExtra(DECK);
    }

    private void configureFirebaseAdapter() {
        try {
            mFirebaseAdapter = new CardRecyclerViewAdapter.Builder(Card.class,
                    R.layout.card_text_view_for_deck, CardViewHolder.class, mQuery, mDeck)
                    .setOnClickListener(this)
                    .build();
        } catch (InstantiationException e) {
            Log.e(TAG, e.getMessage());
        }
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }

    private void startAddCardsActivity() {
        Intent intent = new Intent(this, AddEditCardActivity.class);
        Card card = new Card(mDeck);
        intent.putExtra(AddEditCardActivity.CARD, card);
        startActivity(intent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCardClick(final int position) {
        showCardBeforeEdit(mFirebaseAdapter.getItem(position));
    }

    private void showCardBeforeEdit(final Card card) {
        Intent intent = new Intent(this, PreEditCardActivity.class);
        intent.putExtra(PreEditCardActivity.CARD, card);
        startActivity(intent);
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
    @SuppressWarnings("checkstyle:AvoidEscapedUnicodeCharacters")
    @Override
    public boolean onQueryTextChange(final String newText) {
        try {
            mFirebaseAdapter = new CardRecyclerViewAdapter.Builder(Card.class,
                    R.layout.card_text_view_for_deck, CardViewHolder.class,
                    // The \uf8ff character used in the query is a very high code point in
                    // the Unicode range. Because it is after most regular characters in Unicode,
                    // the query matches all values that start with a newText.
                    mQuery.orderByChild("front").startAt(newText).endAt(newText + "\uf8ff"), mDeck)
                    .setOnClickListener(this)
                    .build();
        } catch (InstantiationException e) {
            Log.e(TAG, e.getMessage());
        }
        mRecyclerView.setAdapter(mFirebaseAdapter);
        return true;
    }
}

