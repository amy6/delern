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

import android.support.annotation.Nullable;

import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;

import org.dasfoo.delern.models.Model;

/**
 * Snapshot parser into Model, for FirebaseRecyclerAdapter.
 *
 * @param <T> class of the model.
 */
public class FirebaseSnapshotParser<T extends Model>
        implements SnapshotParser<T> {

    private final Class<T> mClass;
    private final Model mParent;

    /**
     * Create a new instance of the parser.
     *
     * @param cls    getClass() of the model.
     * @param parent parent to be assigned when parsing the snapshot.
     */
    public FirebaseSnapshotParser(final Class<T> cls, final Model parent) {
        mClass = cls;
        mParent = parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public T parseSnapshot(final DataSnapshot snapshot) {
        return Model.fromSnapshot(snapshot, mClass, mParent);
    }
}
