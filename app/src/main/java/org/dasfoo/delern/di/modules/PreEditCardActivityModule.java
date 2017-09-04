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

package org.dasfoo.delern.di.modules;

import org.dasfoo.delern.previewcard.PreEditCardActivityPresenter;
import org.dasfoo.delern.previewcard.IPreEditCardView;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger 2 class that says how to inject PreEditCardActivity.
 */
@Module
public class PreEditCardActivityModule {
    private final IPreEditCardView mView;

    /**
     * Constructor. It gets interface as parameter that implemented in PreEditCardActivity
     * for callbacks from Presenter.
     *
     * @param view interface to init Presenter for callbacks.
     */
    public PreEditCardActivityModule(final IPreEditCardView view) {
        this.mView = view;
    }

    @Provides
    /* default */ PreEditCardActivityPresenter providePresenter() {
        return new PreEditCardActivityPresenter(mView);
    }
}
