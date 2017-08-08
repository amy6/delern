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

import com.google.firebase.tasks.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * This class is mirrored in "release" version: please update both for feature parity.
 * https://firebase.google.com/docs/reference/admin/java/reference/com/google/firebase/tasks/Task
 *
 * @param <T> task result type.
 */
public class FirebaseTaskAdapter<T> extends TaskAdapter<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseTaskAdapter.class);

    private final Task<T> mTask;

    public FirebaseTaskAdapter(final Task<T> task) {
        mTask = task;
        mTask.addOnFailureListener((final Exception e) ->
                LOGGER.error("Failure in FirebaseTaskAdapter", e));
    }

    @Override
    public TaskAdapter<T> onResult(final Consumer<T> callback) {
        mTask.addOnSuccessListener((final T result) -> callback.accept(result));
        return this;
    }

    @Override
    public FirebaseTaskAdapter<T> onFailure(final Consumer<Exception> callback) {
        mTask.addOnFailureListener((final Exception e) -> callback.accept(e));
        return this;
    }
}
