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

package org.dasfoo.delern.views;

/**
 * Callback interface for communicating Presenter with Activity.
 */
public interface IAddEditCardView {

    /**
     * Initialize view for adding cards.
     */
    void initForAdd();

    /**
     * Initialize view for updating card.
     *
     * @param front front side text for update.
     * @param back back side text for update.
     */
    void initForUpdate(String front, String back);

    /**
     * Handle updating card user message.
     */
    void cardUpdated();

    /**
     * Handle adding card user message.
     */
    void cardAdded();


    /**
     * Checks whether reversed card should be added or not.
     *
     * @return true if add reversed card, false if not.
     */
    boolean addReversedCard();
}
