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

import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.User;
import org.dasfoo.delern.presenters.interfaces.IAddUpdatePresenter;
import org.dasfoo.delern.test.FirebaseServerUnitTest;
import org.dasfoo.delern.views.IAddEditCardView;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Tests AddCardActivityPresenter.
 */
public class AddCardActivityPresenterTest extends FirebaseServerUnitTest {

    private static int TIMEOUT = 5000;

    private IAddUpdatePresenter mPresenter;
    private Deck mDeck;
    private IAddEditCardView mAddEditCardView;

    @Before
    public void setupParamPresenter() throws Exception {
        User mUser = signIn();
        //Create user and deck for testing
        mUser.save().blockingAwait();
        mDeck = new Deck(mUser);
        mDeck.setName("test");
        mDeck.setAccepted(true);
        mDeck.create().blockingAwait();
        mAddEditCardView = mock(IAddEditCardView.class);
        mPresenter = new AddCardActivityPresenter(mAddEditCardView, mDeck);
    }

    @Test
    public void addCard() {
        mPresenter.onAddUpdate("front", "back");
        verify(mAddEditCardView, timeout(TIMEOUT)).cardAdded();
    }

    @Test
    public void addReversedCard() {
        when(mAddEditCardView.addReversedCard()).then(invocation -> true);
        IAddUpdatePresenter presenter =
                new AddCardActivityPresenter(mAddEditCardView, mDeck);
        presenter.onAddUpdate("front", "back");
        verify(mAddEditCardView, timeout(TIMEOUT).times(2)).cardAdded();
    }
}
