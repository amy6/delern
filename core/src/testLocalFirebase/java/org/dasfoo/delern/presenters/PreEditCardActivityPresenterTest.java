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
import org.dasfoo.delern.views.IPreEditCardView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class PreEditCardActivityPresenterTest extends FirebaseServerUnitTest {

    private static int TIMEOUT = 5000;

    @Mock
    private IPreEditCardView mPreEditCardView;

    private PreEditCardActivityPresenter mPresenter;
    private Card mCard;

    @Before
    public void setupParamPresenter() throws Exception {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mPresenter = new PreEditCardActivityPresenter(mPreEditCardView);
    }

    @Test
    public void checkShowCardCallback() throws Exception {
        String frontSide = "frontSide";
        String backSide = "backSide";
        User user = signIn();
        Deck deck = new Deck(user);

        //Create user, deck and card for testing
        user.save().blockingAwait();

        deck.setName("CreateCard");
        deck.setAccepted(true);
        deck.create().blockingAwait();

        Card newCard = new Card(deck);
        newCard.create(frontSide, backSide).blockingAwait();

        mCard = deck.fetchChildren(deck.getChildReference(Card.class), Card.class)
                .firstOrError().blockingGet().get(0);

        mPresenter.onCreate(mCard);
        mPresenter.onStart();
        verify(mPreEditCardView, timeout(TIMEOUT)).showCard(frontSide, backSide);
    }

    @Test
    public void checkEditCardActivity() {
        mPresenter.editCard();
        verify(mPreEditCardView).startEditCardActivity(eq(mCard));
    }
}
