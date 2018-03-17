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

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.dasfoo.delern.R;
import org.dasfoo.delern.billing.row.RowViewHolder;
import org.dasfoo.delern.billing.row.SkuRowData;

import java.util.List;

/**
 * Adapter for a RecyclerView that shows SKU details for the app.
 * <p>
 * Note: It's done fragment-specific logic independent and delegates control back to the
 * specified handler (implemented inside AcquireFragment in this example)
 * </p>
 */
public class SkusAdapter extends RecyclerView.Adapter<RowViewHolder>
        implements RowViewHolder.OnButtonClickListener {
    private final BillingProvider mBillingProvider;
    private List<SkuRowData> mListData;

    /* default */ SkusAdapter(final BillingProvider billingProvider) {
        super();
        mBillingProvider = billingProvider;
    }

    /* default */ void updateData(final List<SkuRowData> data) {
        mListData = data;
        notifyDataSetChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RowViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sku_details_row, parent, /* attachToRoot= */false);
        return new RowViewHolder(item, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(final RowViewHolder holder, final int position) {
        SkuRowData data = getData(position);
        if (data != null) {
            holder.setData(data);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        if (mListData == null) {
            return 0;
        }
        return mListData.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPayButtonClicked(final int position) {
        SkuRowData data = getData(position);
        mBillingProvider.getBillingManager().startPurchaseFlow(data.getSku(),
                data.getBillingType());

    }

    @Nullable
    private SkuRowData getData(final int position) {
        if (mListData == null) {
            return null;
        }
        return mListData.get(position);
    }
}
