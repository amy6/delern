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

package org.dasfoo.delern.sharedeck;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import org.dasfoo.delern.R;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.ParcelableDeck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Handles sharing a deck with users.
 */
public class ShareDeckActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShareDeckActivity.class);

    /**
     * IntentExtra deck for this activity.
     */
    private static final String DECK = "deck";
    private static final int RESULT_PICK_CONTACT = 855;
    @BindView(R.id.person_data)
    /* default */ AutoCompleteTextView mPersonData;
    @BindView(R.id.sharing_permissions_spinner)
    /* default */ Spinner mSharingPermissionsSpinner;
    private Deck mDeck;

    /**
     * Method starts ShareDeckActivity.
     *
     * @param context mContext of Activity that called this method.
     * @param deck    deck to perform sharing.
     */
    public static void startActivity(final Context context, final Deck deck) {
        Intent intent = new Intent(context, ShareDeckActivity.class);
        intent.putExtra(ShareDeckActivity.DECK, new ParcelableDeck(deck));
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_deck_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        mDeck = ParcelableDeck.get(intent.getParcelableExtra(DECK));
        this.setTitle(mDeck.getName());
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSharingPermissionsSpinner.setAdapter(new ShareSpinnerAdapter(this));
        setAutoCompleteAdapter();
    }

    private void setAutoCompleteAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.choose_contact_autocomplete));
        mPersonData.setAdapter(adapter);
        mPersonData.setThreshold(0);
        mPersonData.setOnClickListener(v -> mPersonData.showDropDown());
        mPersonData.setOnItemClickListener((parent, view, position, id) -> {
            mPersonData.setText("");
            chooseEmailFromContactsIntent();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.share_deck_menu, menu);

        // Menu icons from drawable folder isn't tinted on default.
        // http://stackoverflow.com/questions/24301235/tint-menu-icons
        MenuItem sendMenuItem = menu.findItem(R.id.share_deck_menu);
        Drawable tintedIcon = sendMenuItem.getIcon();
        // TODO(ksheremet): Check mode http://ssp.impulsetrain.com/porterduff.html
        tintedIcon.mutate().setColorFilter(ContextCompat.getColor(this, R.color.toolbarIconColor),
                PorterDuff.Mode.SRC_IN);
        sendMenuItem.setIcon(tintedIcon);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_deck_menu:
                Toast.makeText(this, "Share deck:" + mDeck.getName(), Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * Intent that opens Contact app for choosing user.
     */
    private void chooseEmailFromContactsIntent() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Email.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
                default:
                    LOGGER.debug("on ActivityResult not implemented");
                    break;
            }
        } else {
            LOGGER.error("Failed to pick a contact");
        }
    }

    /**
     * Query the Uri and read contact details. Handle the picked contact data.
     *
     * @param data intent to get an email that was chosen from contacts.
     */
    private void contactPicked(final Intent data) {
        Cursor cursor = null;
        // getData() method will have the Content Uri of the selected contact
        Uri uri = data.getData();
        //Query the content uri
        cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        // column index of the email
        int emailIndex = cursor
                .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
        // Set the value to the textview
        mPersonData.setText(cursor.getString(emailIndex));
        cursor.close();
    }
}
