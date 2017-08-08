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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.dasfoo.delern.models.AbstractModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * A helper for queueing multiple operations (add / update / delete) to the database and
 * applying them at once. Use "save()" and "delete()" to populate; the data is not written to
 * the database until "write()" is called.
 */
public class MultiWrite {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiWrite.class);

    // Default to "true" in case we don't want an offline listener.
    private static boolean sConnected = true;

    @SuppressWarnings("PMD.UseConcurrentHashMap")
    private final Map<String, Object> mData = new HashMap<>();
    private DatabaseReference mRoot;

    /**
     * Initialize a listener for online/offline status, for correct operation of write() callback.
     *
     * @param db DatabaseReference to the root of the database.
     */
    public static void initializeOfflineListener(final FirebaseDatabase db) {
        db.getReference(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                sConnected = dataSnapshot.getValue(Boolean.class);
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                LOGGER.error("Offline status listener has been cancelled, re-starting",
                        databaseError.toException());
                initializeOfflineListener(db);
            }
        });
    }

    /**
     * Get bare Firebase path (relative to the root) from the DatabaseReference.
     *
     * @param reference DatabaseReference to get the path for.
     * @return toString() of the reference with protocol, hostname and port stripped.
     */
    private static String getFirebasePathFromReference(final DatabaseReference reference) {
        try {
            return new URI(reference.toString()).getPath();
        } catch (URISyntaxException e) {
            LOGGER.error("Cannot parse Firebase Database URI: {}", reference, e);
            // TODO(dotdoom): make this all-writable for data recovery
            return "trash";
        }
    }

    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    private void setRootFrom(final DatabaseReference reference) {
        final DatabaseReference newRoot = reference.getRoot();
        if (mRoot == null) {
            mRoot = newRoot;
        } else {
            if (!mRoot.toString().equals(newRoot.toString())) {
                throw new RuntimeException(String.format(
                        "Attempt to write multiple values to different roots: %s and %s",
                        mRoot, newRoot));
            }
        }
    }

    /**
     * Save (add or update) the model to the database.
     *
     * @param model instance of model.
     * @return "this" (for chained calls).
     */
    public MultiWrite save(final AbstractModel model) {
        DatabaseReference reference;
        if (model.exists()) {
            reference = model.getReference();
        } else {
            reference = model.getParent().getChildReference(model.getClass()).push();
            model.setKey(reference.getKey());
        }
        mData.put(getFirebasePathFromReference(reference), model.getFirebaseValue());
        setRootFrom(reference);
        return this;
    }

    /**
     * Delete (assign null to the key) a model from the database.
     *
     * @param model instance of model.
     * @return "this" (for chained calls).
     */
    public MultiWrite delete(final AbstractModel model) {
        return delete(model.getReference());
    }

    /**
     * Delete (assign null to the key) data from the database.
     *
     * @param reference Firebase reference to write the null to.
     * @return "this" (for chained calls).
     */
    public MultiWrite delete(final DatabaseReference reference) {
        mData.put(getFirebasePathFromReference(reference), null);
        setRootFrom(reference);
        return this;
    }

    /**
     * Apply all the queued operations to the database.
     *
     * @return FirebaseTaskAdapter, either immediately complete (when offline) or triggered on
     * Firebase event.
     */
    public TaskAdapter<Void> write() {
        FirebaseTaskAdapter<Void> task = new FirebaseTaskAdapter<>(mRoot.updateChildren(mData))
                .onFailure((final Exception parameter) ->
                        LOGGER.error("Failed to save {}", mData, parameter));
        if (sConnected) {
            return task;
        } else {
            return new NoopTaskAdapter<>(null);
        }
    }
}
