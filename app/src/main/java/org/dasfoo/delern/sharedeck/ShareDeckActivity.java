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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.dasfoo.delern.R;
import org.dasfoo.delern.addupdatecard.TextWatcherStub;
import org.dasfoo.delern.di.Injector;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.ParcelableDeck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Handles sharing a deck with users.
 */
public class ShareDeckActivity extends AppCompatActivity {

    private static final int REQUEST_INVITE = 1;
    private static final int USER_NOT_EXIST = 404;
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
    @BindView(R.id.recycler_view)
    /* default */ RecyclerView mRecyclerView;
    @Inject
    /* default */ ShareDeckActivityPresenter mPresenter;
    private boolean mValidInput;

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
        Deck deck = ParcelableDeck.get(intent.getParcelableExtra(DECK));
        this.setTitle(deck.getName());
        ButterKnife.bind(this);
        Injector.getShareDeckActivityInjector(deck).inject(this);

        mSharingPermissionsSpinner.setAdapter(new ShareSpinnerAdapter(this,
                R.array.share_permissions_spinner_text, R.array.share_permissions_spinner_img));
        setAutoCompleteViewSettings();

        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .build());
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(
                new UserDeckAccessRecyclerViewAdapter(R.layout.user_deck_access_layout,
                        mPresenter));
    }

    private void setAutoCompleteViewSettings() {
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
        mPersonData.addTextChangedListener(new TextWatcherStub() {
            /**
             * Checks whether an email address valid or not in view.
             *
             * @param editableView view for typing email address.
             */
            @Override
            public void afterTextChanged(final Editable editableView) {
                // Check valid email address.
                mValidInput = Patterns.EMAIL_ADDRESS
                        .matcher(editableView.toString().trim()).matches();
            }
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
                if (mValidInput) {
                    httpReq(mPersonData.getText().toString().trim());
                } else {
                    Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
                }
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
        switch (requestCode) {
            case REQUEST_INVITE:
                if (resultCode == RESULT_OK) {
                    return;
                } else {
                    LOGGER.error("onActivityResult Invite: requestCode={}, resultCode={}",
                            requestCode, resultCode);
                }
                break;
            case RESULT_PICK_CONTACT:
                if (resultCode == RESULT_OK) {
                    contactPicked(data);
                    return;
                } else {
                    LOGGER.error("onActivityResult Chose Contact: requestCode={}, resultCode={}",
                            requestCode, resultCode);
                }
                break;
            default:
                LOGGER.error("Requesе Code not implemented:", requestCode);
        }
    }

    /**
     * Query the Uri and read contact details. Handle the picked contact data.
     *
     * @param data intent to get an email that was chosen from contacts.
     */
    private void contactPicked(final Intent data) {
        Cursor cursor;
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

    private void httpReq(final String email) {
        // Volley
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = new StringBuilder("http://us-central1-")
                .append(getResources().getString(R.string.project_id))
                .append(".cloudfunctions.net/userLookup?q=")
                .append(email).toString();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    shareDeck(response);
                },
                error -> {
                    if (USER_NOT_EXIST == error.networkResponse.statusCode) {
                        inviteFriendDialog();
                    } else {
                        Toast.makeText(this,
                                "Deck wasn't share. Please try later",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void shareDeck(final String uid) {
        String selectedAccess = mSharingPermissionsSpinner.getSelectedItem().toString();
        if (selectedAccess.equals(getString(R.string.can_edit_text))) {
            mPresenter.shareDeck(uid, "write");

        }
        if (selectedAccess.equals(getString(R.string.can_view_text))) {
            mPresenter.shareDeck(uid, "read");
        }
    }

    @SuppressWarnings("deprecation")
    private void inviteFriendDialog() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.invite_user_sharing_deck_message)
                .setPositiveButton(R.string.invite, (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_EMAIL,
                            new String[]{mPersonData.getText().toString()});
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.invitation_title));
                    intent.putExtra(Intent.EXTRA_TEXT,
                            Html.fromHtml(getString(R.string.simple_email_sharing)));
                    startActivityForResult(intent, REQUEST_INVITE);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.cancel();
                })
                .show();
    }
}
