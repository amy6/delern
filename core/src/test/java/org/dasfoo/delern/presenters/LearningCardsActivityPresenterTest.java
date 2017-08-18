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
import org.dasfoo.delern.presenters.helpers.GrammaticalGenderSpecifier;
import org.dasfoo.delern.test.FirebaseServerUnitTest;
import org.dasfoo.delern.views.ILearningCardsView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class LearningCardsActivityPresenterTest extends FirebaseServerUnitTest {

    private static final int TIMEOUT = 5000;
    private static final String FRONT_SIDE_CARD = "frontSide";
    private static final String BACK_SIDE_CARD = "backSide";


    @Mock
    private ILearningCardsView mLearningCardView;
    @InjectMocks
    private LearningCardsActivityPresenter mPresenter;

    private Deck mDeck;

    @Before
    public void setupParamPresenter() throws Exception {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Create data for testing
        User user = signIn();
        mDeck = new Deck(user);

        //Create user, deck and card for testing
        user.save().blockingAwait();

        mDeck.setName("CreateCard");
        mDeck.setAccepted(true);
        mDeck.create().blockingAwait();

        Card newCard = new Card(mDeck);
        newCard.create(FRONT_SIDE_CARD, BACK_SIDE_CARD).blockingAwait();
    }

    @Test
    public void checkStartLearning() {
        mPresenter.onCreate(mDeck);
        mPresenter.onStart();
        verify(mLearningCardView, timeout(TIMEOUT)).showFrontSide(FRONT_SIDE_CARD);
    }

    @Test
    public void cardIsFlipped() {
        mPresenter.onCreate(mDeck);
        mPresenter.onStart();
        verify(mLearningCardView, timeout(TIMEOUT)).showFrontSide(FRONT_SIDE_CARD);
        mPresenter.flipCard();
        verify(mLearningCardView).showBackSide(BACK_SIDE_CARD);
    }

    @Test
    public void specifyGender() {
        mPresenter.onCreate(mDeck);
        mPresenter.onStart();
        verify(mLearningCardView, timeout(TIMEOUT)).showFrontSide(FRONT_SIDE_CARD);
        GrammaticalGenderSpecifier.Gender gender = mPresenter.specifyContentGender();
        assertEquals(gender, GrammaticalGenderSpecifier.Gender.NO_GENDER);
    }

    @Test
    public void startEditCardCallbackCheck() {
        mPresenter.onCreate(mDeck);
        mPresenter.onStart();
        verify(mLearningCardView, timeout(TIMEOUT)).showFrontSide(FRONT_SIDE_CARD);
        mPresenter.startEditCard();
        ArgumentCaptor<Card> argument = ArgumentCaptor.forClass(Card.class);
        verify(mLearningCardView).startEditCardActivity(argument.capture());
        assertEquals(argument.getValue().getFront(), FRONT_SIDE_CARD);
        assertEquals(argument.getValue().getBack(), BACK_SIDE_CARD);
    }
}
