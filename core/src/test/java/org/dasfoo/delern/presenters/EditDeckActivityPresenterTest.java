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

package org.dasfoo.delern.presenters;


import org.dasfoo.delern.editdeck.EditDeckActivityPresenter;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckType;
import org.dasfoo.delern.models.User;
import org.dasfoo.delern.test.FirebaseServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class EditDeckActivityPresenterTest {

    @Rule
    public final FirebaseServerRule mFirebaseServer = new FirebaseServerRule();

    private EditDeckActivityPresenter mPresenter = new EditDeckActivityPresenter();

    private User mUser;

    @Before
    public void setupParamPresenter() throws Exception {
        mUser = mFirebaseServer.signIn();
    }

    @Test
    public void deleteDeck() {
        mUser.save().blockingAwait();
        Deck newDeck = new Deck(mUser);
        newDeck.setName("test");
        newDeck.setDeckType(DeckType.BASIC.name());
        newDeck.setAccepted(true);
        newDeck.create().blockingAwait();
        mPresenter.deleteDeck(newDeck);
        List<Deck> deleted = mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                .blockingFirst();
        assertTrue(deleted.size() == 0);
    }

    @Test
    public void updateDeckName() {
        mUser.save().blockingAwait();
        Deck newDeck = new Deck(mUser);
        newDeck.setName("test");
        newDeck.setDeckType(DeckType.BASIC.name());
        newDeck.setAccepted(true);
        newDeck.create().blockingAwait();
        newDeck.setName("test2");
        mPresenter.updateDeck(newDeck);
        List<Deck> updated = mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                .firstOrError().blockingGet();
        assertTrue(updated.size() == 1 && "test2".equals(updated.get(0).getName()));
    }

    @Test
    public void updateDeckType() {
        mUser.save().blockingAwait();
        Deck newDeck = new Deck(mUser);
        newDeck.setName("test");
        newDeck.setDeckType(DeckType.BASIC.name());
        newDeck.setAccepted(true);
        newDeck.create().blockingAwait();
        newDeck.setDeckType(DeckType.GERMAN.name());
        mPresenter.updateDeck(newDeck);
        List<Deck> updated = mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                .firstOrError().blockingGet();
        assertTrue(updated.size() == 1 && DeckType.GERMAN.name()
                .equals(updated.get(0).getDeckType()));
    }

    @Test
    public void updateNameDeckType() {
        mUser.save().blockingAwait();
        Deck newDeck = new Deck(mUser);
        newDeck.setName("test");
        newDeck.setDeckType(DeckType.BASIC.name());
        newDeck.setAccepted(true);
        newDeck.create().blockingAwait();
        newDeck.setName("test2");
        newDeck.setDeckType(DeckType.SWISS.name());
        mPresenter.updateDeck(newDeck);
        List<Deck> updated = mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                .firstOrError().blockingGet();
        assertTrue(updated.size() == 1 && DeckType.SWISS.name()
                .equals(updated.get(0).getDeckType()) && "test2".equals(updated.get(0).getName()));
    }
}
