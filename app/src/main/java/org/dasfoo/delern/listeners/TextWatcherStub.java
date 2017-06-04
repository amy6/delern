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

package org.dasfoo.delern.listeners;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Stub implements TextWatcher methods (for convenience).
 */
public class TextWatcherStub implements TextWatcher {
    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeTextChanged(final CharSequence s, final int start, final int count,
                                  final int after) {
        // To be implemented in inherited class
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTextChanged(final CharSequence s, final int start, final int before,
                              final int count) {
        // To be implemented in inherited class
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterTextChanged(final Editable s) {
        // To be implemented in inherited class
    }
}
