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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.dasfoo.delern.adapters.DeckRecyclerViewAdapter;
import org.dasfoo.delern.card.EditCardListActivity;
import org.dasfoo.delern.card.ShowCardsActivity;
import org.dasfoo.delern.handlers.OnDeckViewHolderClick;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckType;
import org.dasfoo.delern.util.LogUtil;
import org.dasfoo.delern.viewholders.DeckViewHolder;

/**
 * A placeholder fragment containing a simple view.
 */
public class DelernMainActivityFragment extends Fragment implements OnDeckViewHolderClick {

    /**
     * Class information for logging.
     */
    private static final String TAG = LogUtil.tagFor(DelernMainActivityFragment.class);
    private final OnDeckViewHolderClick mOnDeckViewHolderClick = this;
    private ProgressBar mProgressBar;
    private DeckRecyclerViewAdapter mFirebaseAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.AdapterDataObserver mAdapterDataObserver;
    private ValueEventListener mProgressBarListener;
    private TextView mEmptyMessageTextView;
    private Query mUsersDecksQuery;

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.delern_main_fragment, container, false);
        mEmptyMessageTextView = (TextView) rootView.findViewById(R.id.empty_recyclerview_message);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // Set up the input
                final EditText input = new EditText(getActivity());
                AlertDialog.Builder builder = newOrUpdateDeckDialog(new Deck(), input);
                builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        Deck newDeck = new Deck(input.getText().toString());
                        newDeck.setDeckType(DeckType.BASIC.name().toLowerCase());
                        String key = Deck.createNewDeck(newDeck);
                        mEmptyMessageTextView.setVisibility(TextView.INVISIBLE);
                        startEditCardsActivity(key, newDeck.getName());
                    }
                });
                builder.show();
            }
        });
        // TODO(ksheremet): Create base fragment for mProgressBar
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .build());

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        return rootView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();
        mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(final int positionStart, final int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
            }
        };

        mProgressBarListener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                Log.v(TAG, "Progress bar");
                mEmptyMessageTextView.setVisibility(TextView.INVISIBLE);
                if (!dataSnapshot.hasChildren()) {
                    mEmptyMessageTextView.setVisibility(TextView.VISIBLE);
                }
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                Log.v(TAG, databaseError.getMessage());
            }
        };

        mUsersDecksQuery = Deck.getUsersDecks();
        mFirebaseAdapter = new DeckRecyclerViewAdapter(Deck.class, R.layout.deck_text_view,
                DeckViewHolder.class, mUsersDecksQuery);
        mFirebaseAdapter.setContext(getContext());
        mFirebaseAdapter.setOnDeckViewHolderClick(mOnDeckViewHolderClick);
        mFirebaseAdapter.registerAdapterDataObserver(mAdapterDataObserver);
        mRecyclerView.setAdapter(mFirebaseAdapter);
        // Checks if the recyclerview is empty, ProgressBar is invisible
        // and writes message for user
        if (mUsersDecksQuery != null) {
            mUsersDecksQuery.addListenerForSingleValueEvent(mProgressBarListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStop() {
        super.onStop();
        mFirebaseAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
        if (mUsersDecksQuery == null) {
            Log.v(TAG, "User is not signed in");
        } else {
            mUsersDecksQuery.removeEventListener(mProgressBarListener);
        }
        mFirebaseAdapter.cleanup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doOnTextViewClick(final int position) {
        Deck deck = getDeckFromAdapter(position);
        startShowCardActivity(deck);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doOnRenameMenuClick(final int position) {
        final Deck deck = getDeckFromAdapter(position);
        Log.v(TAG, deck.toString());
        final EditText input = new EditText(getActivity());
        AlertDialog.Builder builder = newOrUpdateDeckDialog(deck, input);
        builder.setPositiveButton(R.string.rename, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                deck.setName(input.getText().toString());
                Deck.updateDeck(deck);
            }
        });
        builder.show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doOnEditMenuClick(final int position) {
        startEditCardsActivity(mFirebaseAdapter.getRef(position).getKey(),
                mFirebaseAdapter.getItem(position).getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doOnDeleteMenuClick(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_deck);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                final String deckId = mFirebaseAdapter.getRef(position).getKey();
                Deck.deleteDeck(deckId);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doOnDeckTypeClick(final int position, final DeckType deckType) {
        final Deck deck = getDeckFromAdapter(position);
        deck.setDeckType(deckType.name().toLowerCase());
        Deck.updateDeck(deck);
    }


    private AlertDialog.Builder newOrUpdateDeckDialog(final Deck deck, final EditText input) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.deck);
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(deck.getName());
        builder.setView(input);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.cancel();
            }
        });
        return builder;
    }

    private void startEditCardsActivity(final String key, final String name) {
        Intent intent = new Intent(getActivity(), EditCardListActivity.class);
        intent.putExtra(EditCardListActivity.DECK_ID, key);
        intent.putExtra(EditCardListActivity.LABEL, name);
        startActivity(intent);
    }

    private void startShowCardActivity(final Deck deck) {
        Intent intent = new Intent(getActivity(), ShowCardsActivity.class);
        intent.putExtra(ShowCardsActivity.DECK, deck);
        startActivity(intent);
    }

    private Deck getDeckFromAdapter(final int position) {
        final Deck deck = mFirebaseAdapter.getItem(position);
        deck.setdId(mFirebaseAdapter.getRef(position).getKey());
        return deck;
    }
}
