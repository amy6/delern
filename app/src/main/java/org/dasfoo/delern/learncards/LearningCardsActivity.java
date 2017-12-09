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

package org.dasfoo.delern.learncards;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import org.dasfoo.delern.R;
import org.dasfoo.delern.addupdatecard.AddEditCardActivity;
import org.dasfoo.delern.di.Injector;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.DeckAccess;
import org.dasfoo.delern.models.ParcelableDeckAccess;
import org.dasfoo.delern.util.Animation;
import org.dasfoo.delern.util.CardColor;
import org.dasfoo.delern.util.GrammaticalGenderSpecifier;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity for showing cards to learn.
 */
public class LearningCardsActivity extends AppCompatActivity implements ILearningCardsView {

    /**
     * IntentExtra deck for this activity.
     */
    public static final String DECK_ACCESS = "deck_access";

    /**
     * Key for saving onSaveInstanceState.
     */
    private static final String BACK_IS_SHOWN_KEY = "back";
    /**
     * Number of learned cards for saving onSaveInstanceState.
     */
    private static final String LEARNED_CARDS_KEY = "count";
    @BindView(R.id.card_view)
    /* default */ CardView mCardView;
    @BindView(R.id.to_know_button)
    /* default */ FloatingActionButton mKnowButton;
    @BindView(R.id.to_repeat_button)
    /* default */ FloatingActionButton mRepeatButton;
    @BindView(R.id.turn_card_button)
    /* default */ ImageView mTurnCardButton;
    @BindView(R.id.textFrontCardView)
    /* default */ TextView mFrontTextView;
    @BindView(R.id.textBackCardView)
    /* default */ TextView mBackTextView;
    @BindView(R.id.delimeter)
    /* default */ View mDelimiter;
    @BindView(R.id.learned_in_session)
    /* default */ TextView mLearnedInSessionCards;

    @Inject
    /* default */ LearningCardsActivityPresenter mPresenter;
    private boolean mBackIsShown;
    private int mLearnedCardsCount;
    private String mAccess;
    private Trace mStartTrace;
    private Trace mNextCardTrace;

    /**
     * Method starts LearningCardsActivity.
     *
     * @param context    context to start Activity.
     * @param deckAccess deck information which cards to learn.
     */
    public static void startActivity(final Context context, final DeckAccess deckAccess) {
        Intent intent = new Intent(context, LearningCardsActivity.class);
        intent.putExtra(LearningCardsActivity.DECK_ACCESS, new ParcelableDeckAccess(deckAccess));
        context.startActivity(intent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStartTrace = FirebasePerformance.getInstance().newTrace("start_learning_cards");
        mStartTrace.start();

        setContentView(R.layout.show_cards_activity);
        mLearnedCardsCount = 0;
        if (savedInstanceState != null) {
            mBackIsShown = savedInstanceState.getBoolean(BACK_IS_SHOWN_KEY);
            mLearnedCardsCount = savedInstanceState.getInt(LEARNED_CARDS_KEY);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        DeckAccess deckAccess = ParcelableDeckAccess.get(intent.getParcelableExtra(DECK_ACCESS));
        if (deckAccess == null || deckAccess.getDeck() == null) {
            finish();
        } else {
            mAccess = deckAccess.getAccess();
            this.setTitle(deckAccess.getDeck().getName());
            Injector.getLearningCardsActivityInjector(this).inject(this);
            mPresenter.onCreate(deckAccess.getDeck());
            ButterKnife.bind(this);
            mDelimiter.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putBoolean(BACK_IS_SHOWN_KEY, mBackIsShown);
        outState.putInt(LEARNED_CARDS_KEY, mLearnedCardsCount);
        super.onSaveInstanceState(outState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
        mLearnedInSessionCards.setText(String.format(getString(R.string.card_watched_text),
                mLearnedCardsCount));
    }

    @Override
    protected void onStop() {
        mPresenter.onStop();
        if (mNextCardTrace != null) {
            mNextCardTrace.stop();
            mNextCardTrace = null;
        }
        if (mStartTrace != null) {
            mStartTrace.stop();
            mStartTrace = null;
        }
        super.onStop();
    }

    @OnClick(R.id.to_know_button)
    /* default */ void userKnowCardButtonClick() {
        setClickableRepeatKnowButtons(false);
        mNextCardTrace = FirebasePerformance.getInstance().newTrace("learning_next_card");
        mNextCardTrace.start();
        mPresenter.userKnowCard();
        mBackIsShown = false;
        increaseNumberOfShowedCards();
    }

    @OnClick(R.id.to_repeat_button)
    /* default */ void userDontKnowCardButtonClick() {
        setClickableRepeatKnowButtons(false);
        mPresenter.userDoNotKnowCard();
        mBackIsShown = false;
        increaseNumberOfShowedCards();
    }

    @OnClick(R.id.turn_card_button)
    /* default */ void flipCardButtonClick() {
        mPresenter.flipCard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_card_menu, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_card_show_menu:
                if (mAccess.equals(getString(R.string.read_access))) {
                    Toast.makeText(this,
                            getString(R.string.edit_cards_with_read_access_user_warning),
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                mPresenter.startEditCard();
                break;
            case R.id.delete_card_show_menu:
                if (mAccess.equals(getString(R.string.read_access))) {
                    Toast.makeText(this,
                            getString(R.string.delete_cards_with_read_access_user_warning),
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.delete_card_warning);
                builder.setPositiveButton(R.string.delete, (dialogDelete, which) -> {
                    mBackIsShown = false;
                    mPresenter.delete();
                });
                builder.setNegativeButton(R.string.cancel, (dialogCancel, which) ->
                        dialogCancel.cancel());
                builder.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation" /* fromHtml(String, int) not available before API 24 */)
    public void showFrontSide(final String front, final boolean isHtml,
                              final GrammaticalGenderSpecifier.Gender gender) {
        if (mStartTrace != null) {
            mStartTrace.stop();
            mStartTrace = null;
        }
        if (mNextCardTrace != null) {
            mNextCardTrace.stop();
            mNextCardTrace = null;
        }

        mCardView.setCardBackgroundColor(ContextCompat
                .getColor(this, CardColor.getColor(gender)));
        if (isHtml) {
            mFrontTextView.setText(Html.fromHtml(front));
        } else {
            mFrontTextView.setText(front);
        }
        mBackTextView.setText("");
        mRepeatButton.setVisibility(View.INVISIBLE);
        mKnowButton.setVisibility(View.INVISIBLE);
        mTurnCardButton.setVisibility(View.VISIBLE);
        mDelimiter.setVisibility(View.INVISIBLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation" /* fromHtml(String, int) not available before API 24 */)
    public void showBackSide(final String back, final boolean isHtml) {
        if (isHtml) {
            mBackTextView.setText(Html.fromHtml(back));
        } else {
            mBackTextView.setText(back);
        }
        Animator repeatButtonAnimation = null;
        Animator knowButtonAnimation = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            repeatButtonAnimation = Animation.appearanceAnimation(mRepeatButton);
            knowButtonAnimation = Animation.appearanceAnimation(mKnowButton);
        }
        setClickableRepeatKnowButtons(true);
        mRepeatButton.setVisibility(View.VISIBLE);
        mKnowButton.setVisibility(View.VISIBLE);
        if (repeatButtonAnimation != null && knowButtonAnimation != null) {
            repeatButtonAnimation.start();
            knowButtonAnimation.start();
        }
        mTurnCardButton.setVisibility(View.INVISIBLE);
        mDelimiter.setVisibility(View.VISIBLE);
        this.mBackIsShown = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startEditCardActivity(final Card card) {
        AddEditCardActivity.startEditCardActivity(this, card);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finishLearning() {
        finish();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean backSideIsShown() {
        return mBackIsShown;
    }

    /**
     * Do buttons not clickable after first click to prevent double click and missing
     * next card.
     *
     * @param isClickable whether buttons clickable or not
     */
    private void setClickableRepeatKnowButtons(final Boolean isClickable) {
        mKnowButton.setClickable(isClickable);
        mRepeatButton.setClickable(isClickable);
    }

    private void increaseNumberOfShowedCards() {
        mLearnedCardsCount++;
        mLearnedInSessionCards.setText(String.format(getString(R.string.card_watched_text),
                mLearnedCardsCount));
    }
}
