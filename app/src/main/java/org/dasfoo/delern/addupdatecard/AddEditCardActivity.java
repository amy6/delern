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

package org.dasfoo.delern.addupdatecard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import org.dasfoo.delern.AbstractActivity;
import org.dasfoo.delern.R;
import org.dasfoo.delern.di.Injector;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.ParcelableCard;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity to edit or add a new card.
 */
public class AddEditCardActivity extends AbstractActivity implements IAddEditCardView {

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

    @Inject
    /* default */ IAddUpdatePresenter mPresenter;
    private boolean mInputValid;

    /**
     * Method starts activity for adding cards in deck.
     *
     * @param context context from activity where method was called.
     * @param deck    deck where to add cards.
     */
    public static void startAddCardActivity(final Context context, final Deck deck) {
        Intent intent = new Intent(context, AddEditCardActivity.class);
        Card card = new Card(deck);
        intent.putExtra(AddEditCardActivity.CARD, new ParcelableCard(card));
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
        intent.putExtra(AddEditCardActivity.CARD, new ParcelableCard(card));
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_card_activity);
        configureToolbar();
        Intent intent = getIntent();
        Card card = ParcelableCard.get(intent.getParcelableExtra(CARD));
        this.setTitle(card.getDeck().getName());
        ButterKnife.bind(this);
        if (card.exists()) {
            Injector.getUpdateActivityInjector(this, card).inject(this);
            initForUpdate(card.getFront(), card.getBack());
        } else {
            Injector.getAddActivityInjector(this, card.getDeck()).inject(this);
            initForAdd();
        }

        mAddCardToDbButton.setEnabled(false);
        final TextWatcherStub cardValid = new TextWatcherStub() {
            @Override
            public void afterTextChanged(final Editable s) {
                mInputValid = true;
                if (TextUtils.isEmpty(mFrontSideInputText.getText().toString().trim())) {
                    mInputValid = false;
                }
                if (mAddReversedCardCheckbox.isChecked() &&
                        TextUtils.isEmpty(mBackSideInputText.getText().toString().trim())) {
                    mInputValid = false;
                }
                mAddCardToDbButton.setEnabled(mInputValid);
            }
        };

        mFrontSideInputText.addTextChangedListener(cardValid);
        mBackSideInputText.addTextChangedListener(cardValid);
        mAddReversedCardCheckbox
                .setOnCheckedChangeListener((buttonView, isChecked) ->
                        cardValid.afterTextChanged(null));
    }

    /**
     * Called when user clicked on Save or Update button.
     */
    @OnClick(R.id.add_card_to_db)
    public void onAddUpdateButtonClick() {
        mPresenter.onAddUpdate(mFrontSideInputText.getText().toString(),
                mBackSideInputText.getText().toString());
    }

    private void cleanTextFields() {
        mFrontSideInputText.setText("");
        mFrontSideInputText.requestFocus();
        mBackSideInputText.setText("");
    }

    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Initialize view for adding cards.
     */
    public void initForAdd() {
        mAddReversedCardCheckbox.setVisibility(View.VISIBLE);
    }

    /**
     * Initialize view for updating card.
     *
     * @param front front side text for update.
     * @param back  back side text for update.
     */
    public void initForUpdate(final String front, final String back) {
        mFrontSideInputText.setText(front);
        mBackSideInputText.setText(back);
        mAddReversedCardCheckbox.setVisibility(View.INVISIBLE);
        mAddCardToDbButton.setVisibility(View.INVISIBLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cardUpdated() {
        Toast.makeText(this, R.string.updated_card_user_message, Toast.LENGTH_SHORT).show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cardAdded() {
        if (mAddReversedCardCheckbox.isChecked()) {
            // TODO(ksheremet): Fix showing this message double times (2 card)
            Toast.makeText(this, R.string.add_extra_reversed_card_message,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.added_card_user_message, Toast.LENGTH_SHORT).show();
        }
        cleanTextFields();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addReversedCard() {
        return mAddReversedCardCheckbox.isChecked();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        updateCard();
        super.onBackPressed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                updateCard();
                NavUtils.navigateUpFromSameTask(this);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void updateCard() {
        if (mInputValid) {
            mPresenter.onAddUpdate(mFrontSideInputText.getText().toString(),
                    mBackSideInputText.getText().toString());
        }
    }
}
