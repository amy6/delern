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


import org.dasfoo.delern.learncards.ILearningCardsView;
import org.dasfoo.delern.learncards.LearningCardsActivityPresenter;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.User;
import org.dasfoo.delern.test.FirebaseServerRule;
import org.dasfoo.delern.util.GrammaticalGenderSpecifier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LearningCardsActivityPresenterTest {

    private static final int TIMEOUT = 5000;
    private static final String FRONT_SIDE_CARD = "frontSide";
    private static final String BACK_SIDE_CARD = "backSide";

    @Rule
    public final FirebaseServerRule mFirebaseServer = new FirebaseServerRule();

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
        User user = mFirebaseServer.signIn();
        mDeck = new Deck(user);

        //Create user, deck and card for testing
        user.save().blockingAwait();

        mDeck.setName("CreateCard");
        mDeck.setAccepted(true);
        mDeck.create().blockingAwait();

        Card newCard = new Card(mDeck);
        newCard.setFront(FRONT_SIDE_CARD);
        newCard.setBack(BACK_SIDE_CARD);
        newCard.create().blockingAwait();
    }

    @Test
    public void startLearning() {
        mPresenter.onCreate(mDeck);
        mPresenter.onStart();
        verify(mLearningCardView, timeout(TIMEOUT)).showFrontSide(FRONT_SIDE_CARD, false,
                GrammaticalGenderSpecifier.Gender.NO_GENDER);
    }

    @Test
    public void backSideWasShown() {
        when(mLearningCardView.backSideIsShown()).thenReturn(true);
        mPresenter.onCreate(mDeck);
        mPresenter.onStart();
        verify(mLearningCardView, timeout(TIMEOUT)).showFrontSide(FRONT_SIDE_CARD, false,
                GrammaticalGenderSpecifier.Gender.NO_GENDER);
        verify(mLearningCardView, timeout(TIMEOUT)).showBackSide(BACK_SIDE_CARD, false);
    }

    @Test
    public void cardIsFlipped() {
        mPresenter.onCreate(mDeck);
        mPresenter.onStart();
        verify(mLearningCardView, timeout(TIMEOUT)).showFrontSide(FRONT_SIDE_CARD, false,
                GrammaticalGenderSpecifier.Gender.NO_GENDER);
        mPresenter.flipCard();
        verify(mLearningCardView).showBackSide(BACK_SIDE_CARD, false);
    }

    @Test
    public void finishLearningByUserKnowCard() {
        mPresenter.onCreate(mDeck);
        mPresenter.onStart();
        verify(mLearningCardView, timeout(TIMEOUT)).showFrontSide(FRONT_SIDE_CARD, false,
                GrammaticalGenderSpecifier.Gender.NO_GENDER);
        mPresenter.flipCard();
        verify(mLearningCardView).showBackSide(BACK_SIDE_CARD, false);
        mPresenter.userKnowCard();
        verify(mLearningCardView, timeout(TIMEOUT)).finishLearning();
    }

    @Test
    public void finishLearningByUserDoNotKnowCard() {
        mPresenter.onCreate(mDeck);
        mPresenter.onStart();
        verify(mLearningCardView, timeout(TIMEOUT)).showFrontSide(FRONT_SIDE_CARD, false,
                GrammaticalGenderSpecifier.Gender.NO_GENDER);
        mPresenter.flipCard();
        verify(mLearningCardView).showBackSide(BACK_SIDE_CARD, false);
        mPresenter.userDoNotKnowCard();
        verify(mLearningCardView, timeout(TIMEOUT)).finishLearning();
    }

    @Test
    public void startEditCardCallbackCheck() {
        mPresenter.onCreate(mDeck);
        mPresenter.onStart();
        verify(mLearningCardView, timeout(TIMEOUT)).showFrontSide(FRONT_SIDE_CARD, false,
                GrammaticalGenderSpecifier.Gender.NO_GENDER);
        mPresenter.startEditCard();
        ArgumentCaptor<Card> argument = ArgumentCaptor.forClass(Card.class);
        verify(mLearningCardView).startEditCardActivity(argument.capture());
        assertEquals(argument.getValue().getFront(), FRONT_SIDE_CARD);
        assertEquals(argument.getValue().getBack(), BACK_SIDE_CARD);
    }

    @Test
    public void finishLearningByDeletingCard() {
        mPresenter.onCreate(mDeck);
        mPresenter.onStart();
        verify(mLearningCardView, timeout(TIMEOUT)).showFrontSide(FRONT_SIDE_CARD, false,
                GrammaticalGenderSpecifier.Gender.NO_GENDER);
        mPresenter.flipCard();
        verify(mLearningCardView).showBackSide(BACK_SIDE_CARD, false);
        mPresenter.delete();
        verify(mLearningCardView, timeout(TIMEOUT)).finishLearning();
    }
}
