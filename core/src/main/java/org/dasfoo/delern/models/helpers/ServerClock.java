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

package org.dasfoo.delern.models.helpers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ServerClock tries to track Firebase Server time, if available.
 */
@SuppressWarnings(/* The only place where we can use it */ "checkstyle:NoSystemCurrentTime")
public final class ServerClock {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerClock.class);

    private static double sOffset;

    private ServerClock() {
    }

    /**
     * Initialize offset listener with the specified database.
     *
     * @param db Firebase Database server to fetch time offset from.
     */
    public static void initializeOffsetListener(final FirebaseDatabase db) {
        db.getReference(".info/serverTimeOffset")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot snapshot) {
                        Double offset = snapshot.getValue(Double.class);
                        if (offset != null) {
                            LOGGER.info("Got server time offset: {}", offset);
                            sOffset = offset;
                        }
                    }

                    @Override
                    public void onCancelled(final DatabaseError error) {
                        LOGGER.error("Server time offset listener has been cancelled",
                                error.toException());
                        initializeOffsetListener(db);
                    }
                });
    }

    /**
     * Get current time, adjusted to match Firebase Server, if available.
     *
     * @return current time (UTC), in milliseconds.
     */
    public static double currentTimeMillis() {
        return System.currentTimeMillis() + sOffset;
    }
}
