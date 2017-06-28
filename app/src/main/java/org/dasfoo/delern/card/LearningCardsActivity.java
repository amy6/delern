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

package org.dasfoo.delern.card;

import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.dasfoo.delern.R;
import org.dasfoo.delern.controller.CardColor;
import org.dasfoo.delern.controller.GrammaticalGenderSpecifier;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckType;
import org.dasfoo.delern.models.listeners.AbstractDataAvailableListener;
import org.dasfoo.delern.util.Animation;
import org.dasfoo.delern.util.LogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity for showing cards to learn.
 */
public class LearningCardsActivity extends AppCompatActivity {

    /**
     * IntentExtra deck for this activity.
     */
    public static final String DECK = "deck";

    /**
     * Information about class for logging.
     */
    private static final String TAG = LogUtil.tagFor(LearningCardsActivity.class);

    /**
     * Key for saving onSaveInstanceState.
     */
    private static final String BACK_IS_SHOWN = "back";
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
    private boolean mBackIsShown;
    private Deck mDeck;
    private Card mCard;
    private final AbstractDataAvailableListener<Card> mCardAvailableListener =
            new AbstractDataAvailableListener<Card>(this) {
                @Override
                public void onData(final Card data) {
                    if (data == null) {
                        finish();
                        return;
                    }
                    mCard = data;
                    showFrontSide();
                    // if user decided to edit card, a back side can be shown or not.
                    // After returning back it must show the same state (the same buttons
                    // and text) as before editing
                    if (mBackIsShown) {
                        showBackSide();
                    }
                }
            };

    /**
     * Method starts LearningCardsActivity.
     *
     * @param context context to start Activity.
     * @param deck    deck which cards to learn.
     */
    public static void startActivity(final Context context, final Deck deck) {
        Intent intent = new Intent(context, LearningCardsActivity.class);
        intent.putExtra(LearningCardsActivity.DECK, deck);
        context.startActivity(intent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_cards_activity);
        if (savedInstanceState != null) {
            mBackIsShown = savedInstanceState.getBoolean(BACK_IS_SHOWN);
        }
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getParameters();
        ButterKnife.bind(this);
        mDelimiter.setVisibility(View.INVISIBLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BACK_IS_SHOWN, mBackIsShown);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart() {
        super.onStart();
        mDeck.startScheduledCardWatcher(mCardAvailableListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCardAvailableListener.cleanup();
    }

    @OnClick(R.id.to_know_button)
    /* default */ void userKnowCardButtonClick() {
        setClickableRepeatKnowButtons(false);
        mCard.answer(true);
        mBackIsShown = false;
    }

    @OnClick(R.id.to_repeat_button)
    /* default */ void userDontKnowCardButtonClick() {
        setClickableRepeatKnowButtons(false);
        mCard.answer(false);
        mBackIsShown = false;
    }

    @OnClick(R.id.turn_card_button)
    /* default */ void flipCardButtonClick() {
        showBackSide();
    }

    /**
     * Gets parameters sent from previous Activity.
     */
    private void getParameters() {
        Intent intent = getIntent();
        mDeck = intent.getParcelableExtra(DECK);
        this.setTitle(mDeck.getName());
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
                Intent intentEdit = new Intent(this, AddEditCardActivity.class);
                intentEdit.putExtra(AddEditCardActivity.CARD, mCard);
                startActivity(intentEdit);
                break;
            case R.id.delete_card_show_menu:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.delete_card_warning);
                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        mBackIsShown = false;
                        // TODO(ksheremet): delete if not owner
                        mCard.delete();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * Shows front side of the current card and appropriate buttons.
     */
    private void showFrontSide() {
        setBackgroundCardColor();
        mFrontTextView.setText(mCard.getFront());
        mBackTextView.setText("");
        mRepeatButton.setVisibility(View.INVISIBLE);
        mKnowButton.setVisibility(View.INVISIBLE);
        mTurnCardButton.setVisibility(View.VISIBLE);
        mDelimiter.setVisibility(View.INVISIBLE);
    }

    /**
     * Specifies grammatical gender of content.
     * Sets background color for mCardView regarding gender.
     */
    private void setBackgroundCardColor() {
        GrammaticalGenderSpecifier.Gender gender;
        try {
            gender = GrammaticalGenderSpecifier.specifyGender(
                    DeckType.valueOf(mCard.getDeck().getDeckType()),
                    mCard.getBack());

        } catch (IllegalArgumentException e) {
            LogUtil.error(TAG, "Cannot detect gender: " + mCard.getBack(), e);
            gender = GrammaticalGenderSpecifier.Gender.NO_GENDER;
        }
        mCardView.setCardBackgroundColor(ContextCompat.getColor(this, CardColor.getColor(gender)));
    }

    /**
     * Shows back side of current card and appropriate buttons.
     */
    private void showBackSide() {
        mBackTextView.setText(mCard.getBack());
        Animator repeatButtonAnimation = null;
        Animator knowButtonAnimation = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            repeatButtonAnimation = Animation.appearanceAnimation(mRepeatButton);
            knowButtonAnimation = Animation.appearanceAnimation(mKnowButton);
        }
        setClickableRepeatKnowButtons(true);
        mRepeatButton.setVisibility(View.VISIBLE);
        mKnowButton.setVisibility(View.VISIBLE);
        if (repeatButtonAnimation != null) {
            repeatButtonAnimation.start();
            knowButtonAnimation.start();
        }
        mTurnCardButton.setVisibility(View.INVISIBLE);
        mDelimiter.setVisibility(View.VISIBLE);
        mBackIsShown = true;
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
}
