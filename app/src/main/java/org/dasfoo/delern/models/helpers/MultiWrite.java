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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.dasfoo.delern.models.AbstractModel;
import org.dasfoo.delern.models.listeners.OnOperationCompleteListener;
import org.dasfoo.delern.util.LogUtil;

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
    private static final String TAG = LogUtil.tagFor(MultiWrite.class);
    private static boolean sConnected;

    @SuppressWarnings("PMD.UseConcurrentHashMap")
    private final Map<String, Object> mData = new HashMap<>();
    private DatabaseReference mRoot;

    /**
     * Initialize a listener for online/offline status, for correct operation of write() callback.
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
                LogUtil.error(TAG, "Offline status listener has been cancelled, re-starting",
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
            LogUtil.error(TAG, "Cannot parse Firebase Database URI: " + reference, e);
            // TODO(refactoring): make this all-writable for data recovery
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
     * @param onCompleteListener invoked when the operation finishes; onSuccess is triggered
     *                           immediately if database is offline.
     */
    public void write(@Nullable final OnOperationCompleteListener onCompleteListener) {
        if (sConnected && onCompleteListener != null) {
            mRoot.updateChildren(mData, onCompleteListener);
        } else {
            // If offline, we install default listener to log any errors when we are back online.
            // But we don't want this behavior for normal operation, because it breaks the workflow.
            mRoot.updateChildren(mData, OnOperationCompleteListener.getDefaultInstance());
            if (onCompleteListener != null) {
                onCompleteListener.onSuccess();
            }
        }
    }
}
