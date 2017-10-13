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

import org.dasfoo.delern.di.modules.DelernMainActivityModule;
import org.dasfoo.delern.listdecks.DelernMainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * It can be seen as an intermediate object which allows accessing to objects defined in
 * Dagger modules. Interface describes for which types we want to use members injection
 */
@Singleton
@Component(modules = DelernMainActivityModule.class)
public interface DelernMainActivityComponent {
    /**
     * Dagger injector.
     *
     * @param mainActivity sets a type for injection
     */
    void inject(DelernMainActivity mainActivity);
}
