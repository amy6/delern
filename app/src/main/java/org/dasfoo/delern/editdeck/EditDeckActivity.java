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

package org.dasfoo.delern.editdeck;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import org.dasfoo.delern.R;
import org.dasfoo.delern.addupdatecard.TextWatcherStub;
import org.dasfoo.delern.di.Injector;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckAccess;
import org.dasfoo.delern.models.ParcelableDeckAccess;
import org.dasfoo.delern.util.PerfEventTracker;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Performs operations with a deck, such as rename, delete, update.
 */
public class EditDeckActivity extends AppCompatActivity implements IEditDeckView {

    /**
     * IntentExtra deck for this activity.
     */
    public static final String DECK_ACCESS = "deckAccess";

    @BindView(R.id.deck_name)
    /* default */ TextInputEditText mDeckNameEditText;
    @BindView(R.id.deck_type_spinner)
    /* default */ Spinner mDeckTypeSpinner;
    @BindView(R.id.on_off_switch)
    /* default */ Switch mOnOffSwitch;
    @Inject
    /* default */ EditDeckActivityPresenter mPresenter;
    private final AdapterView.OnItemSelectedListener mSpinnerItemClickListener =
            new AdapterView.OnItemSelectedListener() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void onItemSelected(final AdapterView<?> adapterView, final View view,
                                           final int position, final long id) {
                    mPresenter.selectDeckType(position);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void onNothingSelected(final AdapterView<?> adapterView) {
                    mPresenter.selectDeckType(-1);
                }
            };
    private final CompoundButton.OnCheckedChangeListener mCheckedChangeListener =
            (compoundButton, isChecked) -> {
                mPresenter.setMarkdown(isChecked);
            };
    private Deck mDeck;
    private boolean mInputValid;

    /**
     * Method starts EditDeckActivity.
     *
     * @param context    context of Activity that called this method.
     * @param deckAccess deck to perform operations.
     */
    public static void startActivity(final Context context, final DeckAccess deckAccess) {
        Intent intent = new Intent(context, EditDeckActivity.class);
        intent.putExtra(EditDeckActivity.DECK_ACCESS, new ParcelableDeckAccess(deckAccess));
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_deck_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        DeckAccess deckAccess = ParcelableDeckAccess.get(intent.getParcelableExtra(DECK_ACCESS));
        mDeck = deckAccess.getDeck();
        this.setTitle(mDeck.getName());
        Injector.getEditDeckActivityInjector(this, deckAccess).inject(this);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ArrayAdapter<CharSequence> arrayAdapter =
                ArrayAdapter.createFromResource(this,
                        R.array.deck_type_spinner, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDeckTypeSpinner.setAdapter(arrayAdapter);

        int arrayLength = getResources().getStringArray(R.array.deck_type_spinner).length;
        mDeckTypeSpinner.setSelection(mPresenter.setDefaultDeckType(arrayLength));

        mDeckTypeSpinner.setOnItemSelectedListener(mSpinnerItemClickListener);

        mDeckNameEditText.setText(mDeck.getName());
        final TextWatcherStub deckNameChanged = new TextWatcherStub() {
            @Override
            public void afterTextChanged(final Editable s) {
                mInputValid = !TextUtils.isEmpty(mDeckNameEditText.getText().toString().trim());
            }
        };
        mDeckNameEditText.addTextChangedListener(deckNameChanged);
        mOnOffSwitch.setChecked(mDeck.isMarkdown());
        mOnOffSwitch.setOnCheckedChangeListener(mCheckedChangeListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        saveDeck();
        super.onBackPressed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.deck_settings_menu, menu);
        // Menu icons from drawable folder isn't tinted on default.
        // http://stackoverflow.com/questions/24301235/tint-menu-icons
        MenuItem deleteMenuItem = menu.findItem(R.id.delete_deck_menu);
        Drawable tintedIcon = deleteMenuItem.getIcon();
        // More about PorterDuff.Mode http://ssp.impulsetrain.com/porterduff.html
        tintedIcon.mutate().setColorFilter(ContextCompat.getColor(this, R.color.toolbarIconColor),
                PorterDuff.Mode.SRC_IN);
        deleteMenuItem.setIcon(tintedIcon);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_deck_menu:
                showDeleteDialog();
                break;
            case android.R.id.home:
                saveDeck();
                NavUtils.navigateUpFromSameTask(this);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.delete_deck)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    mPresenter.deleteDeck();
                    finish();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.cancel();
                })
                .show();
    }

    private void saveDeck() {
        PerfEventTracker.trackEvent(PerfEventTracker.Event.DECK_SETTINGS_SAVE, this, null);
        if (mInputValid) {
            String newDeckName = mDeckNameEditText.getText().toString().trim();
            mDeck.setName(newDeckName);
        }
        mPresenter.updateDeck(mDeck);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showDeckTypeNotExistUserMessage() {
        Toast.makeText(this, R.string.decktype_not_exist_user_message,
                Toast.LENGTH_SHORT).show();
    }
}
