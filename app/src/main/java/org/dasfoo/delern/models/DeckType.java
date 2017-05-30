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

/**
 * Created by katarina on 1/18/17.
 */

public enum DeckType {

    /**
     * Basic deck type. It means default color for cards.
     */
    BASIC,

    /**
     * Cards in this deck are swissgerman. Extra colors for background available.
     */
    SWISS,

    /**
     * Cards in this deck are german. Extra colors for background available.
     */
    GERMAN
}
