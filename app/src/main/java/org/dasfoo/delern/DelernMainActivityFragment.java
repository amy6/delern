package org.dasfoo.delern;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.dasfoo.delern.adapters.DeckRecyclerViewAdapter;
import org.dasfoo.delern.callbacks.OnDeckViewHolderClick;
import org.dasfoo.delern.card.EditCardListActivity;
import org.dasfoo.delern.card.ShowCardsActivity;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.util.LogUtil;
import org.dasfoo.delern.viewholders.DeckViewHolder;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DelernMainActivityFragment extends Fragment implements OnDeckViewHolderClick {

    /**
     * Class information for logging.
     */
    private static final String TAG = LogUtil.tagFor(DelernMainActivityFragment.class);
    private OnDeckViewHolderClick onDeckViewHolderClick = this;
    private ProgressBar mProgressBar;
    private DeckRecyclerViewAdapter mFirebaseAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.AdapterDataObserver mAdapterDataObserver;
    private ValueEventListener mProgressBarListener;
    private TextView mEmptyMessageTextView;
    private Query mUsersDecksQuery;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.delern_main_fragment, container, false);
        mEmptyMessageTextView = (TextView) rootView.findViewById(R.id.empty_recyclerview_message);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set up the input
                final EditText input = new EditText(getActivity());
                AlertDialog.Builder builder = newOrUpdateDeckDialog(new Deck(), input);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Deck newDeck = new Deck(input.getText().toString());
                        String key = Deck.createNewDeck(newDeck);
                        mEmptyMessageTextView.setVisibility(TextView.INVISIBLE);
                        startEditCardsActivity(key, newDeck.getName());
                    }
                });
                builder.show();
            }
        });
        // TODO(ksheremet): Create base fragment for mProgressBar
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .build());

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        return rootView;
    }

    /**
     * Called when the Fragment is visible to the user.  This is generally
     * tied to { Activity#onStart() Activity.onStart} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStart() {
        super.onStart();
        mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
            }
        };

        mProgressBarListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                Log.v(TAG, "Progress bar");
                mEmptyMessageTextView.setVisibility(TextView.INVISIBLE);
                if (!dataSnapshot.hasChildren()) {
                    mEmptyMessageTextView.setVisibility(TextView.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v(TAG, databaseError.getMessage());
            }
        };


        mFirebaseAdapter = new DeckRecyclerViewAdapter(Deck.class, R.layout.deck_text_view,
                DeckViewHolder.class, Deck.getUsersDecks());
        mFirebaseAdapter.setContext(getContext());
        mFirebaseAdapter.setOnDeckViewHolderClick(onDeckViewHolderClick);
        mFirebaseAdapter.registerAdapterDataObserver(mAdapterDataObserver);
        mRecyclerView.setAdapter(mFirebaseAdapter);
        // Checks if the recyclerview is empty, ProgressBar is invisible
        // and writes message for user
        mUsersDecksQuery = Deck.getUsersDecks();
        if (mUsersDecksQuery != null) {
           mUsersDecksQuery.addListenerForSingleValueEvent(mProgressBarListener);
        }
    }

    /**
     * Called when the Fragment is no longer started.  This is generally
     * tied to { Activity#onStop() Activity.onStop} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStop() {
        super.onStop();
        mFirebaseAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
        if (mUsersDecksQuery != null) {
            mUsersDecksQuery.removeEventListener(mProgressBarListener);
        } else {
            Log.v(TAG, "User is not signed in");
        }
    }

    @Override
    public void doOnTextViewClick(final int position) {
        final String deckId = mFirebaseAdapter.getRef(position).getKey();
        Query query = Card.fetchCardsFromDeckToRepeat(deckId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<Card> cards = new ArrayList<>();
                for (DataSnapshot cardSnapshot : dataSnapshot.getChildren()) {
                    final Card card = cardSnapshot.getValue(Card.class);
                    card.setcId(cardSnapshot.getKey());
                    Log.v(TAG, card.toString());
                    cards.add(card);
                }
                if (cards.size() != 0) {
                    startShowCardActivity(mFirebaseAdapter.getItem(position).getName(), deckId, cards);
                } else {
                    Toast.makeText(getContext(), R.string.no_card_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    @Override
    public void doOnRenameMenuClick(final int position) {
        final Deck deck = mFirebaseAdapter.getItem(position);
        deck.setdId(mFirebaseAdapter.getRef(position).getKey());
        Log.v(TAG, deck.toString());
        final EditText input = new EditText(getActivity());
        AlertDialog.Builder builder = newOrUpdateDeckDialog(deck, input);
        builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deck.setName(input.getText().toString());
                Deck.renameDeck(deck);
            }
        });
        builder.show();
    }

    @Override
    public void doOnEditMenuClick(final int position) {
        startEditCardsActivity(mFirebaseAdapter.getRef(position).getKey(),
                mFirebaseAdapter.getItem(position).getName());
    }

    /**
     * Deletes deck with all cards.
     *
     * @param position of deck in RecyclerView
     */
    @Override
    public void doOnDeleteMenuClick(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Delete deck with all cards!");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String deckId = mFirebaseAdapter.getRef(position).getKey();
                Deck.deleteDeck(deckId);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private AlertDialog.Builder newOrUpdateDeckDialog(Deck deck, EditText input) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Deck");
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(deck.getName());
        builder.setView(input);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder;
    }

    private void startEditCardsActivity(String key, String name) {
        Intent intent = new Intent(getActivity(), EditCardListActivity.class);
        intent.putExtra(EditCardListActivity.DECK_ID, key);
        intent.putExtra(EditCardListActivity.LABEL, name);
        startActivity(intent);
    }

    private void startShowCardActivity(String label, String deckId, ArrayList<Card> cards) {
        Intent intent = new Intent(getActivity(), ShowCardsActivity.class);
        intent.putExtra(ShowCardsActivity.DECK_ID, deckId);
        intent.putExtra(ShowCardsActivity.CARDS, cards);
        intent.putExtra(ShowCardsActivity.LABEL, label);
        startActivity(intent);
    }
}