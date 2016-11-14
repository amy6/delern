package org.dasfoo.delern.card;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.dasfoo.delern.BaseActivity;
import org.dasfoo.delern.R;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.viewholders.CardViewHolder;

public class EditCardListActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter<Card, CardViewHolder> mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String message = intent.getStringExtra("label");
        String deckId = intent.getStringExtra("deckId");
        this.setTitle(message+":"+deckId);

        enableToolbarArrow(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.f_add_card_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .build());
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Card, CardViewHolder>(
                Card.class,
                R.layout.card_text_view_forlist,
                CardViewHolder.class,
                firebaseController.getAllCardsForDeck(deckId)) {

            @Override
            protected void populateViewHolder(CardViewHolder cardViewHolder, Card card, int position) {
                cardViewHolder.getmFrontTextView().setText(card.getFront());
                cardViewHolder.getmBackTextView().setText(card.getBack());
            }
        };

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
        return R.layout.activity_edit_card_list;
    }

}
