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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AddEditCardActivityPresenterTest extends FirebaseServerUnitTest {

    private static int TIMEOUT = 5000;

    private Deck mDeck;
    private Card mCard;

    @Before
    public void setupParamPresenter() throws Exception {
        User mUser = signIn();
        //Create user and deck for testing
        mUser.save().blockingAwait();
        mDeck = new Deck(mUser);
        mDeck.setName("test");
        mDeck.setAccepted(true);
        mDeck.create().blockingAwait();
        mCard = new Card(mDeck);
    }

    @Test
    public void onCreateForNewCard() {
        IAddEditCardView iAddEditCardView = mock(IAddEditCardView.class);
        AddEditCardActivityPresenter presenter =
                new AddEditCardActivityPresenter(iAddEditCardView, mCard);
        presenter.onCreate();
        verify(iAddEditCardView).initForAdd();
    }

    @Test
    public void onCreateForExistingCard() {
        mCard.setKey("exist");
        mCard.setFront("front");
        mCard.setBack("back");
        IAddEditCardView iAddEditCardView = mock(IAddEditCardView.class);
        AddEditCardActivityPresenter presenter =
                new AddEditCardActivityPresenter(iAddEditCardView, mCard);
        presenter.onCreate();
        verify(iAddEditCardView).initForUpdate("front", "back");
    }

    @Test
    public void addCard() {
        // inject the mocks with new card.
        IAddEditCardView iAddEditCardView = mock(IAddEditCardView.class);
        AddEditCardActivityPresenter presenter =
                new AddEditCardActivityPresenter(iAddEditCardView, mCard);
        presenter.onCreate();
        verify(iAddEditCardView).initForAdd();
        presenter.onAddUpdate("front", "back");
        verify(iAddEditCardView, timeout(TIMEOUT)).cardAdded();
    }

    @Test
    public void addReversedCard() {
        // inject the mocks with new card.
        IAddEditCardView iAddEditCardView = mock(IAddEditCardView.class);
        when(iAddEditCardView.addReversedCard()).then(invocation -> true);
        AddEditCardActivityPresenter presenter =
                new AddEditCardActivityPresenter(iAddEditCardView, mCard);
        presenter.onCreate();
        verify(iAddEditCardView).initForAdd();
        presenter.onAddUpdate("front", "back");
        verify(iAddEditCardView, timeout(TIMEOUT).times(2)).cardAdded();
    }

    @Test
    public void updateCard() {
        mCard.setFront("to_update_front");
        mCard.setBack("to_update_back");
        mCard.save().blockingAwait();
        Card fetchedCard = mDeck.fetchChildren(mDeck.getChildReference(Card.class), Card.class)
                .firstOrError().blockingGet().get(0);
        // It is needed to inject Presenter with one mock and one real object.
        // By using @Spy it throws NullPointerException in Deck.getChildReference
        // because @Spy creates object instance of Card$$EnhancerByMockitoWithCGLIB$$5b16c521.
        // We need exactly Card.class
        IAddEditCardView iAddEditCardView = mock(IAddEditCardView.class);
        AddEditCardActivityPresenter presenter =
                new AddEditCardActivityPresenter(iAddEditCardView, fetchedCard);
        presenter.onCreate();
        verify(iAddEditCardView).initForUpdate("to_update_front", "to_update_back");
        presenter.onAddUpdate("front", "back");
        verify(iAddEditCardView, timeout(TIMEOUT)).cardUpdated();
    }

}
