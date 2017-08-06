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

import org.dasfoo.delern.models.helpers.AbstractTrackingFunction;
import org.dasfoo.delern.models.helpers.AbstractTrackingProcedure;
import org.dasfoo.delern.models.helpers.TaskAdapter;
import org.dasfoo.delern.test.FirebaseServerUnitTest;
import org.junit.Test;

import java.util.List;

/**
 * Test for Deck model.
 */
public class DeckTest extends FirebaseServerUnitTest {

    @Test
    public void decks_createdAndFetched() throws Exception {
        final User user = signIn();
        user.save().continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<Void>>() {
            @Override
            public TaskAdapter<Void> call(Void parameter) {
                Deck deck = new Deck(user);
                deck.setName("My Deck");
                deck.setAccepted(true);
                return deck.create();
            }
        }).continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<List<Deck>>>() {
            @Override
            public TaskAdapter<List<Deck>> call(Void parameter) {
                return user.fetchChildren(user.getChildReference(Deck.class), Deck.class);
            }
        }).onResult(new AbstractTrackingProcedure<List<Deck>>() {
            @Override
            public void call(List<Deck> data) {
                if (data.size() == 1 && data.get(0).getName().equals("My Deck")) {
                    testSucceeded();
                }
            }
        });
    }
}
