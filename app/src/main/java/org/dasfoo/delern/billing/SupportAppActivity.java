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

package org.dasfoo.delern.billing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import org.dasfoo.delern.AbstractActivity;
import org.dasfoo.delern.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity that shows a user options how they can support development.
 */
public class SupportAppActivity extends AbstractActivity implements BillingProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(SupportAppActivity.class);

    // Tag for a dialog that allows us to find it when screen was rotated
    private static final String DIALOG_TAG = "dialog";
    @BindView(R.id.donate_button)
    /* default */ Button mDonateButton;
    private BillingManager mBillingManager;
    private SupportDevFragment mSupportDevFragment;

    /**
     * Method starts SupportAppActivity.
     *
     * @param context mContext of Activity that called this method.
     */
    public static void startActivity(final Context context) {
        Intent intent = new Intent(context, SupportAppActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.support_dev_activity);
        // Create and initialize BillingManager which talks to BillingLibrary
        mBillingManager = new BillingManager(this);
        // If item is paid, method consumes it.
        mBillingManager.consumePurchases();
        ButterKnife.bind(this);
        configureToolbar();
        this.setTitle(R.string.nav_support_development);
        // Try to restore dialog fragment if we were showing it prior to screen rotation
        if (savedInstanceState != null) {
            mSupportDevFragment = (SupportDevFragment) getSupportFragmentManager()
                    .findFragmentByTag(DIALOG_TAG);
        }
        showRefreshedUi();
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
     * User clicked the "Pay" button - show a purchase dialog with all available SKUs.
     */
    @OnClick(R.id.donate_button)
    public void onPurchaseButtonClicked() {
        LOGGER.debug("Support button clicked.");

        if (mSupportDevFragment == null) {
            mSupportDevFragment = new SupportDevFragment();
        }

        if (!isSupDevFragmentShown()) {
            mSupportDevFragment.show(getSupportFragmentManager(), DIALOG_TAG);
        }
    }

    /**
     * Check whether fragment is shown of not.
     *
     * @return whether fragment is shown of not
     */
    public boolean isSupDevFragmentShown() {
        return mSupportDevFragment != null && mSupportDevFragment.isVisible();
    }

    /**
     * Getter for BillingManager.
     *
     * @return BillingManager
     */
    @Override
    public BillingManager getBillingManager() {
        return mBillingManager;
    }

    /**
     * Remove loading spinner and refresh the UI.
     */
    public void showRefreshedUi() {
        if (isSupDevFragmentShown()) {
            mSupportDevFragment.refreshUI();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBillingManager.destroy();
    }
}
