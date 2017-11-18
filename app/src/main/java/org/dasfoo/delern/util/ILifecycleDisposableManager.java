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

package org.dasfoo.delern.util;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;

import io.reactivex.disposables.Disposable;

/**
 * IDisposableManager with a default implementation that disposes of managed objects on
 * LifecycleOwner (e.g. Activity) ON_DESTROY lifecycle event.
 */
public interface ILifecycleDisposableManager extends IDisposableManager, LifecycleOwner {
    /**
     * {@inheritDoc}
     */
    @Override
    // TODO(dotdoom): this is slightly sub-optimal: we create an observer per Disposable. Would be
    //                nice to have a single observer, but interfaces can't have fields.
    default void manageDisposable(final Disposable d) {
        getLifecycle().addObserver(new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            public void releaseDisposable() {
                d.dispose();
            }
        });
    }
}
