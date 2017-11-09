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

package org.dasfoo.delern.previewcard;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.dasfoo.delern.R;
import org.dasfoo.delern.addupdatecard.AddEditCardActivity;
import org.dasfoo.delern.di.Injector;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.ParcelableCard;
import org.dasfoo.delern.util.CardColor;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity that shows the card before it is being edited.
 */
public class PreEditCardActivity extends AppCompatActivity implements IPreEditCardView {

    /**
     * IntentExtra card that is being edited.
     */
    public static final String CARD = "card";

    @BindView(R.id.textFrontCardView)
    /* default */ TextView mFrontPreview;
    @BindView(R.id.textBackCardView)
    /* default */ TextView mBackPreview;
    @BindView(R.id.card_view)
    /* default */ CardView mCardView;
    @Inject
    /* default */ PreEditCardActivityPresenter mPresenter;

    /**
     * Method starts PreEditCardActivity. It gets context from where it was called
     * and card for preview.
     *
     * @param context context for starting activity.
     * @param card    card for preview.
     */
    public static void startActivity(final Context context, final Card card) {
        Intent intent = new Intent(context, PreEditCardActivity.class);
        intent.putExtra(PreEditCardActivity.CARD, new ParcelableCard(card));
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_edit_card_activity);

        configureToolbar();
        Intent intent = getIntent();
        Card card = ParcelableCard.get(intent.getParcelableExtra(CARD));
        this.setTitle(card.getDeck().getName());
        ButterKnife.bind(this);
        Injector.getPreEditCardActivityInjector(this).inject(this);
        mPresenter.onCreate(card);
    }

    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    protected void onStop() {
        mPresenter.onStop();
        super.onStop();
    }

    @OnClick(R.id.edit_card_button)
    /* default */ void editCardActivityStart() {
        mPresenter.editCard();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_card_menu, menu);

        // Menu icons from drawable folder isn't tinted on default.
        // http://stackoverflow.com/questions/24301235/tint-menu-icons
        MenuItem deleteMenuItem = menu.findItem(R.id.delete_card_menu);
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
            case R.id.delete_card_menu:
                deleteCard();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void deleteCard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_card_warning);
        builder.setPositiveButton(R.string.delete, (dialogDelete, which) -> {
            mPresenter.deleteCard();
            finish();
        });
        builder.setNegativeButton(R.string.cancel, (dialogCancel, which) -> dialogCancel.cancel());
        builder.show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation" /* fromHtml(String, int) not available before API 24 */)
    public void showCard(final String front, final String back, final boolean isHtml) {
        if (isHtml) {
            mFrontPreview.setText(Html.fromHtml(front));
            mBackPreview.setText(Html.fromHtml(back));
        } else {
            mFrontPreview.setText(front);
            mBackPreview.setText(back);
        }
        mCardView.setCardBackgroundColor(ContextCompat
                .getColor(this, CardColor.getColor(mPresenter.specifyContentGender())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startEditCardActivity(final Card card) {
        AddEditCardActivity.startEditCardActivity(this, card);
    }
}
