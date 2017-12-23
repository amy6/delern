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

import org.dasfoo.delern.util.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ServerConnection tracks Firebase Server connection status.
 */
public final class ServerConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerConnection.class);

    // Default to "true" in case we don't want an offline listener.
    private static boolean sConnected = true;
    private static Consumer<Boolean> sOnlineStatusWatcher;

    private ServerConnection() {
    }

    /**
     * Check last received server availability status.
     *
     * @return true if Firebase framework thinks that the Firebase server is connected.
     */
    public static boolean isOnline() {
        return sConnected;
    }

    /**
     * Set a (single) watcher for online status. IMPORTANT: any other watcher will be removed!
     * TODO(dotdoom): expand to allow more online status watchers (or even LiveData).
     *
     * @param callback called immediately with current status, and then every time status changes.
     */
    public static void setOnlineStatusWatcher(final Consumer<Boolean> callback) {
        sOnlineStatusWatcher = callback;
        sOnlineStatusWatcher.accept(sConnected);
    }

    /**
     * Initialize a listener for online/offline status, e.g. for correct operation of
     * MultiWrite.write() callback.
     *
     * @param db DatabaseReference to the root of the database.
     */
    public static void initializeOfflineListener(final FirebaseDatabase db) {
        db.getReference(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                sConnected = dataSnapshot.getValue(Boolean.class);
                if (sOnlineStatusWatcher != null) {
                    sOnlineStatusWatcher.accept(sConnected);
                }
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                LOGGER.error("Offline status listener has been cancelled, re-starting",
                        databaseError.toException());
                initializeOfflineListener(db);
            }
        });
    }
}
