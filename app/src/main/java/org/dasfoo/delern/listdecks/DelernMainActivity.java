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

package org.dasfoo.delern.listdecks;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.dasfoo.delern.AbstractActivity;
import org.dasfoo.delern.R;
import org.dasfoo.delern.SplashScreenActivity;
import org.dasfoo.delern.aboutapp.AboutAppActivity;
import org.dasfoo.delern.addupdatecard.AddEditCardActivity;
import org.dasfoo.delern.addupdatecard.TextWatcherStub;
import org.dasfoo.delern.di.Injector;
import org.dasfoo.delern.editdeck.EditDeckActivity;
import org.dasfoo.delern.learncards.LearningCardsActivity;
import org.dasfoo.delern.listcards.EditCardListActivity;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckAccess;
import org.dasfoo.delern.models.ParcelableUser;
import org.dasfoo.delern.models.User;
import org.dasfoo.delern.models.helpers.ServerConnection;
import org.dasfoo.delern.sharedeck.ShareDeckActivity;
import org.dasfoo.delern.util.PerfEventTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Main activity of the application, containing decks and menu.
 */
@SuppressWarnings("PMD.TooManyMethods" /* TODO(dotdoom): refactor */)
public class DelernMainActivity extends AbstractActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        IDelernMainView, OnDeckAction {

    /**
     * IntentExtra user for showing user info and data.
     */
    public static final String USER = "user";
    private static final int REQUEST_INVITE = 1;
    private static final Logger LOGGER = LoggerFactory.getLogger(DelernMainActivity.class);

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

    /**
     * Method starts DelernMainActivity.
     *
     * @param context context of Activity that called this method.
     * @param user    current user that uses app.
     */
    public static void startActivity(final Context context, final User user) {
        Intent intent = new Intent(context, DelernMainActivity.class);
        intent.putExtra(DelernMainActivity.USER, new ParcelableUser(user));
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delern_main_activity);
        ButterKnife.bind(this);
        Injector.getMainActivityInjector(this).inject(this);

        showProgressBar(true);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        Intent intent = getIntent();
        User user = ParcelableUser.get(intent.getParcelableExtra(USER));

        // TODO(ksheremet): finish isn't called
        if (!mMainActivityPresenter.onCreate(user)) {
            return;
        }

        initViews();
    }

    private void initViews() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        ServerConnection.setOnlineStatusWatcher(online -> {
            int color;
            if (online) {
                color = R.color.onlineToolbarIconColor;
            } else {
                color = R.color.offlineToolbarIconColor;
            }
            mToolbar.getNavigationIcon().setColorFilter(
                    ContextCompat.getColor(this, color),
                    PorterDuff.Mode.SRC_IN);
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        mProfilePhotoImageView = hView.findViewById(R.id.profile_image);
        mUserNameTextView = hView.findViewById(R.id.user_name);
        mUserEmailTextView = hView.findViewById(R.id.user_email);
        mMainActivityPresenter.getUserInfo();
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .build());

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(new DeckRecyclerViewAdapter(mMainActivityPresenter.getUser(),
                this, this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMainActivityPresenter.onStart();
    }

    @Override
    protected void onStop() {
        mMainActivityPresenter.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // Stop memory leaks.
        mRecyclerView.setAdapter(null);
        super.onDestroy();
    }

    /**
     * Overriding to close the drawer on Back button instead of closing an activity.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(/* TODO(ksheremet): why? */ "deprecation")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_invite:
                PerfEventTracker.trackEvent(PerfEventTracker.Event.INVITE, this, null);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.invitation_title));
                intent.putExtra(Intent.EXTRA_TEXT,
                        Html.fromHtml(getString(R.string.invitation_email_html_content)));
                startActivityForResult(intent, REQUEST_INVITE);
                break;
            case R.id.nav_sign_out:
                signOut();
                break;
            case R.id.nav_contact_us:
                contactUs();
                break;
            case R.id.nav_about:
                AboutAppActivity.startActivity(this);
                break;
            default:
                LOGGER.warn("Not implemented: {}", item.getItemId());
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.sign_out_warning)
                .setPositiveButton(R.string.sign_out, (dialog, which) -> {
                    mMainActivityPresenter.cleanup();
                    org.dasfoo.delern.models.Auth.signOut();
                    signIn();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel())
                .show();
    }

    private void contactUs() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        // Only email apps should handle this
        intent.setData(Uri.parse("mailto:"));
        // It must be an array. In another way, the recipient is empty.
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.developers_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_email_subject));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent,
                    getString(R.string.send_email_intent_chooser_message)));
            return;
        }
        Toast.makeText(this, R.string.install_email_app_user_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LOGGER.debug("onActivityResult: requestCode={}, resultCode={}", requestCode, resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "invite friend");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE,
                        payload);
                Toast.makeText(this, R.string.invitation_sent_message, Toast.LENGTH_SHORT).show();
            } else {
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "cancel invite friend");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE,
                        payload);
                Toast.makeText(this, R.string.invitation_failed_message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Shows user a dialog for creating deck. User should type name of deck.
     */
    @OnClick(R.id.fab)
    /* default */ void createNewDeckDialog() {
        final EditText input = new EditText(this);
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        // TODO(ksheremet): un-duplicate this code (see DeckViewHolder).
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.deck)
                .setView(input)
                .setNegativeButton(R.string.cancel, (dialogCancel, which) -> dialogCancel.cancel())
                .setPositiveButton(R.string.add, (dialogCreate, which) -> {
                    PerfEventTracker.trackEventStart(PerfEventTracker.Event.DECK_CREATE, this, null,
                            this);
                    mMainActivityPresenter.createNewDeck(input.getText().toString().trim());
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
        if (dialog.getWindow() != null) {
            dialog.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    /**
     * Method  starts SplashScreeActivity if user is not Signed In.
     */
    @Override
    public void signIn() {
        SplashScreenActivity.startActivity(this);
        // TODO(ksheremet): finish() doesn't stop activity. It continues onCreate()
        finish();
    }

    /**
     * Callback method from Presenter to show/hide Progress Bar.
     *
     * @param toShow true if to show. Otherwise false.
     */
    @Override
    public void showProgressBar(final Boolean toShow) {
        if (toShow) {
            PerfEventTracker.trackEventStart(PerfEventTracker.Event.DECKS_LOAD, this, null,
                    this);
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
        } else {
            PerfEventTracker.trackEventFinish(PerfEventTracker.Event.DECKS_LOAD);
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        }
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
        // TODO(dotdoom): fix User and make this work!
        mUserEmailTextView.setText("");
        Picasso.with(this).load(user.getPhotoUrl())
                .error(android.R.color.holo_orange_dark).into(mProfilePhotoImageView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCardsToDeck(final Deck deck) {
        PerfEventTracker.trackEventFinish(PerfEventTracker.Event.DECK_CREATE);
        AddEditCardActivity.startAddCardActivity(this, deck);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void editDeckSettings(final DeckAccess deckAccess) {
        PerfEventTracker.trackEvent(PerfEventTracker.Event.DECK_SETTINGS_OPEN, this, null);
        EditDeckActivity.startActivity(this, deckAccess);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shareDeck(final DeckAccess deckAccess) {
        if (getString(R.string.owner_access).equals(deckAccess.getAccess())) {
            ShareDeckActivity.startActivity(this, deckAccess.getDeck());
            return;
        }
        Toast.makeText(this, getString(R.string.share_cards_with_no_access_user_warning),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void learnDeck(final DeckAccess deckAccess) {
        LearningCardsActivity.startActivity(this, deckAccess);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void editDeck(final DeckAccess deckAccess) {
        EditCardListActivity.startActivity(this, deckAccess);
    }
}
