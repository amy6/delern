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

package org.dasfoo.delern.java.util.function;

/**
 * This interface mimics java.util.function.Function while it's not available on lower Android API
 * levels: https://developer.android.com/studio/write/java8-support.html.
 *
 * @param <T> see java.util.function.Function.
 * @param <R> see java.util.function.Function.
 */
public interface Function<T, R> {
    /**
     * See java.util.function.Function.
     *
     * @param t see java.util.function.Function.
     * @return see java.util.function.Function.
     */
    R apply(T t);
}
