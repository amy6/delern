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
import org.dasfoo.delern.editdeck.IEditDeckView;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckAccess;
import org.dasfoo.delern.models.DeckType;
import org.dasfoo.delern.models.User;
import org.dasfoo.delern.test.FirebaseServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class EditDeckActivityPresenterTest {

    private static int TIMEOUT = 5000;

    @Rule
    public final FirebaseServerRule mFirebaseServer = new FirebaseServerRule();

    private User mUser;
    private Deck mDeck;
    EditDeckActivityPresenter mPresenter;
    IEditDeckView mView;

    @Before
    public void setupParamPresenter() throws Exception {
        mUser = mFirebaseServer.signIn();
        mUser.save().blockingAwait();

        mDeck = new Deck(mUser);
        mDeck.setName("test");
        mDeck.setDeckType(DeckType.BASIC.name());
        mDeck.setAccepted(true);
        mDeck.create().blockingAwait();
        DeckAccess deckAccess = new DeckAccess(mDeck);
        deckAccess.setAccess("owner");
        mView = mock(IEditDeckView.class);
        mPresenter = new EditDeckActivityPresenter(mView, deckAccess);
    }

    @Test
    public void deleteDeck() {
        mPresenter.deleteDeck();
        List<Deck> deleted = mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                .blockingFirst();
        assertTrue(deleted.size() == 0);
    }

    @Test
    public void updateDeckName() {
        mDeck.setName("test2");
        mPresenter.updateDeck(mDeck);
        List<Deck> updated = mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                .firstOrError().blockingGet();
        assertTrue(updated.size() == 1 && "test2".equals(updated.get(0).getName()));
    }

    @Test
    public void updateDeckType() {
        mPresenter.selectDeckType(DeckType.GERMAN.ordinal());
        mPresenter.updateDeck(mDeck);
        List<Deck> updated = mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                .firstOrError().blockingGet();
        assertTrue(updated.size() == 1 && DeckType.GERMAN.name()
                .equals(updated.get(0).getDeckType()));
    }

    @Test
    public void updateNameDeckType() {
        mDeck.setName("test2");
        mPresenter.selectDeckType(DeckType.SWISS.ordinal());
        mPresenter.updateDeck(mDeck);
        List<Deck> updated = mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                .firstOrError().blockingGet();
        assertTrue(updated.size() == 1 && DeckType.SWISS.name()
                .equals(updated.get(0).getDeckType()) && "test2".equals(updated.get(0).getName()));
    }

    @Test
    public void chooseNotExistedDeckType() {
        int deckType = mPresenter.setDefaultDeckType(DeckType.values().length - 1);
        assertTrue(deckType == 0);
    }

    @Test
    public void selectNotExistedDeckType() {
        mPresenter.selectDeckType(DeckType.values().length + 1);
        verify(mView, timeout(TIMEOUT)).showDeckTypeNotExistUserMessage();
    }
}
