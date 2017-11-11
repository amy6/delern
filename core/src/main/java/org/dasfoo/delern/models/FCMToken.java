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
 * Model class for FCM token.
 */
@SuppressWarnings(/* firebase */ {"checkstyle:MemberName", "checkstyle:HiddenField"})
public class FCMToken extends Model {

    private String name;

    /**
     * An empty constructor is required for Firebase deserialization.
     */
    private FCMToken() {
        super(null, null);
    }

    /**
     * Create an FCMToken object associated with a User.
     *
     * @param user User which this token belongs to.
     */
    public FCMToken(final User user) {
        super(user, null);
    }

    /**
     * Getter for FCM token name.
     *
     * @return token name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets FCM token name.
     *
     * @param name token name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "FCMToken{" + super.toString() +
                ", name='" + name + '\'' +
                '}';
    }
}
