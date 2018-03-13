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

package org.dasfoo.delern.billing.row;

/**
 * A model for SkusAdapter's row which holds all the data to render UI.
 */
public class SkuRowData {
    private final String mSku;
    private final String mTitle;
    private final String mPrice;
    private final String mDescription;
    private final String mBillingType;

    /**
     * Constructor for Model.
     *
     * @param sku         unique product ID
     * @param title       mTitle that will be displayed on payment.
     * @param price       mPrice that will be displayed
     * @param description mDescription that will be desplayed on payment layout.
     * @param type        billing type.
     */
    public SkuRowData(final String sku, final String title, final String price,
                      final String description, final String type) {
        this.mSku = sku;
        this.mTitle = title;
        this.mPrice = price;
        this.mDescription = description;
        this.mBillingType = type;
    }

    /**
     * Getter returns unique product ID.
     *
     * @return unique product ID
     */
    public String getSku() {
        return mSku;
    }

    /**
     * Getter for title of product.
     *
     * @return title of product
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Getter for price of product.
     *
     * @return price of product
     */
    public String getPrice() {
        return mPrice;
    }

    /**
     * Getter for the description of product.
     *
     * @return description of product
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Getter for billing type of product.
     *
     * @return billing type.
     */
    public String getBillingType() {
        return mBillingType;
    }
}

