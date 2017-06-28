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
import org.dasfoo.delern.models.listeners.OnOperationCompleteListener;
import org.dasfoo.delern.presenters.AddEditCardActivityPresenter;
import org.dasfoo.delern.views.IAddEditCardView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity to edit or add a new card.
 */
public class AddEditCardActivity extends AppCompatActivity implements IAddEditCardView {

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
    @BindView(R.id.add_card_to_db)
    /* default */ Button mAddCardToDbButton;
    private OnOperationCompleteListener mOnCardAddedListener;
    private OnOperationCompleteListener mOnCardUpdatedListener;

    private final AddEditCardActivityPresenter mPresenter = new AddEditCardActivityPresenter(this);

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
        Card card = intent.getParcelableExtra(CARD);
        this.setTitle(card.getDeck().getName());
        ButterKnife.bind(this);

        mPresenter.onCreate(card);
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
        if (mPresenter.cardExist()) {
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
                    mPresenter.cleanCardFields();
                }
            };
        }
    }

    /**
     * Called when user clicked on Save or Update button.
     */
    @OnClick(R.id.add_card_to_db)
    public void onAddUpdateButtonClick() {
        if (mPresenter.cardExist()) {
            mPresenter.update(mFrontSideInputText.getText().toString(),
                    mBackSideInputText.getText().toString(), mOnCardUpdatedListener);
        } else {
            String frontCardSide = mFrontSideInputText.getText().toString();
            String backCardSide = mBackSideInputText.getText().toString();
            mPresenter.add(frontCardSide, backCardSide, mOnCardAddedListener);
            if (mAddReversedCardCheckbox.isChecked()) {
                mPresenter.add(backCardSide, frontCardSide, mOnCardAddedListener);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void initForAdd() {
        mAddReversedCardCheckbox.setVisibility(View.VISIBLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initForUpdate(final String front, final String back) {
        mAddCardToDbButton.setText(R.string.save);
        mFrontSideInputText.setText(front);
        mBackSideInputText.setText(back);
        mAddReversedCardCheckbox.setVisibility(View.INVISIBLE);
    }
}
