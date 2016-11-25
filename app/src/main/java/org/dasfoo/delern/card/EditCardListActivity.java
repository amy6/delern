package org.dasfoo.delern.card;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.dasfoo.delern.BaseActivity;
import org.dasfoo.delern.R;
import org.dasfoo.delern.adapters.CardRecyclerViewAdapter;
import org.dasfoo.delern.callbacks.OnCardViewHolderClick;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.viewholders.CardViewHolder;

public class EditCardListActivity extends BaseActivity implements OnCardViewHolderClick {

    public static final String LABEL = "label";
    public static final String DECK_ID = "deckId";
    private static final String TAG = LogUtil.tagFor(EditCardListActivity.class);
    private CardRecyclerViewAdapter mFirebaseAdapter;
    private String mLabel;
    private String mDeckId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mLabel = intent.getStringExtra(LABEL);
        mDeckId = intent.getStringExtra(DECK_ID);
        this.setTitle(mLabel);

        enableToolbarArrow(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.f_add_card_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddCardsActivity(mDeckId, mLabel);
            }
        });

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .build());
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mFirebaseAdapter = new CardRecyclerViewAdapter(Card.class, R.layout.card_text_view_for_deck,
                CardViewHolder.class, Card.fetchAllCardsForDeck(mDeckId));
        mFirebaseAdapter.setOnCardViewHolderClick(this);

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
            }
        });

        mRecyclerView.setAdapter(mFirebaseAdapter);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.show_deck_activity;
    }

    private void startAddCardsActivity(String key, String label) {
        Intent intent = new Intent(this, AddEditCardActivity.class);
        intent.putExtra(AddEditCardActivity.DECK_ID, key);
        intent.putExtra(AddEditCardActivity.LABEL, label);
        startActivity(intent);
    }

    @Override
    public void onCardClick(int position) {
        Log.v(TAG, "Position:" + position);
        showCardForEdit(mFirebaseAdapter.getRef(position).getKey());
    }

    private void showCardForEdit(String cardId) {
        Intent intent = new Intent(this, PreEditCardActivity.class);
        intent.putExtra(PreEditCardActivity.LABEL, mLabel);
        intent.putExtra(PreEditCardActivity.DECK_ID, mDeckId);
        intent.putExtra(PreEditCardActivity.CARD_ID, cardId);
        startActivity(intent);
    }
}
