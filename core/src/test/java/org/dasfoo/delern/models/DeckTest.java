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
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Test for Deck model.
 */
public class DeckTest extends FirebaseServerUnitTest {

    private User mUser;

    @Before
    public void createUser() throws Exception {
        mUser = signIn();
    }

    @Test
    public void decks_createdAndFetched() throws Exception {
        mUser.save().continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<Void>>() {
            @Override
            public TaskAdapter<Void> call(Void parameter) {
                Deck deck = new Deck(mUser);
                deck.setName("My Deck");
                deck.setAccepted(true);
                return deck.create();
            }
        }).continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<List<Deck>>>() {
            @Override
            public TaskAdapter<List<Deck>> call(Void parameter) {
                return mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class);
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

    @Test
    public void decks_renamed() {
        mUser.save().continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<Void>>() {
            @Override
            public TaskAdapter<Void> call(Void parameter) {
                Deck deck = new Deck(mUser);
                deck.setName("ToRename");
                deck.setAccepted(true);
                return deck.create();
            }
        }).continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<List<Deck>>>() {
            @Override
            public TaskAdapter<List<Deck>> call(Void parameter) {
                return mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class);
            }
        }).continueWithOnce(new AbstractTrackingFunction<List<Deck>, TaskAdapter<Void>>() {
            @Override
            public TaskAdapter<Void> call(List<Deck> data) {
                if (data.size() == 1 && data.get(0).getName().equals("ToRename")) {
                    Deck fetchedDeck = data.get(0);
                    fetchedDeck.setName("Renamed");
                    return fetchedDeck.save();
                }
                return null;
            }
        }).continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<List<Deck>>>() {
            @Override
            public TaskAdapter<List<Deck>> call(Void parameter) {
                return mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class);
            }
        }).onResult(new AbstractTrackingProcedure<List<Deck>>() {
            @Override
            public void call(List<Deck> data) {
                if (data.size() == 1 && data.get(0).getName().equals("Renamed")) {
                    testSucceeded();
                }
            }
        });
    }

    @Test
    public void decks_changedDeckType() {
        mUser.save().continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<Void>>() {
            @Override
            public TaskAdapter<Void> call(Void parameter) {
                Deck deck = new Deck(mUser);
                deck.setName("DeckType");
                deck.setAccepted(true);
                return deck.create();
            }
        }).continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<List<Deck>>>() {
            @Override
            public TaskAdapter<List<Deck>> call(Void parameter) {
                return mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class);
            }
        }).continueWithOnce(new AbstractTrackingFunction<List<Deck>, TaskAdapter<Void>>() {
            @Override
            public TaskAdapter<Void> call(List<Deck> data) {
                if (data.size() == 1 && data.get(0).getName().equals("DeckType")) {
                    Deck fetchedDeck = data.get(0);
                    fetchedDeck.setDeckType(DeckType.SWISS.name());
                    return fetchedDeck.save();
                }
                return null;
            }
        }).continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<List<Deck>>>() {
            @Override
            public TaskAdapter<List<Deck>> call(Void parameter) {
                return mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class);
            }
        }).onResult(new AbstractTrackingProcedure<List<Deck>>() {
            @Override
            public void call(List<Deck> data) {
                if (data.size() == 1 && data.get(0).getDeckType().equals(DeckType.SWISS.name())) {
                    testSucceeded();
                }
            }
        });
    }

    @Test
    public void decks_checkedUserDeck() {
        mUser.save().continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<Void>>() {
            @Override
            public TaskAdapter<Void> call(Void parameter) {
                Deck deck = new Deck(mUser);
                deck.setName("UsersDeck");
                deck.setAccepted(true);
                return deck.create();
            }
        }).continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<List<Deck>>>() {
            @Override
            public TaskAdapter<List<Deck>> call(Void parameter) {
                return mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class);
            }
        }).onResult(new AbstractTrackingProcedure<List<Deck>>() {
            @Override
            public void call(List<Deck> data) {
                if (data.size() == 1 && data.get(0).getName().equals("UsersDeck") &&
                        data.get(0).getUser() == mUser) {
                    testSucceeded();
                }
            }
        });
    }

    @Test
    public void decks_createdAndDeleted() throws Exception {
        final Deck deck = new Deck(mUser);
        mUser.save().continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<Void>>() {
            @Override
            public TaskAdapter<Void> call(Void parameter) {
                deck.setName("Created");
                deck.setAccepted(true);
                return deck.create();
            }
        }).continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<List<Deck>>>() {
            @Override
            public TaskAdapter<List<Deck>> call(Void parameter) {
                return mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class);
            }
        }).continueWithOnce(new AbstractTrackingFunction<List<Deck>, TaskAdapter<Void>>() {
            @Override
            public TaskAdapter<Void> call(List<Deck> data) {
                if (data.size() == 1 && data.get(0).getName().equals("Created")) {
                    Deck fetchedDeck = data.get(0);
                    return fetchedDeck.delete();
                }
                return null;
            }
        }).continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<List<Deck>>>() {
            @Override
            public TaskAdapter<List<Deck>> call(Void parameter) {
                System.out.println("Fetching decks again");
                return mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class);
            }
        }).onResult(new AbstractTrackingProcedure<List<Deck>>() {
            @Override
            public void call(List<Deck> data) {
                if (data.size() == 0) {
                    testSucceeded();
                }
            }
        });
    }
}
