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
import org.dasfoo.delern.listeners.TextWatcherStub;
import org.dasfoo.delern.models.Deck;
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
        GoogleApiClient.OnConnectionFailedListener, IDelernMainView {

    private static final int REQUEST_INVITE = 1;
    private static final String TAG = LogUtil.tagFor(DelernMainActivity.class);

    private DelernMainActivityPresenter mMainActivityPresenter =
            new DelernMainActivityPresenter(this);
    private FirebaseAnalytics mFirebaseAnalytics;
    private GoogleApiClient mGoogleApiClient;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private TextView mEmptyMessageTextView;
    private TextView mUserNameTextView;
    private TextView mUserEmailTextView;
    private CircleImageView mProfilePhotoImageView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delern_main_activity);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        configureToolbar();
        mMainActivityPresenter.onCreate();

        Crashlytics.setUserIdentifier(User.getCurrentUser().getUid());
        initViews();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(
                        this /* Activity */,
                        this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .addApi(AppInvite.API)
                .build();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                createNewDeckDialog();

            }
        });
    }

    private void initViews() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        mProfilePhotoImageView = (CircleImageView) hView.findViewById(R.id.profile_image);
        mUserNameTextView = (TextView) hView.findViewById(R.id.user_name);
        mUserEmailTextView = (TextView) hView.findViewById(R.id.user_email);
        mMainActivityPresenter.getUserInfo();

        mEmptyMessageTextView = (TextView) findViewById(R.id.empty_recyclerview_message);

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
        mRecyclerView.setAdapter(mMainActivityPresenter.getAdapter(R.layout.deck_text_view));
        mMainActivityPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMainActivityPresenter.onStop();
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
        new AlertDialog.Builder(this)
                .setMessage(R.string.sign_out_warning)
                .setPositiveButton(R.string.sign_out, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        mMainActivityPresenter.cleanup();
                        User.signOut();
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                        signIn();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void configureToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
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

    /**
     * Shows user a dialog for creating deck. User should type name of deck.
     */
    private void createNewDeckDialog() {
        final EditText input = new EditText(DelernMainActivity.this);
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.deck)
                .setView(input)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        mMainActivityPresenter.creteNewDeck(input.getText().toString().trim());
                    }
                })
                .create();
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
    }

    /**
     * Method  starts SignInActivity if user is not Signed In.
     */
    @Override
    public void signIn() {
        // Per https://goo.gl/qHTbjw and https://goo.gl/rnD2g3.
        Intent signInIntent = new Intent(this, SignInActivity.class);
        signInIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(signInIntent);
        finish();
    }

    @Override
    public void learnCardsInDeckClick(final Deck deck) {
        Intent intent = new Intent(this, LearningCardsActivity.class);
        intent.putExtra(LearningCardsActivity.DECK, deck);
        startActivity(intent);
    }

    @Override
    public void editCardsInDeckClick(final Deck deck) {
        Intent intent = new Intent(this, EditCardListActivity.class);
        intent.putExtra(EditCardListActivity.DECK, deck);
        startActivity(intent);
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
    public void noDecksMessage(final Boolean noDecks) {
        if (noDecks) {
            mEmptyMessageTextView.setVisibility(TextView.VISIBLE);
        } else {
            mEmptyMessageTextView.setVisibility(TextView.INVISIBLE);
        }
    }

    @Override
    public void updateUserProfileInfo(final User user) {
        mUserNameTextView.setText(user.getName());
        mUserEmailTextView.setText(user.getEmail());
        Glide.with(DelernMainActivity.this).load(user.getPhotoUrl()).into(mProfilePhotoImageView);
    }
}
