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

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;

import org.dasfoo.delern.R;
import org.dasfoo.delern.billing.row.SkuRowData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a screen with various in-app purchase and subscription options.
 */
public class SupportDevFragment extends DialogFragment {
    private static final Logger LOGGER = LoggerFactory.getLogger(SupportDevFragment.class);
    private RecyclerView mRecyclerView;
    private SkusAdapter mAdapter;
    private View mLoadingView;
    private TextView mErrorTextView;
    private BillingProvider mBillingProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    private void configureToolbar(final View root) {
        Toolbar toolbar = root.findViewById(R.id.toolbar);

        final Drawable upArrow = ContextCompat.getDrawable(root.getContext(),
                R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(ContextCompat.getColor(root.getContext(),
                android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(upArrow);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle(R.string.nav_support_development);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        View root = inflater
                .inflate(R.layout.support_dev_fragment, container, /* attachToRoot=*/false);
        mErrorTextView = root.findViewById(R.id.error_textview);
        mRecyclerView = root.findViewById(R.id.list);
        mLoadingView = root.findViewById(R.id.progress_bar);
        configureToolbar(root);
        setWaitScreen(true);
        onManagerReady((BillingProvider) getActivity());
        return root;
    }

    /**
     * Refreshes this fragment's UI.
     */
    public void refreshUI() {
        LOGGER.debug("Looks like purchases list might have been updated - refreshing the UI");
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Notifies the fragment that billing manager is ready and provides a BillingProvider
     * instance to access it.
     *
     * @param billingProvider BillingProvider
     */
    public void onManagerReady(final BillingProvider billingProvider) {
        mBillingProvider = billingProvider;
        if (mRecyclerView != null) {
            mAdapter = new SkusAdapter(mBillingProvider);
            if (mRecyclerView.getAdapter() == null) {
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
            handleManagerAndUiReady();
        }
    }

    /**
     * Enables or disables "please wait" screen.
     */
    private void setWaitScreen(final boolean set) {
        if (set) {
            mRecyclerView.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mLoadingView.setVisibility(View.GONE);
        }
    }

    /**
     * Executes query for SKU details at the background thread.
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops" /* TODO(ksheremet): refactor */)
    private void handleManagerAndUiReady() {
        final List<SkuRowData> inList = new ArrayList<>();
        SkuDetailsResponseListener responseListener = (responseCode, skuDetailsList) -> {
            if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
                // Repacking the result for an adapter
                SkuRowData rowData;
                for (SkuDetails details : skuDetailsList) {
                    LOGGER.info("Found sku: {} ", details);
                    rowData = new SkuRowData(details.getSku(), details.getTitle(),
                            details.getPrice(), details.getDescription(), details.getType());
                    inList.add(rowData);
                }
                if (inList.isEmpty()) {
                    displayAnErrorIfNeeded();
                } else {
                    mAdapter.updateData(inList);
                    setWaitScreen(false);
                }
            }
        };

        // Start querying for in-app SKUs
        List<String> skus = mBillingProvider.getBillingManager()
                .getSkus(BillingClient.SkuType.INAPP);
        mBillingProvider.getBillingManager()
                .querySkuDetailsAsync(BillingClient.SkuType.INAPP, skus, responseListener);
        // Start querying for subscriptions SKUs
        /*skus = mBillingProvider.getBillingManager().getSkus(BillingClient.SkuType.SUBS);
        mBillingProvider.getBillingManager().querySkuDetailsAsync(BillingClient.SkuType.SUBS,
        skus, responseListener);*/
    }

    private void displayAnErrorIfNeeded() {
        if (getActivity() == null || getActivity().isFinishing()) {
            LOGGER.info("No need to show an error - activity is finishing already");
            return;
        }

        mLoadingView.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.VISIBLE);
        mErrorTextView.setText(R.string.error_occurred_user_message);

        // TODO(ksheremet): Here you will need to handle various respond codes from BillingManager
    }

}
