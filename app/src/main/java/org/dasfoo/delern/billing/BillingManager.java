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

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.dasfoo.delern.models.User;
import org.dasfoo.delern.util.PerfEventTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BillingManager will handle all the interactions with Play Store
 * (via Billing library), maintain connection to it through BillingClient and cache
 * temporary states/data if needed.
 */
public class BillingManager implements PurchasesUpdatedListener {

    /**
     * Sku parameter id.
     */
    public static final String SKU_SUP_DEV1 = "sup_dev_1";
    /**
     * Sku parameter id.
     */
    public static final String SKU_SUP_DEV2 = "sup_dev_2";
    /**
     * Sku parameter id.
     */
    public static final String SKU_SUP_DEV5 = "sup_dev_5";

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingManager.class);
    private static final Map<String, List<String>> SKUS;

    static {
        SKUS = new HashMap<>();
        SKUS.put(BillingClient.SkuType.INAPP,
                Arrays.asList(SKU_SUP_DEV1, SKU_SUP_DEV2, SKU_SUP_DEV5));
        //SKUS.put(BillingClient.SkuType.SUBS, Arrays.asList("gold_monthly", "gold_yearly"));
    }

    private final Activity mActivity;
    private final ConsumeResponseListener mConsumeResponseListener;
    private final User mUser;
    private BillingClient mBillingClient;

    /**
     * Initializes BillingClient. When connection is set up, consumes
     * products.
     *
     * @param activity activity to initialize BillingClient.
     * @param user     user to track for analytics.
     */
    public BillingManager(final Activity activity, final User user) {
        mActivity = activity;
        mUser = user;
        mBillingClient = BillingClient.newBuilder(mActivity).setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(
                    @BillingClient.BillingResponse final int billingResponse) {
                if (billingResponse == BillingClient.BillingResponse.OK) {
                    LOGGER.info("onBillingSetupFinished() response: {} ", billingResponse);
                    // If item is paid, method consumes it.
                    consumePurchases();
                } else {
                    LOGGER.error("onBillingSetupFinished() error code: {} ", billingResponse);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                LOGGER.warn("onBillingServiceDisconnected");
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });

        mConsumeResponseListener = (responseCode, purchaseToken) -> {
            if (responseCode == BillingClient.BillingResponse.OK) {
                LOGGER.info("Product was consumed: {}", responseCode);
            } else {
                LOGGER.error("Product wasn't consumed code {}", responseCode);
            }
        };
    }

    /* default */ void startPurchaseFlow(final String skuId, final String billingType) {
        // Launch billing flow
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setType(billingType).setSku(skuId).build();
        // Check that payments are supported.
        if (mBillingClient.isFeatureSupported(billingType) == BillingClient.BillingResponse.OK) {
            Bundle payload = new Bundle();
            payload.putString(FirebaseAnalytics.Param.ITEM_ID, mUser.getKey());
            payload.putString(FirebaseAnalytics.Param.VIRTUAL_CURRENCY_NAME, skuId);
            PerfEventTracker.trackEvent(PerfEventTracker.Event.START_PURCHASE, mActivity, payload);
            mBillingClient.launchBillingFlow(mActivity, billingFlowParams);
        }
    }

    /**
     * Fetches results from a cache provided by the Google Play Store app without
     * initiating a network request. Consumes all purchases.
     */
    private void consumePurchases() {
        Purchase.PurchasesResult purchasesResult = mBillingClient
                .queryPurchases(BillingClient.SkuType.INAPP);
        LOGGER.info("Start consuming, purchasesResult: {}", purchasesResult.getResponseCode());
        if (purchasesResult.getPurchasesList() != null) {
            for (Purchase purchase : purchasesResult.getPurchasesList()) {
                mBillingClient.consumeAsync(purchase.getPurchaseToken(), mConsumeResponseListener);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPurchasesUpdated(final int responseCode,
                                   @Nullable final List<Purchase> purchases) {
        if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                mBillingClient.consumeAsync(purchase.getPurchaseToken(), mConsumeResponseListener);
            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            LOGGER.warn("Billing was canceled: {}", responseCode);
            Bundle payload = new Bundle();
            payload.putString(FirebaseAnalytics.Param.ITEM_ID, mUser.getKey());
            PerfEventTracker
                    .trackEvent(PerfEventTracker.Event.CANCEL_PURCHASE, mActivity, payload);
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            LOGGER.warn("Not handled error code: {}", responseCode);
            // Handle any other error codes.
        }
    }

    /* default */ List<String> getSkus(@BillingClient.SkuType final String type) {
        return SKUS.get(type);
    }

    /**
     * Allows to get all the details about products (SKUs) defined at Google Play Developer Console.
     *
     * @param itemType type of Sku.
     * @param skuList  list of SKUs to get info about.
     * @param listener a listener to the actual response from BillingClient library.
     */
    /* default */ void querySkuDetailsAsync(@BillingClient.SkuType final String itemType,
                                            final List<String> skuList,
                                            final SkuDetailsResponseListener listener) {
        SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder()
                .setSkusList(skuList).setType(itemType).build();
        mBillingClient.querySkuDetailsAsync(skuDetailsParams,
                listener);
    }

    /**
     * Clear the resources.
     */
    public void destroy() {
        if (mBillingClient != null && mBillingClient.isReady()) {
            mBillingClient.endConnection();
            mBillingClient = null;
        }
    }
}
