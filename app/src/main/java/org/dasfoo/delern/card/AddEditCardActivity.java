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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import org.dasfoo.delern.R;
import org.dasfoo.delern.listeners.TextWatcherStub;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.Level;
import org.dasfoo.delern.models.ScheduledCard;
import org.dasfoo.delern.models.helpers.MultiWrite;
import org.dasfoo.delern.models.listeners.OnOperationCompleteListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity to edit or add a new card.
 */
public class AddEditCardActivity extends AppCompatActivity {

    /**
     * IntentExtra Card ID being edited.
     */
    public static final String CARD = "card";

    @BindView(R.id.front_side_text)
    /* default */ TextInputEditText mFrontSideInputText;
    @BindView(R.id.back_side_text)
    /* default */ TextInputEditText mBackSideInputText;
    @BindView(R.id.add_reversed_card_checkbox)
    /* default */ CheckBox mAddReversedCardCheckbox;
    private Card mCard;
    private OnOperationCompleteListener mOnCardAddedListener;
    private OnOperationCompleteListener mOnCardUpdatedListener;

    /**
     * Method starts activity for adding cards in deck.
     *
     * @param context context from activity where method was called.
     * @param deck    deck where to add cards.
     */
    public static void startAddCardActivity(final Context context, final Deck deck) {
        Intent intent = new Intent(context, AddEditCardActivity.class);
        Card card = new Card(deck);
        intent.putExtra(AddEditCardActivity.CARD, card);
        context.startActivity(intent);
    }

    /**
     * Method starts AddEditCardActivity for editing card.
     *
     * @param context context from activity where method was called.
     * @param card    card to edit.
     */
    public static void startEditCardActivity(final Context context, final Card card) {
        Intent intent = new Intent(context, AddEditCardActivity.class);
        intent.putExtra(AddEditCardActivity.CARD, card);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_card_activity);
        configureToolbar();
        Intent intent = getIntent();
        mCard = intent.getParcelableExtra(CARD);
        this.setTitle(mCard.getDeck().getName());
        ButterKnife.bind(this);

        final Button mAddCardToDbButton = ButterKnife.findById(this, R.id.add_card_to_db);
        if (mCard.exists()) {
            mAddCardToDbButton.setText(R.string.save);
            mFrontSideInputText.setText(mCard.getFront());
            mBackSideInputText.setText(mCard.getBack());
            mAddReversedCardCheckbox.setVisibility(View.INVISIBLE);
        } else {
            mAddReversedCardCheckbox.setVisibility(View.VISIBLE);
        }
        mAddCardToDbButton.setEnabled(false);
        final TextWatcherStub cardValid = new TextWatcherStub() {
            @Override
            public void afterTextChanged(final Editable s) {
                boolean inputValid = true;
                if (TextUtils.isEmpty(mFrontSideInputText.getText().toString().trim())) {
                    inputValid = false;
                }
                if (mAddReversedCardCheckbox.isChecked() &&
                        TextUtils.isEmpty(mBackSideInputText.getText().toString().trim())) {
                    inputValid = false;
                }
                mAddCardToDbButton.setEnabled(inputValid);
            }
        };

        mFrontSideInputText.addTextChangedListener(cardValid);
        mBackSideInputText.addTextChangedListener(cardValid);
        mAddReversedCardCheckbox
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(final CompoundButton buttonView,
                                                 final boolean isChecked) {
                        cardValid.afterTextChanged(null);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCard.exists()) {
            mOnCardUpdatedListener = new OnOperationCompleteListener(this) {
                @Override
                public void onSuccess() {
                    Toast.makeText(AddEditCardActivity.this,
                            R.string.updated_card_user_message,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            };
        } else {
            mOnCardAddedListener = new OnOperationCompleteListener(this) {
                @Override
                public void onSuccess() {
                    if (mAddReversedCardCheckbox.isChecked()) {
                        // TODO(ksheremet): Fix showing this message double times (2 card)
                        Toast.makeText(AddEditCardActivity.this,
                                R.string.add_extra_reversed_card_message,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddEditCardActivity.this,
                                R.string.added_card_user_message,
                                Toast.LENGTH_SHORT).show();
                    }
                    cleanTextFields();
                    // Clean fields for the next new card
                    mCard.setFront(null);
                    mCard.setBack(null);
                }
            };
        }
    }

    /**
     * Called when user clicked on Save or Update button.
     */
    @OnClick(R.id.add_card_to_db)
    public void onAddUpdateButtonClick() {
        if (mCard.exists()) {
            mCard.setFront(mFrontSideInputText.getText().toString());
            mCard.setBack(mBackSideInputText.getText().toString());
            mCard.save(mOnCardUpdatedListener);
        } else {
            String frontCardSide = mFrontSideInputText.getText().toString();
            String backCardSide = mBackSideInputText.getText().toString();
            addNewCard(frontCardSide, backCardSide);
            if (mAddReversedCardCheckbox.isChecked()) {
                addNewCard(backCardSide, frontCardSide);
            }
        }
    }

    private void cleanTextFields() {
        mFrontSideInputText.setText("");
        mFrontSideInputText.requestFocus();
        mBackSideInputText.setText("");
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void addNewCard(final String frontSide, final String backSide) {
        // TODO(refactoring): move to Card, make ScheduledCard private
        ScheduledCard scheduledCard = new ScheduledCard(mCard.getDeck());
        scheduledCard.setLevel(Level.L0.name());
        scheduledCard.setRepeatAt(System.currentTimeMillis());

        Card newCard = new Card(scheduledCard);
        newCard.setFront(frontSide);
        newCard.setBack(backSide);

        new MultiWrite()
                .save(newCard)
                .save(scheduledCard)
                .write(mOnCardAddedListener);
    }
}
