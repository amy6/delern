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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.dasfoo.delern.R;


/**
 * Adapter for Spinner that combines text and image.
 */
public class ShareSpinnerAdapter extends ArrayAdapter<String> {
    private final String[] mContentArray;
    private final Integer[] mImageArray = {R.drawable.ic_create_black_24dp,
            R.drawable.ic_can_view_black_24dp};

    /**
     * Constructor. Initializes content of spinner.
     *
     * @param context context.
     */
    public ShareSpinnerAdapter(@NonNull final Context context) {
        super(context, R.layout.share_spinner_layout, R.id.sharingTextView, context.getResources()
                .getStringArray(R.array.share_permissions_spinner));
        this.mContentArray = context.getResources()
                .getStringArray(R.array.share_permissions_spinner);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getDropDownView(final int position, final @Nullable View convertView,
                                final @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.share_spinner_layout, parent, false);

        TextView textView = row.findViewById(R.id.sharingTextView);
        textView.setText(mContentArray[position]);

        ImageView imageView = row.findViewById(R.id.sharingImageView);
        imageView.setImageResource(mImageArray[position]);

        return row;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public View getView(final int position, final View convertView,
                        @NonNull final ViewGroup parent) {
        //return getCustomView(position, convertView, parent);
        ImageView v = new ImageView(parent.getContext());
        v.setImageResource(mImageArray[position]);
        return v;
    }
}
