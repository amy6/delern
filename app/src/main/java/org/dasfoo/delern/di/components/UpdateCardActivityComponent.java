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

package org.dasfoo.delern.di.components;

import org.dasfoo.delern.card.AddEditCardActivity;
import org.dasfoo.delern.di.modules.UpdateCardActivityModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * It can be seen as an intermediate object which allows accessing to objects defined in
 * Dagger modules. Interface describes for which types we want to use members injection
 */
@Singleton
@Component(modules = UpdateCardActivityModule.class)
public interface UpdateCardActivityComponent {
    /**
     * @param addEditCardActivity sets type for injection
     */
    void inject(AddEditCardActivity addEditCardActivity);
}
