package org.dasfoo.delern.card;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.dasfoo.delern.BaseActivity;
import org.dasfoo.delern.R;
import org.dasfoo.delern.adapters.CardRecyclerViewAdapter;
import org.dasfoo.delern.callbacks.OnCardViewHolderClick;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.util.LogUtil;
import org.dasfoo.delern.viewholders.CardViewHolder;

public class EditCardListActivity extends BaseActivity implements OnCardViewHolderClick {

    public static final String LABEL = "label";
    public static final String DECK_ID = "deckId";
    private static final String TAG = LogUtil.tagFor(EditCardListActivity.class);
    CardRecyclerViewAdapter mFirebaseAdapter;
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
        Intent intent = new Intent(this, AddCardActivity.class);
        intent.putExtra(AddCardActivity.DECK_ID, key);
        intent.putExtra(AddCardActivity.LABEL, label);
        startActivity(intent);
    }

    @Override
    public void onCardClick(int position) {
        Log.v(TAG, "Position:" + position);
        Query query = Card.getCardById(mDeckId, mFirebaseAdapter.getRef(position).getKey());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Card card = null;
                for (DataSnapshot cardSnapshot : dataSnapshot.getChildren()) {
                    Log.v(TAG, cardSnapshot.toString());
                    card = cardSnapshot.getValue(Card.class);
                    card.setcId(cardSnapshot.getKey());
                    Log.v(TAG, card.toString());
                }
                if (card != null) {
                    showCardForEdit(card);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v(TAG, databaseError.getMessage());
            }
        });
    }

    private void showCardForEdit(Card card) {
        Intent intent = new Intent(this, PreEditCardActivity.class);
        intent.putExtra(PreEditCardActivity.LABEL, mLabel);
        intent.putExtra(PreEditCardActivity.DECK_ID, mDeckId);
        intent.putExtra(PreEditCardActivity.CARD, card);
        startActivity(intent);
    }
}
