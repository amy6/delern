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
import org.dasfoo.delern.callbacks.OnCardViewHolderClick;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.util.LogUtil;
import org.dasfoo.delern.viewholders.CardViewHolder;

public class EditCardListActivity extends AppCompatActivity implements OnCardViewHolderClick,
        SearchView.OnQueryTextListener {

    public static final String LABEL = "label";
    public static final String DECK_ID = "deckId";
    private static final String TAG = LogUtil.tagFor(EditCardListActivity.class);
    private CardRecyclerViewAdapter mFirebaseAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.AdapterDataObserver mAdapterDataObserver;
    private Query mQuery;

    private String mLabel;
    private String mDeckId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_deck_activity);
        configureToolbar();
        getInputVariables();
        this.setTitle(mLabel);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.f_add_card_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddCardsActivity(mDeckId, R.string.add);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        configureRecyclerView();

        mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAdapter.registerAdapterDataObserver(mAdapterDataObserver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    private void getInputVariables() {
        Intent intent = getIntent();
        mLabel = intent.getStringExtra(LABEL);
        mDeckId = intent.getStringExtra(DECK_ID);
    }

    private void configureRecyclerView() {
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .build());
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mQuery = Card.fetchAllCardsForDeck(mDeckId);
        try {
            mFirebaseAdapter = new CardRecyclerViewAdapter.Builder(Card.class,
                    R.layout.card_text_view_for_deck, CardViewHolder.class, mQuery)
                    .setOnClickListener(this)
                    .build();
        } catch (InstantiationException e) {
            Log.e(TAG, e.getMessage());
        }
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }

    private void startAddCardsActivity(String key, int label) {
        Intent intent = new Intent(this, AddEditCardActivity.class);
        intent.putExtra(AddEditCardActivity.DECK_ID, key);
        intent.putExtra(AddEditCardActivity.LABEL, label);
        startActivity(intent);
    }

    @Override
    public void onCardClick(int position) {
        Log.v(TAG, mFirebaseAdapter.getRef(position).getKey());
        showCardBeforeEdit(mFirebaseAdapter.getRef(position).getKey());
    }

    private void showCardBeforeEdit(String cardId) {
        Intent intent = new Intent(this, PreEditCardActivity.class);
        intent.putExtra(PreEditCardActivity.LABEL, mLabel);
        intent.putExtra(PreEditCardActivity.DECK_ID, mDeckId);
        intent.putExtra(PreEditCardActivity.CARD_ID, cardId);
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
    public boolean onQueryTextSubmit(String query) {
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
        mFirebaseAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
        try {
            mFirebaseAdapter = new CardRecyclerViewAdapter.Builder(Card.class,
                    R.layout.card_text_view_for_deck, CardViewHolder.class,
                    mQuery.orderByChild("front").startAt(newText).endAt(newText + "\uf8ff"))
                    .setOnClickListener(this)
                    .build();
        } catch (InstantiationException e) {
            Log.e(TAG, e.getMessage());
        }
        mFirebaseAdapter.registerAdapterDataObserver(mAdapterDataObserver);
        mRecyclerView.setAdapter(mFirebaseAdapter);
        return true;
    }
}
