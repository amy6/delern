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

public class CardTest extends FirebaseServerUnitTest {

    private User mUser;

    @Before
    public void createUser() throws Exception {
        mUser = signIn();
    }

    @Test
    public void cards_createdAndFetched() {
        final Deck deck = new Deck(mUser);
        mUser.save().continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<Void>>() {
            @Override
            public TaskAdapter<Void> call(final Void parameter) {
                deck.setName("CreateCards");
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
                if (data.size() == 1 && data.get(0).getName().equals("CreateCards")) {
                    Card newCard = new Card(data.get(0));
                    return newCard.create("frontSide", "backSide");
                }
                return null;
            }
        }).continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<List<Card>>>() {
            @Override
            public TaskAdapter<List<Card>> call(final Void parameter) {
                return deck.fetchChildren(deck.getChildReference(Card.class), Card.class);
            }
        }).onResult(new AbstractTrackingProcedure<List<Card>>() {
            @Override
            public void call(final List<Card> data) {
                if (data.size() == 1 && data.get(0).getFront().equals("frontSide") &&
                        data.get(0).getBack().equals("backSide")) {
                    testSucceeded();
                }
            }
        });
    }

    @Test
    public void cards_createdAndDeleted() {
        final Deck deck = new Deck(mUser);
        mUser.save().continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<Void>>() {
            @Override
            public TaskAdapter<Void> call(final Void parameter) {
                deck.setName("TestDelete");
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
                if (data.size() == 1 && data.get(0).getName().equals("TestDelete")) {
                    Card newCard = new Card(data.get(0));
                    return newCard.create("frontSide", "backSide");
                }
                return null;
            }
        }).continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<List<Card>>>() {
            @Override
            public TaskAdapter<List<Card>> call(final Void parameter) {
                return deck.fetchChildren(deck.getChildReference(Card.class), Card.class);
            }
        }).continueWithOnce(new AbstractTrackingFunction<List<Card>, TaskAdapter<Void>>() {
            @Override
            public TaskAdapter<Void> call(final List<Card> data) {
                if (data.size() == 1 && data.get(0).getFront().equals("frontSide") &&
                        data.get(0).getBack().equals("backSide")) {
                    return data.get(0).delete();
                }
                return null;
            }
        }).continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<List<Card>>>() {
            @Override
            public TaskAdapter<List<Card>> call(final Void parameter) {
                return deck.fetchChildren(deck.getChildReference(Card.class), Card.class);
            }
        }).onResult(new AbstractTrackingProcedure<List<Card>>() {
            @Override
            public void call(final List<Card> data) {
                if (data.size() == 0) {
                    testSucceeded();
                }
            }
        });
    }

    @Test
    public void cards_createdAndEdited() {
        final Deck deck = new Deck(mUser);
        mUser.save().continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<Void>>() {
            @Override
            public TaskAdapter<Void> call(final Void parameter) {
                deck.setName("TestRename");
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
                if (data.size() == 1 && data.get(0).getName().equals("TestRename")) {
                    Card newCard = new Card(data.get(0));
                    return newCard.create("frontSide", "backSide");
                }
                return null;
            }
        }).continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<List<Card>>>() {
            @Override
            public TaskAdapter<List<Card>> call(final Void parameter) {
                return deck.fetchChildren(deck.getChildReference(Card.class), Card.class);
            }
        }).continueWithOnce(new AbstractTrackingFunction<List<Card>, TaskAdapter<Void>>() {
            @Override
            public TaskAdapter<Void> call(final List<Card> data) {
                if (data.size() == 1 && data.get(0).getFront().equals("frontSide") &&
                        data.get(0).getBack().equals("backSide")) {
                    data.get(0).setFront("frontSide2");
                    data.get(0).setBack("backSide2");
                    return data.get(0).save();
                }
                return null;
            }
        }).continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<List<Card>>>() {
            @Override
            public TaskAdapter<List<Card>> call(final Void parameter) {
                return deck.fetchChildren(deck.getChildReference(Card.class), Card.class);
            }
        }).onResult(new AbstractTrackingProcedure<List<Card>>() {
            @Override
            public void call(final List<Card> data) {
                if (data.size() == 1 && data.get(0).getFront().equals("frontSide2") &&
                        data.get(0).getBack().equals("backSide2")) {
                    testSucceeded();
                }
            }
        });
    }

    @Test
    public void cards_createdAndAnsweredTrue() {
        final Deck deck = new Deck(mUser);
        mUser.save().continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<Void>>() {
            @Override
            public TaskAdapter<Void> call(final Void parameter) {
                deck.setName("TestAnswer");
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
                if (data.size() == 1 && data.get(0).getName().equals("TestAnswer")) {
                    Card newCard = new Card(data.get(0));
                    return newCard.create("frontSide", "backSide");
                }
                return null;
            }
        }).continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<Card>>() {
            @Override
            public TaskAdapter<Card> call(final Void parameter) {
                return deck.startScheduledCardWatcher();
            }
        }).continueWithOnce(new AbstractTrackingFunction<Card, TaskAdapter<Void>>() {
            @Override
            public TaskAdapter<Void> call(final Card card) {
                return card.answer(true);
            }
        }).continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<List<ScheduledCard>>>() {
            @Override
            public TaskAdapter<List<ScheduledCard>> call(final Void parameter) {
                return deck.fetchChildren(deck.getChildReference(ScheduledCard.class),
                        ScheduledCard.class);
            }
        }).onResult(new AbstractTrackingProcedure<List<ScheduledCard>>() {
            @Override
            public void call(final List<ScheduledCard> data) {
                if (data.size() == 1
                        && data.get(0).getLevel().equals(Level.L1.name())) {
                    testSucceeded();
                }
            }
        });
    }

    @Test
    public void cards_createdAndAnsweredFalse() {
        final Deck deck = new Deck(mUser);
        mUser.save().continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<Void>>() {
            @Override
            public TaskAdapter<Void> call(final Void parameter) {
                deck.setName("TestAnswer");
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
                if (data.size() == 1 && data.get(0).getName().equals("TestAnswer")) {
                    Card newCard = new Card(data.get(0));
                    return newCard.create("frontSide", "backSide");
                }
                return null;
            }
        }).continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<Card>>() {
            @Override
            public TaskAdapter<Card> call(final Void parameter) {
                return deck.startScheduledCardWatcher();
            }
        }).continueWithOnce(new AbstractTrackingFunction<Card, TaskAdapter<Void>>() {
            @Override
            public TaskAdapter<Void> call(final Card card) {
                return card.answer(false);
            }
        }).continueWithOnce(new AbstractTrackingFunction<Void, TaskAdapter<List<ScheduledCard>>>() {
            @Override
            public TaskAdapter<List<ScheduledCard>> call(final Void parameter) {
                return deck.fetchChildren(deck.getChildReference(ScheduledCard.class),
                        ScheduledCard.class);
            }
        }).onResult(new AbstractTrackingProcedure<List<ScheduledCard>>() {
            @Override
            public void call(final List<ScheduledCard> data) {
                if (data.size() == 1
                        && data.get(0).getLevel().equals(Level.L0.name())) {
                    testSucceeded();
                }
            }
        });
    }

}
