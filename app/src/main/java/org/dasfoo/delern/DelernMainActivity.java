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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.dasfoo.delern.models.listeners.AbstractDataAvailableListener;
import org.dasfoo.delern.adapters.DeckRecyclerViewAdapter;
import org.dasfoo.delern.card.EditCardListActivity;
import org.dasfoo.delern.card.LearningCardsActivity;
import org.dasfoo.delern.handlers.OnDeckViewHolderClick;
import org.dasfoo.delern.listeners.AbstractDataAvailableListener;
import org.dasfoo.delern.listeners.TextWatcherStub;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckType;
import org.dasfoo.delern.models.User;
import org.dasfoo.delern.presenters.DelernMainActivityPresenter;
import org.dasfoo.delern.signin.SignInActivity;
import org.dasfoo.delern.util.LogUtil;
import org.dasfoo.delern.views.IDelernMainView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Main activity of the application, containing decks and menu.
 */
public class DelernMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener, OnDeckViewHolderClick, IDelernMainView {

    private static final int REQUEST_INVITE = 1;
    private static final String TAG = LogUtil.tagFor(DelernMainActivity.class);

    private FirebaseAnalytics mFirebaseAnalytics;
    private GoogleApiClient mGoogleApiClient;
    private Toolbar mToolbar;
    private AbstractDataAvailableListener<User> mAbstractDataAvailableListener;

    //From fragment
    private final OnDeckViewHolderClick mOnDeckViewHolderClick = this;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    // TODO(refactoring): move setVisibility of this to RecyclerViewAdapter
    private TextView mEmptyMessageTextView;

    private User mUser;

    //Presenter
    DelernMainActivityPresenter mainActivityPresenter = new DelernMainActivityPresenter(this);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delern_main_activity);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        configureToolbar();
        mainActivityPresenter.onCreate();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(
                        this /* Activity */,
                        this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .addApi(AppInvite.API)
                .build();

        // From fragment
        mEmptyMessageTextView = (TextView) findViewById(R.id.empty_recyclerview_message);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // Set up the input
                final EditText input = new EditText(DelernMainActivity.this);
                // TODO(refactoring): user should be available here
                newOrUpdateDeckDialog(new Deck(new User()), input, R.string.add,
                        new DialogInterface.OnClickListener() {
                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                final Deck newDeck = new Deck(new User());
                                newDeck.setName(input.getText().toString().trim());
                                newDeck.setDeckType(DeckType.BASIC.name());
                                newDeck.setAccepted(true);
                                newDeck.create(new AbstractDataAvailableListener<Deck>(
                                        DelernMainActivity.this) {
                                    @Override
                                    public void onData(@Nullable final Deck deck) {
                                        startEditCardsActivity(deck);
                                    }
                                });
                            }
                        });
            }
        });
        // TODO(ksheremet): Create base fragment for mProgressBar
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        showProgressBar();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .build());

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        configureProfileInfo(navigationView);
        mRecyclerView.setAdapter(mainActivityPresenter.getAdapter(R.layout.deck_text_view));
        mainActivityPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeListeners();
        cleanup();
    }

    private void removeListeners() {
        mAbstractDataAvailableListener.cleanup();
    }

    /**
     * Overriding to close the drawer on Back button instead of closing an activity.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_import) {
            Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_export) {
            Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_manage) {
            Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {
            Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send) {
            Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_invite) {
            sendInvitation();
        } else if (id == R.id.nav_sign_out) {
            // If user signs out, remove all listeners in advance that can be.
            // OnStop called after a sign out. User is already signed out,
            // but listeners are attached yet, therefore log has permission denied message.
            cleanup();
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sendInvitation() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    private void signOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.sign_out_warning);
        builder.setPositiveButton(R.string.sign_out, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                removeListeners();
                User.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                signIn();
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

    private void configureToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    private void configureProfileInfo(final NavigationView navigationView) {
        View hView = navigationView.getHeaderView(0);
        final CircleImageView profilePhoto =
                (CircleImageView) hView.findViewById(R.id.profile_image);
        final TextView userName = (TextView) hView.findViewById(R.id.user_name);
        final TextView userEmail = (TextView) hView.findViewById(R.id.user_email);

        mAbstractDataAvailableListener = new AbstractDataAvailableListener<User>(this) {
            @Override
            public void onData(@Nullable final User user) {
                Log.d(TAG, "Check if user null");
                if (user == null) {
                    Log.d(TAG, "Starting sign in");
                    signIn();
                    finish();
                } else {
                    mUser = user;
                    userName.setText(user.getName());
                    userEmail.setText(user.getEmail());
                    Glide.with(DelernMainActivity.this).load(user.getPhotoUrl()).into(profilePhoto);
                }
            }
        };

        ((User) getIntent().getParcelableExtra(DelernMainActivityFragment.USER))
                .watch(mAbstractDataAvailableListener, User.class);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "sent");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE,
                        payload);
            } else {
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "not sent");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE,
                        payload);
                Toast.makeText(this, R.string.invitation_failed_message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Notify the user that sign in has permanently failed.
     *
     * @param connectionResult information about unsuccessful connection attempt
     */
    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.e(TAG, "onConnectionFailed:" + connectionResult);
        Crashlytics.log(Log.ERROR, TAG, connectionResult.getErrorMessage());
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    //From Fragment
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
        final EditText input = new EditText(this);
        newOrUpdateDeckDialog(deck, input, R.string.rename, new DialogInterface.OnClickListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                deck.setName(input.getText().toString().trim());
                deck.save(null);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doOnEditMenuClick(final int position) {
        startEditCardsActivity(mFirebaseAdapter.getItem(position));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doOnDeleteMenuClick(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_deck);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                Deck deck = mFirebaseAdapter.getItem(position);
                deck.delete();
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
        deck.save(null);
    }


    private AlertDialog newOrUpdateDeckDialog(final Deck deck, final EditText input,
                                              final int positiveButtonName,
                                              final DialogInterface.OnClickListener
                                                      positiveButtonListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    private void startEditCardsActivity(final Deck deck) {
        Intent intent = new Intent(this, EditCardListActivity.class);
        intent.putExtra(EditCardListActivity.DECK, deck);
        startActivity(intent);
    }

    private void startShowCardActivity(final Deck deck) {
        Intent intent = new Intent(this, LearningCardsActivity.class);
        intent.putExtra(LearningCardsActivity.DECK, deck);
        startActivity(intent);
    }

    private Deck getDeckFromAdapter(final int position) {
        return mFirebaseAdapter.getItem(position);
    }

    /**
     * Removes listeners and cleans resources.
     */
    public void cleanup() {
        mFirebaseAdapter.cleanup();
        mainActivityPresenter.cleanup();

    }

    @Override
    public void signIn() {
        // Per https://goo.gl/qHTbjw and https://goo.gl/rnD2g3.
        Intent signInIntent = new Intent(this, SignInActivity.class);
        signInIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(signInIntent);
        finish();
    }

    @Override
    public void learnCardsInDeck(final Deck deck) {

    }

    @Override
    public void hideProgressBar() {
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    public void showProgressBar() {
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    public void noDecksMessage(Boolean noDecks) {
        if (noDecks) {
            mEmptyMessageTextView.setVisibility(TextView.VISIBLE);
        } else {
            mEmptyMessageTextView.setVisibility(TextView.INVISIBLE);
        }
    }
}
