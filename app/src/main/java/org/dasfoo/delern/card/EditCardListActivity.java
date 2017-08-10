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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.Query;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.dasfoo.delern.R;
import org.dasfoo.delern.adapters.CardRecyclerViewAdapter;
import org.dasfoo.delern.di.Injector;
import org.dasfoo.delern.handlers.OnCardViewHolderClick;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.presenters.EditCardListActivityPresenter;

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
    public static final String DECK = "deck";

    @BindView(R.id.recycler_view)
    /* default */ RecyclerView mRecyclerView;

    @BindView(R.id.total_number_of_cards)
    /* default */ TextView mTotalNumberOfCards;

    @Inject
    /* default */ EditCardListActivityPresenter mPresenter;

    private CardRecyclerViewAdapter mFirebaseAdapter;
    private RecyclerView.AdapterDataObserver mFirebaseAdapterDataObserver;

    /**
     * Method starts EditCardListActivity.
     *
     * @param context context from where it was called.
     * @param deck    deck which cards to show.
     */
    public static void startActivity(final Context context, final Deck deck) {
        Intent intent = new Intent(context, EditCardListActivity.class);
        intent.putExtra(EditCardListActivity.DECK, deck);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_deck_activity);
        configureToolbar();
        Intent intent = getIntent();
        Deck deck = intent.getParcelableExtra(DECK);
        this.setTitle(deck.getName());
        ButterKnife.bind(this);
        Injector.getEditCardListActivityInjector().inject(this);

        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .build());
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // For better performance
        // https://stackoverflow.com/questions/28709220/understanding-recyclerview-sethasfixedsize
        mRecyclerView.hasFixedSize();
        // The FirebaseRecyclerAdapter asynchronously synchronizes data from the database.
        // To know whenever the data in an adapter changes, you can register an AdapterDataObserver.
        // https://stackoverflow.com/questions/37937497/getitemcount-on-adapter-is-returning-0
        mFirebaseAdapterDataObserver = new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeInserted(final int positionStart, final int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                mTotalNumberOfCards.setText(String.valueOf(mFirebaseAdapter.getItemCount()));
            }
        };
        mPresenter.onCreate(deck);
    }

    @OnClick(R.id.f_add_card_button)
    /* default */ void addCards() {
        AddEditCardActivity.startAddCardActivity(this, mPresenter.getDeck());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRecyclerView.setAdapter(createAdapter(null));
    }

    @Override
    protected void onStop() {
        super.onStop();
        cleanup();
    }

    private void cleanup() {
        if (mFirebaseAdapter != null) {
            mFirebaseAdapter.unregisterAdapterDataObserver(mFirebaseAdapterDataObserver);
            mFirebaseAdapter.cleanup();
            mFirebaseAdapter = null;
        }
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
    @SuppressWarnings("checkstyle:AvoidEscapedUnicodeCharacters")
    @Override
    public boolean onQueryTextChange(final String newText) {
        mRecyclerView.setAdapter(search(newText));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCardClick(final int position) {
        PreEditCardActivity.startActivity(this, mFirebaseAdapter.getItem(position));
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

        mFirebaseAdapter.registerAdapterDataObserver(mFirebaseAdapterDataObserver);
        return mFirebaseAdapter;
    }

    /**
     * Called when user searches in list of cards.
     *
     * @param text text to be searched
     * @return Adapter with appropriate list of cards.
     */
    @SuppressWarnings("checkstyle:AvoidEscapedUnicodeCharacters")
    private CardRecyclerViewAdapter search(final String text) {
        if (mPresenter.getQuery() != null) {
            return createAdapter(mPresenter.getQuery().orderByChild("front").startAt(text)
                    .endAt(text + "\uf8ff"));
        }
        return createAdapter(null);
    }
}

