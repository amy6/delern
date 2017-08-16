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

import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.User;
import org.dasfoo.delern.test.FirebaseServerUnitTest;
import org.dasfoo.delern.views.IAddEditCardView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class AddEditCardActivityPresenterTest extends FirebaseServerUnitTest {

    private static int TIMEOUT = 5000;

    @Mock
    private IAddEditCardView mAddEditCardView;

    private AddEditCardActivityPresenter mPresenter;
    private Deck mDeck;

    @Before
    public void setupParamPresenter() throws Exception {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mPresenter = new AddEditCardActivityPresenter(mAddEditCardView);
        User mUser = signIn();

        //Create user and deck for testing
        mUser.save().blockingAwait();
        mDeck = new Deck(mUser);
        mDeck.setName("test");
        mDeck.setAccepted(true);
        mDeck.create().blockingAwait();
    }

    @Test
    public void onCreateForNewCard() {
        Card card = new Card(mDeck);
        mPresenter.onCreate(card);
        verify(mAddEditCardView).initForAdd();
    }

    @Test
    public void onCreateForExistingCard() {
        Card card = new Card(mDeck);
        card.setKey("exist");
        card.setFront("front");
        card.setBack("back");
        mPresenter.onCreate(card);
        verify(mAddEditCardView).initForUpdate("front", "back");
    }

    @Test
    public void addCard() {
        Card card = new Card(mDeck);
        mPresenter.onCreate(card);
        verify(mAddEditCardView).initForAdd();
        mPresenter.onAddUpdate("front", "back");
        verify(mAddEditCardView, timeout(TIMEOUT)).cardAdded();
    }

    @Test
    public void updateCard() {
        Card card = new Card(mDeck);
        card.setFront("to_update_front");
        card.setBack("to_update_back");
        card.save().blockingAwait();
        card = mDeck.fetchChildren(mDeck.getChildReference(Card.class), Card.class)
                .firstOrError().blockingGet().get(0);

        mPresenter.onCreate(card);
        verify(mAddEditCardView).initForUpdate("to_update_front", "to_update_back");
        mPresenter.onAddUpdate("front", "back");
        verify(mAddEditCardView, timeout(TIMEOUT)).cardUpdated();
    }

}
