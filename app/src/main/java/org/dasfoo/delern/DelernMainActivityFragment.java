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

package org.dasfoo.delern;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.dasfoo.delern.adapters.DeckRecyclerViewAdapter;
import org.dasfoo.delern.card.EditCardListActivity;
import org.dasfoo.delern.card.LearningCardsActivity;
import org.dasfoo.delern.handlers.OnDeckViewHolderClick;
import org.dasfoo.delern.listeners.AbstractOnDataChangeListener;
import org.dasfoo.delern.listeners.OnFbOperationCompleteListener;
import org.dasfoo.delern.listeners.TextWatcherStub;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckType;
import org.dasfoo.delern.models.listener.AbstractUserMessageValueEventListener;
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
    private ValueEventListener mProgressBarListener;
    private TextView mEmptyMessageTextView;
    private Query mUsersDecksQuery;
    private boolean mIsListenersAttached = true;

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
                newOrUpdateDeckDialog(new Deck(), input, R.string.add,
                        new DialogInterface.OnClickListener() {
                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                final Deck newDeck = new Deck(input.getText().toString().trim(),
                                        DeckType.BASIC.name(), true);
                                Deck.createNewDeck(newDeck,
                                        new OnFbOperationCompleteListener(TAG, getContext()),
                                        new AbstractOnDataChangeListener(TAG, getContext()) {
                                            /**
                                             * {@inheritDoc}
                                             */
                                            @Override
                                            public void onDataChange(
                                                    final DataSnapshot dataSnapshot) {
                                                startEditCardsActivity(dataSnapshot.getKey(),
                                                        newDeck.getName());
                                                mEmptyMessageTextView
                                                        .setVisibility(TextView.INVISIBLE);
                                            }
                                        });
                            }
                        });
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
        mProgressBarListener = new AbstractUserMessageValueEventListener(getContext()) {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                mEmptyMessageTextView.setVisibility(TextView.INVISIBLE);
                if (!dataSnapshot.hasChildren()) {
                    mEmptyMessageTextView.setVisibility(TextView.VISIBLE);
                }
            }
        };
        mUsersDecksQuery = Deck.getUsersDecks();
        return rootView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAdapter = new DeckRecyclerViewAdapter(Deck.class, R.layout.deck_text_view,
                DeckViewHolder.class, mUsersDecksQuery);
        mFirebaseAdapter.setContext(getContext());
        mFirebaseAdapter.setOnDeckViewHolderClick(mOnDeckViewHolderClick);
        mRecyclerView.setAdapter(mFirebaseAdapter);
        // Checks if the recyclerview is empty, ProgressBar is invisible
        // and writes message for user
        if (mUsersDecksQuery != null) {
            mUsersDecksQuery.addValueEventListener(mProgressBarListener);
        }
        mIsListenersAttached = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStop() {
        super.onStop();
        cleanup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doOnDeckClick(final int position) {
        Deck deck = getDeckFromAdapter(position);
        startShowCardActivity(deck);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doOnRenameMenuClick(final int position) {
        final Deck deck = getDeckFromAdapter(position);
        Log.d(TAG, "Deck to rename: " + deck.toString());
        final EditText input = new EditText(getActivity());
        newOrUpdateDeckDialog(deck, input, R.string.rename, new DialogInterface.OnClickListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                deck.setName(input.getText().toString().trim());
                Deck.updateDeck(deck, new OnFbOperationCompleteListener(TAG, getContext()) {
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public void onOperationSuccess() {
                        //No implementation needed
                    }
                });
            }
        });
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
                Deck.deleteDeck(deckId,
                        new OnFbOperationCompleteListener(TAG, getContext()) {
                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public void onOperationSuccess() {
                                Log.i(TAG, "Deck was removed");
                            }
                        });
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
        deck.setDeckType(deckType.name());
        Deck.updateDeck(deck, new OnFbOperationCompleteListener(TAG, getContext()) {
            /**
             * {@inheritDoc}
             */
            @Override
            public void onOperationSuccess() {
                // Not implemented yet
            }
        });
    }


    private AlertDialog newOrUpdateDeckDialog(final Deck deck, final EditText input,
                                              final int positiveButtonName,
                                              final DialogInterface.OnClickListener
                                                      positiveButtonListener) {
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
        builder.setPositiveButton(positiveButtonName, positiveButtonListener);
        final AlertDialog dialog = builder.create();
        input.addTextChangedListener(new TextWatcherStub() {
            @Override
            public void afterTextChanged(final Editable s) {
                // Check if edittext is empty, disable button. Not allow deck that
                // contains only spaces in name
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setEnabled(!TextUtils.isEmpty(s.toString().trim()));
            }
        });
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        return dialog;
    }

    private void startEditCardsActivity(final String key, final String name) {
        Intent intent = new Intent(getActivity(), EditCardListActivity.class);
        intent.putExtra(EditCardListActivity.DECK_ID, key);
        intent.putExtra(EditCardListActivity.LABEL, name);
        startActivity(intent);
    }

    private void startShowCardActivity(final Deck deck) {
        Intent intent = new Intent(getActivity(), LearningCardsActivity.class);
        intent.putExtra(LearningCardsActivity.DECK, deck);
        startActivity(intent);
    }

    private Deck getDeckFromAdapter(final int position) {
        final Deck deck = mFirebaseAdapter.getItem(position);
        deck.setdId(mFirebaseAdapter.getRef(position).getKey());
        return deck;
    }

    /**
     * Removes listeners and cleans resources.
     */
    public void cleanup() {
        if (mIsListenersAttached) {
            mIsListenersAttached = false;
            if (mUsersDecksQuery == null) {
                Log.w(TAG, "Cleanup listeners: User is not signed in");
            } else {
                mUsersDecksQuery.removeEventListener(mProgressBarListener);
            }
            mFirebaseAdapter.cleanup();
        }
    }
}
