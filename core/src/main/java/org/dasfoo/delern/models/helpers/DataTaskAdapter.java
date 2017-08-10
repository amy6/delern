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

package org.dasfoo.delern.models.helpers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.dasfoo.delern.java.util.function.Function;

/**
 * TaskAdapter for Firebase data available events.
 *
 * @param <T> parsed type (usually a model) that onResult/continueWith will receive.
 */
public class DataTaskAdapter<T> extends TaskAdapter<T> {

    private final Query mQuery;
    private final ValueEventListener mValueEventListener;

    /**
     * Start watching query for values, invoking callbacks as necessary.
     *
     * @param query     Firebase query to fetch values from.
     * @param converter mapper from DataSnapshot into T.
     */
    public DataTaskAdapter(final Query query, final Function<DataSnapshot, T> converter) {
        super();
        mQuery = query;
        mValueEventListener = mQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {
                triggerOnResult(converter.apply(snapshot));
            }

            @Override
            public void onCancelled(final DatabaseError error) {
                triggerOnFailure(error.toException());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        super.stop();
        mQuery.removeEventListener(mValueEventListener);
    }
}
