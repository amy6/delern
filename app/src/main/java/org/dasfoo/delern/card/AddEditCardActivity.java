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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ServerValue;

import org.dasfoo.delern.R;
import org.dasfoo.delern.listeners.AbstractOnDataChangeListener;
import org.dasfoo.delern.listeners.TextWatcherStub;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Level;
import org.dasfoo.delern.models.ScheduledCard;
import org.dasfoo.delern.util.LogUtil;

/**
 * Activity to edit or add a new card.
 */
public class AddEditCardActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * IntentExtra R.string for the title of this activity.
     */
    public static final String LABEL = "label";

    /**
     * IntentExtra Deck ID which this card belongs to.
     */
    public static final String DECK_ID = "mDeckId";

    /**
     * IntentExtra Card ID being edited.
     */
    public static final String CARD = "card";

    private static final String TAG = LogUtil.tagFor(AddEditCardActivity.class);
    private String mDeckId;
    private TextInputEditText mFrontSideInputText;
    private TextInputEditText mBackSideInputText;
    private Card mCard;
    private CheckBox mAddReversedCardCheckbox;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_card_activity);
        configureToolbar();
        Intent intent = getIntent();
        final int label = intent.getIntExtra(LABEL, 0);
        mDeckId = intent.getStringExtra(DECK_ID);
        mCard = intent.getParcelableExtra(CARD);
        this.setTitle(label);

        mFrontSideInputText = (TextInputEditText) findViewById(R.id.front_side_text);
        mBackSideInputText = (TextInputEditText) findViewById(R.id.back_side_text);
        mAddReversedCardCheckbox = (CheckBox) findViewById(R.id.add_reversed_card_checkbox);
        final Button mAddCardToDbButton = (Button) findViewById(R.id.add_card_to_db);
        mAddCardToDbButton.setOnClickListener(this);
        if (mCard == null) {
            mAddReversedCardCheckbox.setVisibility(View.VISIBLE);
        } else {
            mAddCardToDbButton.setText(R.string.save);
            mFrontSideInputText.setText(mCard.getFront());
            mBackSideInputText.setText(mCard.getBack());
            mAddReversedCardCheckbox.setVisibility(View.INVISIBLE);
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

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.add_card_to_db) {
            if (mCard == null) {
                String frontCardSide = mFrontSideInputText.getText().toString();
                String backCardSide = mBackSideInputText.getText().toString();
                addNewCard(frontCardSide, backCardSide);
                if (mAddReversedCardCheckbox.isChecked()) {
                    addNewCard(backCardSide, frontCardSide);
                }
            } else {
                mCard.setFront(mFrontSideInputText.getText().toString());
                mCard.setBack(mBackSideInputText.getText().toString());
                Card.updateCard(mCard, mDeckId,
                        new AbstractOnDataChangeListener(TAG, this) {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                Toast.makeText(AddEditCardActivity.this,
                                        R.string.updated_card_user_message,
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
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
        Card newCard = new Card();
        newCard.setFront(frontSide);
        newCard.setBack(backSide);
        newCard.setCreatedAt(ServerValue.TIMESTAMP);
        ScheduledCard scheduledCard = new ScheduledCard(Level.L0.name(),
                System.currentTimeMillis());
        Card.createNewCard(newCard, mDeckId, scheduledCard,
                new AbstractOnDataChangeListener(TAG, this) {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
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
                    }
                });
    }
}
