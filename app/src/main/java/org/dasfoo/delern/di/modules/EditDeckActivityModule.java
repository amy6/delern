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

import org.dasfoo.delern.editdeck.EditDeckActivityPresenter;
import org.dasfoo.delern.editdeck.IEditDeckView;
import org.dasfoo.delern.models.Deck;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger 2 class that says how to inject EditDeckActivity.
 */
@Module
public class EditDeckActivityModule {
    private final IEditDeckView mView;
    private final Deck mDeck;

    /**
     * Constructor.
     *
     * @param view view to init EditDeckActivityPresenter.
     * @param deck deck to init EditDeckActivityPresenter.
     */
    public EditDeckActivityModule(final IEditDeckView view,
                                  final Deck deck) {
        this.mView = view;
        this.mDeck = deck;
    }

    @Provides
    /* default */ EditDeckActivityPresenter providePresenter() {
        return new EditDeckActivityPresenter(mView, mDeck);
    }
}
