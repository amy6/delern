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

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.dasfoo.delern.card.EditCardListActivity;
import org.dasfoo.delern.card.LearningCardsActivity;
import org.dasfoo.delern.listeners.TextWatcherStub;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.User;
import org.dasfoo.delern.presenters.DelernMainActivityPresenter;
import org.dasfoo.delern.signin.SignInActivity;
import org.dasfoo.delern.util.LogUtil;
import org.dasfoo.delern.views.IDelernMainView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Main activity of the application, containing decks and menu.
 */
public class DelernMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener, IDelernMainView {

    /**
     * IntentExtra user for showing user info and data.
     */
    public static final String USER = "user";
    private static final int REQUEST_INVITE = 1;
    private static final String TAG = LogUtil.tagFor(DelernMainActivity.class);

    @BindView(R.id.toolbar)
    /* default */ Toolbar mToolbar;
    @BindView(R.id.progress_bar)
    /* default */ ProgressBar mProgressBar;
    @BindView(R.id.recycler_view)
    /* default */ RecyclerView mRecyclerView;
    @BindView(R.id.empty_recyclerview_message)
    /* default */ TextView mEmptyMessageTextView;
    @Inject
    /* default */ DelernMainActivityPresenter mMainActivityPresenter;
    private TextView mUserNameTextView;
    private TextView mUserEmailTextView;
    private CircleImageView mProfilePhotoImageView;
    private FirebaseAnalytics mFirebaseAnalytics;
    private GoogleApiClient mGoogleApiClient;

    /**
     * Method starts DelernMainActivity.
     *
     * @param context context of Activity that called this method.
     * @param user    current user that uses app.
     */
    public static void startActivity(final Context context, final User user) {
        Intent intent = new Intent(context, DelernMainActivity.class);
        intent.putExtra(DelernMainActivity.USER, user);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delern_main_activity);
        ButterKnife.bind(this);
        DelernApplication.getMainActivityInjector(this).inject(this);

        showProgressBar();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        configureToolbar();
        Intent intent = getIntent();
        User user = intent.getParcelableExtra(USER);
        // TODO(ksheremet): finish isn't called
        if (!mMainActivityPresenter.onCreate(user)) {
            return;
        }

        initViews();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(
                        this /* Activity */,
                        this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .addApi(AppInvite.API)
                .build();
    }

    private void initViews() {
        DrawerLayout drawer = ButterKnife.findById(this, R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = ButterKnife.findById(this, R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        mProfilePhotoImageView = ButterKnife.findById(hView, R.id.profile_image);
        mUserNameTextView = ButterKnife.findById(hView, R.id.user_name);
        mUserEmailTextView = ButterKnife.findById(hView, R.id.user_email);
        mMainActivityPresenter.getUserInfo();
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .build());

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRecyclerView.setAdapter(mMainActivityPresenter.createAdapter(R.layout.deck_text_view));
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
        LogUtil.error(TAG, "Google Play Services connection failed: " +
                connectionResult.getErrorMessage());
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows user a dialog for creating deck. User should type name of deck.
     */
    @OnClick(R.id.fab)
    /* default */ void createNewDeckDialog() {
        final EditText input = new EditText(this);
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
                        mMainActivityPresenter.createNewDeck(input.getText().toString().trim());
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
        SignInActivity.startActivity(this);
        // TODO(ksheremet): finish() doesn't stop activity. It continues onCreate()
        finish();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void learnCardsInDeckClick(final Deck deck) {
        LearningCardsActivity.startActivity(this, deck);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void editCardsInDeckClick(final Deck deck) {
        EditCardListActivity.startActivity(this, deck);
    }

    /**
     * Callback method from Presenter to hide Progress Bar.
     */
    @Override
    public void hideProgressBar() {
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    /**
     * Callback method from Presenter to show Progress Bar.
     */
    @Override
    public void showProgressBar() {
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
    }

    /**
     * Callback method called from Presenter. If user doesn't have
     * decks it shows message.
     *
     * @param noDecks boolean var whether user has decks or not
     */
    @Override
    public void noDecksMessage(final Boolean noDecks) {
        if (noDecks) {
            mEmptyMessageTextView.setVisibility(TextView.VISIBLE);
        } else {
            mEmptyMessageTextView.setVisibility(TextView.INVISIBLE);
        }
    }

    /**
     * Updates user profile info in NavigationDrawer.
     *
     * @param user model User
     */
    @Override
    public void updateUserProfileInfo(final User user) {
        mUserNameTextView.setText(user.getName());
        mUserEmailTextView.setText(user.getEmail());
        Glide.with(this).load(user.getPhotoUrl()).into(mProfilePhotoImageView);
    }
}
