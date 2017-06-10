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

package org.dasfoo.delern.models;


import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.dasfoo.delern.listeners.AbstractDataAvailableListener;
import org.dasfoo.delern.listeners.AbstractOnFBDataChangeListener;
import org.dasfoo.delern.listeners.OnFBOperationCompleteListener;
import org.dasfoo.delern.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO(refactoring): Review method visibility

/**
 * Base class for models, implementing Firebase functionality.
 */
public abstract class AbstractModel {

    @Exclude
    private String mKey;

    @Exclude
    private AbstractModel mParent;

    /**
     * The only constructor.
     * @param parent a parent model for this instance. There is a limited set of which classes
     *               are expected as parents, it's usually seen in custom getXXX methods.
     *               This parameter must not be null unless called in a form of super(null) from a
     *               private parameterless constructor used by Firebase.
     */
    protected AbstractModel(final AbstractModel parent) {
        mParent = parent;
    }

    /**
     * Parse model from a database snapshot using getValue().
     * @param snapshot a snapshot pointing to model data (not the list of models).
     * @param cls a model class, e.g. Card.class or card.getClass().
     * @param parent a parent model. See AbstractModel constructor for limitations.
     * @param <T> an AbstractModel subclass.
     * @return an instance of T with key and parent set, or null.
     */
    public static <T extends AbstractModel> T fromSnapshot(final DataSnapshot snapshot,
                                                           final Class<T> cls,
                                                           final AbstractModel parent) {
        T model = snapshot.getValue(cls);
        if (model == null) {
            return null;
        }
        model.setKey(snapshot.getKey());
        model.setParent(parent);
        return model;
    }

    /**
     * Count the child nodes (non-recursively) returned by the query.
     * @param query a DatabaseReference or a specific query.
     * @param callback a callback to run when data is available and then every time the count
     *                 changes. To stop the updates and save resources, call callback.clean().
     */
    public static void fetchCount(final Query query,
                                  final AbstractDataAvailableListener<Long> callback) {
        callback.setQuery(query);
        // TODO(refactoring): this should be childeventlistener, not valueeventlistener
        query.addValueEventListener(callback.setListener(
                new AbstractOnFBDataChangeListener(callback) {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        callback.onData(dataSnapshot.getChildrenCount());
                    }
                }));
    }

    /**
     * Get the key assigned when fetching from or saving to the database.
     * @return value of the key (usually a fairly random string).
     */
    @Exclude
    public String getKey() {
        return mKey;
    }

    /**
     * Set the known key for the model. This may be used internally by AbstractModel when saving
     * the new value to the database, or externally when unpacking the value from Parcel.
     * Another use case is when child model would set the key to a parent model when they share
     * the same key.
     * @param key value of the key (usually a fairly random string).
     */
    @Exclude
    protected void setKey(final String key) {
        this.mKey = key;
    }

    /**
     * Whether the model (supposedly) exists in the database.
     * @return true if key is not null.
     */
    @Exclude
    public boolean exists() {
        return getKey() != null;
    }

    /**
     * Get a parent model assigned when this object is created, or by fromSnapshot when restoring
     * from the database. This method is usually overridden in subclasses to provide a fine-grained
     * parent access (i.e. with a specific class rather than just AbstractModel).
     * @return AbstractModel
     */
    @Exclude
    protected AbstractModel getParent() {
        return mParent;
    }

    /**
     * Set a parent model for this object. This method is not intended to be used directly, because
     * parent is a required parameter to the model's constructor. It is called internally in
     * fromSnapshot() because DataSnapshot.getValue() doesn't allow constructor parameters.
     * @param parent parent model which is deserializing the value in fromSnapshot().
     */
    @Exclude
    protected void setParent(final AbstractModel parent) {
        mParent = parent;
    }

    /**
     * Return a value that should be saved to the database for this model. It's usually the same
     * object, but may be overwritten in child classes for trivial models or for performance.
     * @return value to be written by Firebase to getKey() location/
     */
    @Exclude
    protected Object getFirebaseValue() {
        return this;
    }

    /**
     * Get a DatabaseReference pointing to the root of all child nodes belonging to this parent.
     * There may be more levels of hierarchy between the reference returned and child objects in the
     * database. On a related note, child nodes are usually not under the parent node in JSON tree;
     * instead, they have their own path from the root of the database.
     * @param childClass class of the child model.
     * @param <T> class of the child model.
     * @return DatabaseReference pointing to the root of all child nodes (recursively).
     */
    public abstract <T> DatabaseReference getChildReference(Class<T> childClass);

    /**
     * Get a DatabaseReference pointing to a specific child node, or root of indirect child nodes
     * belonging to a direct child.
     * @param childClass class of the child model.
     * @param key key of the direct child (doesn't always mean the key of the childClass model).
     * @param <T> class of the child model.
     * @return DatabaseReference pointing to a specific child node or a root of child nodes.
     */
    public <T> DatabaseReference getChildReference(final Class<T> childClass,
                                                   final String key) {
        return getChildReference(childClass).child(key);
    }

    /**
     * Write the current model to the database, creating a new node if it doesn't exist.
     * @param callback called when the operation completes, or immediately if offline.
     */
    @Exclude
    public void save(@Nullable final AbstractDataAvailableListener callback) {
        new MultiWrite().save(this, callback).write();
    }

    /**
     * Fetch a single model from the database, and watch for changes until callback.cleanup() is
     * called. The model will have its parent set to "this" (receiver).
     * @param query Firebase query returning a node to directly parse into the model.
     * @param cls class of the model to parse the data into.
     * @param callback callback when the data is first available or changed.
     * @param ignoreNull do not invoke callback for null objects.
     * @param <T> class of the model to parse the data into.
     */
    @Exclude
    public <T extends AbstractModel> void fetchChild(
            final Query query, final Class<T> cls, final AbstractDataAvailableListener<T> callback,
            final boolean ignoreNull) {
        callback.setQuery(query);
        query.addValueEventListener(callback.setListener(
                new AbstractOnFBDataChangeListener(callback) {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if (!ignoreNull || dataSnapshot.getValue() != null) {
                            callback.onData(AbstractModel.fromSnapshot(
                                    dataSnapshot, cls, AbstractModel.this));
                        }
                    }
                }));
    }

    /**
     * Similar to fetchChild, but iterates over the objects pointed to by query and invokes callback
     * with a List.
     * @param query see fetchChild.
     * @param cls see fetchChild.
     * @param callback see fetchChild.
     * @param <T> see fetchChild.
     */
    @Exclude
    public <T extends AbstractModel> void fetchChildren(
            final Query query, final Class<T> cls,
            final AbstractDataAvailableListener<List<T>> callback) {
        callback.setQuery(query);
        query.addValueEventListener(callback.setListener(
                new AbstractOnFBDataChangeListener(callback) {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        List<T> items = new ArrayList<>((int) dataSnapshot.getChildrenCount());
                        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                            items.add(AbstractModel.fromSnapshot(itemSnapshot, cls,
                                    AbstractModel.this));
                        }
                        callback.onData(items);
                    }
                }));
    }

    /**
     * Get the reference pointing to the current model, if it exists.
     * @return a Firebase reference to the node where the model data is located.
     */
    @Exclude
    public DatabaseReference getReference() {
        return getParent().getChildReference(this.getClass(), this.getKey());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "parent=" + getParent() + ", key='" + getKey() + '\'';
    }

    /**
     * A helper for queueing multiple operations (add / update / delete) to the database and
     * applying them at once.
     */
    public static class MultiWrite {
        @SuppressWarnings("PMD.UseConcurrentHashMap")
        private final Map<String, Object> mData = new HashMap<>();

        /**
         * Save (add or update) the model to the database.
         * @param model instance of model.
         * @param callback invoked when the operation is completed, or immediately if offline.
         * @return "this" (for chained calls).
         */
        public MultiWrite save(final AbstractModel model,
                               @Nullable final AbstractDataAvailableListener callback) {
            DatabaseReference reference;
            if (model.exists()) {
                reference = model.getReference();
            } else {
                reference = model.getParent().getChildReference(model.getClass()).push();
                model.setKey(reference.getKey());
            }
            if (callback != null) {
                callback.setQuery(reference);
                reference.addValueEventListener(
                        callback.setListener(new AbstractOnFBDataChangeListener(callback) {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    callback.clean();
                                    // TODO(refactoring): if updateChildren below fails,
                                    // listener hangs forever
                                    // TODO(refactoring): set a timer?
                                    callback.onData(fromSnapshot(dataSnapshot, model.getClass(),
                                            model.getParent()));
                                }
                            }
                        }));
            }
            mData.put(StringUtil.getFirebasePathFromReference(reference), model.getFirebaseValue());
            return this;
        }

        /**
         * Delete (assign null to the key) a model from the database.
         * @param model instance of model.
         * @param callback invoked when the operation is completed, or immediately if offline.
         * @return "this" (for chained calls).
         */
        public MultiWrite delete(final AbstractModel model,
                                 @Nullable final AbstractDataAvailableListener callback) {
            return delete(model.getReference(), callback);
        }

        /**
         * Delete (assign null to the key) data from the database.
         * @param reference Firebase reference to write the null to.
         * @param callback invoked when the operation is completed, or immediately if offline.
         * @return "this" (for chained calls).
         */
        public MultiWrite delete(final DatabaseReference reference,
                                 @Nullable final AbstractDataAvailableListener callback) {
            // TODO(refactoring): remove callback from delete()
            if (callback != null) {
                reference.addListenerForSingleValueEvent(
                        new AbstractOnFBDataChangeListener(callback) {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() == null) {
                                    callback.onData(null);
                                } else {
                                    callback.onError(null);
                                }
                            }
                        });
            }
            mData.put(StringUtil.getFirebasePathFromReference(reference), null);
            return this;
        }

        /**
         * Apply all the queued operations to the database.
         */
        public void write() {
            FirebaseDatabase.getInstance().getReference().updateChildren(mData,
                    OnFBOperationCompleteListener.getDefaultInstance());
        }
    }
}
