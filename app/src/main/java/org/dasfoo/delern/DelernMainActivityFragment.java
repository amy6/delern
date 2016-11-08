package org.dasfoo.delern;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.dasfoo.delern.callbacks.OnDeckViewHolderClick;
import org.dasfoo.delern.card.AddNewCardFragment;
import org.dasfoo.delern.card.ShowCardsFragment;
import org.dasfoo.delern.controller.FirebaseController;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.viewholders.DeckViewHolder;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DelernMainActivityFragment extends Fragment implements OnDeckViewHolderClick {

    private static final String TAG = DelernMainActivityFragment.class.getSimpleName();
    private OnDeckViewHolderClick onDeckViewHolderClick = this;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter<Deck, DeckViewHolder> mFirebaseAdapter;
    private FirebaseController firebaseController = FirebaseController.getInstance();

    public DelernMainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_delern_main, container, false);
        // TODO(ksheremet) : move logic in separate class
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Deck");
                // Set up the input
                final EditText input = new EditText(getActivity());
                // Specify the type of input expected
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Deck newDeck = new Deck(input.getText().toString());
                        firebaseController.createNewDeck(newDeck);
                        rootView.findViewById(R.id.empty_recyclerview_message)
                                .setVisibility(TextView.INVISIBLE);
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
        });
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_recycler_view);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .build());

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        //TODO(ksheremet): Move adapter to package adapters
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Deck, DeckViewHolder>(
                Deck.class,
                R.layout.card_text_view,
                DeckViewHolder.class,
                firebaseController.getUsersDecks()) {

            @Override
            protected void populateViewHolder(DeckViewHolder viewHolder, Deck deck, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.getmDesktopTextView().setText(deck.getName());
                viewHolder.setOnViewClick(onDeckViewHolderClick);
                viewHolder.setContext(getContext());
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
            }
        });

        //add the listener for the single value event that will function
        //like a completion listener for initial data load of the FirebaseRecyclerAdapter
        // Checks if the recyclerview is empty, ProgressBar is invisible
        // and writes message for user
        firebaseController.getFirebaseDecksRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                rootView.findViewById(R.id.empty_recyclerview_message)
                        .setVisibility(TextView.INVISIBLE);
                if (!dataSnapshot.hasChildren()) {
                    rootView.findViewById(R.id.empty_recyclerview_message)
                            .setVisibility(TextView.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v(TAG, databaseError.getMessage());
            }
        });

        mRecyclerView.setAdapter(mFirebaseAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void doOnAddCardButtonClick(int position) {
        AddNewCardFragment newCardFragment = AddNewCardFragment.newInstance(mFirebaseAdapter.getRef(position).getKey());
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newCardFragment);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void doOnTextViewClick(int position) {
        final String deckId = mFirebaseAdapter.getRef(position).getKey();
        Query query = firebaseController.getCardsFromDeckToRepeat(deckId);
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
                    // TODO(ksheremet): Move to separate method
                    ShowCardsFragment newFragment = ShowCardsFragment.newInstance(cards, deckId);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                            .beginTransaction();
                    // Replace whatever is in the fragment_container view with this fragment,
                    // and add the transaction to the back stack so the user can navigate back
                    transaction.replace(R.id.fragment_container, newFragment);
                    transaction.addToBackStack(null);
                    // Commit the transaction
                    transaction.commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    @Override
    public void doOnRenameMenuClick(int position) {
        Log.v(TAG, "Rename:" + position);
    }

    @Override
    public void doOnEditMenuClick(int position) {
        Log.v(TAG, "Edit:" + position);
    }

    @Override
    public void doOnDeleteMenuClick(int position) {
        Log.v(TAG, "Delete:" + position);
    }
}
