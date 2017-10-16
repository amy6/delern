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
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.dasfoo.delern.R;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.ParcelableDeck;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Handles sharing a deck with users.
 */
public class ShareDeckActivity extends AppCompatActivity {
    /**
     * IntentExtra deck for this activity.
     */
    private static final String DECK = "deck";
    @BindView(R.id.person_data)
    /* default */ TextView mPersonData;
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
}
