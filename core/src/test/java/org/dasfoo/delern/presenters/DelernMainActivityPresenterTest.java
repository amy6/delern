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

import org.dasfoo.delern.listdecks.DelernMainActivityPresenter;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckType;
import org.dasfoo.delern.models.User;
import org.dasfoo.delern.test.FirebaseServerRule;
import org.dasfoo.delern.listdecks.IDelernMainView;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class DelernMainActivityPresenterTest {

    private final static int TIMEOUT = 5000;

    @Rule
    public final FirebaseServerRule mFirebaseServer = new FirebaseServerRule();

    @Mock
    private IDelernMainView mDelernMainView;
    @InjectMocks
    private DelernMainActivityPresenter mPresenter;

    private User mUser;

    @Before
    public void setupParamPresenter() throws Exception {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        mUser = mFirebaseServer.signIn();
    }

    @Test
    public void createDeck() throws Exception {
        mUser.save().blockingAwait();
        mPresenter.onCreate(mUser);
        mPresenter.createNewDeck("test");
        verify(mDelernMainView, timeout(TIMEOUT)).addCardsToDeck(any(Deck.class));
    }

    @Test
    public void signInCallbackOnNullUser() {
        mPresenter.onCreate(null);
        verify(mDelernMainView).signIn();
    }

    @Test
    public void signInCallbackOnNotExistingUser() {
        User user = new User(null);
        mPresenter.onCreate(user);
        verify(mDelernMainView).signIn();
    }

    @Test
    public void noDecksUserMessage() {
        mUser.save().blockingAwait();
        mPresenter.onCreate(mUser);
        mPresenter.onStart();
        verify(mDelernMainView, timeout(TIMEOUT)).showProgressBar(Boolean.FALSE);
        verify(mDelernMainView, timeout(TIMEOUT)).noDecksMessage(Boolean.TRUE);
    }

    @Test
    public void userHasDecks() {
        ArgumentCaptor<Deck> deckArgumentCaptor = ArgumentCaptor.forClass(Deck.class);
        mUser.save().blockingAwait();
        mPresenter.onCreate(mUser);
        mPresenter.onStart();
        verify(mDelernMainView, timeout(TIMEOUT)).noDecksMessage(Boolean.TRUE);
        mPresenter.createNewDeck("test");
        // Called 2 times: onStart and after deck was created.
        verify(mDelernMainView, timeout(TIMEOUT).times(2)).showProgressBar(Boolean.FALSE);
        verify(mDelernMainView, timeout(TIMEOUT)).noDecksMessage(Boolean.FALSE);
        verify(mDelernMainView, timeout(TIMEOUT)).addCardsToDeck(deckArgumentCaptor.capture());
        assertEquals("test", deckArgumentCaptor.getValue().getName());
    }

    @Test
    public void getUserInfo() {
        mUser.save().blockingAwait();
        mPresenter.onCreate(mUser);
        mPresenter.getUserInfo();
        verify(mDelernMainView, timeout(TIMEOUT)).updateUserProfileInfo(eq(mUser));
    }

    @Test
    public void getUser() {
        mPresenter.onCreate(mUser);
        User user = mPresenter.getUser();
        assertEquals(mUser, user);
    }

    @Test
    public void deleteDeckWithListener() {
        mUser.save().blockingAwait();
        Deck newDeck = new Deck(mUser);
        newDeck.setName("test");
        newDeck.setDeckType(DeckType.BASIC.name());
        newDeck.setAccepted(true);
        newDeck.create().blockingAwait();
        mPresenter.onCreate(mUser);
        mPresenter.onStart();
        verify(mDelernMainView, timeout(TIMEOUT)).noDecksMessage(Boolean.FALSE);
        mPresenter.deleteDeck(newDeck);
        verify(mDelernMainView, timeout(TIMEOUT).times(2)).showProgressBar(Boolean.FALSE);
        verify(mDelernMainView, timeout(TIMEOUT)).noDecksMessage(Boolean.TRUE);
    }

    @Test
    public void deleteDeckWithoutListener() {
        mUser.save().blockingAwait();
        Deck newDeck = new Deck(mUser);
        newDeck.setName("test");
        newDeck.setDeckType(DeckType.BASIC.name());
        newDeck.setAccepted(true);
        newDeck.create().blockingAwait();
        mPresenter.onCreate(mUser);
        mPresenter.onStart();
        verify(mDelernMainView, timeout(TIMEOUT)).showProgressBar(Boolean.FALSE);
        verify(mDelernMainView, timeout(TIMEOUT)).noDecksMessage(Boolean.FALSE);
        mPresenter.onStop();
        mPresenter.deleteDeck(newDeck);
        verifyNoMoreInteractions(mDelernMainView);
    }

    @Test
    public void renameDeck() {
        mUser.save().blockingAwait();
        Deck deck = new Deck(mUser);
        deck.setName("test");
        deck.setDeckType(DeckType.BASIC.name());
        deck.setAccepted(true);
        deck.create().blockingAwait();

        mPresenter.onCreate(mUser);
        mPresenter.onStart();
        verify(mDelernMainView, timeout(TIMEOUT)).noDecksMessage(Boolean.FALSE);
        mPresenter.renameDeck(deck, "newTest");

        List<Deck> deckList = mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                .blockingFirst();
        assertEquals("newTest", deckList.get(0).getName());
    }

    @Test
    public void changeDeckType() {
        mUser.save().blockingAwait();
        Deck deck = new Deck(mUser);
        deck.setName("test");
        deck.setDeckType(DeckType.BASIC.name());
        deck.setAccepted(true);
        deck.create().blockingAwait();

        mPresenter.onCreate(mUser);
        mPresenter.onStart();
        verify(mDelernMainView, timeout(TIMEOUT)).noDecksMessage(Boolean.FALSE);
        mPresenter.changeDeckType(deck, DeckType.SWISS);

        List<Deck> deckList = mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                .blockingFirst();
        assertEquals(DeckType.SWISS.name(), deckList.get(0).getDeckType());
    }
}
