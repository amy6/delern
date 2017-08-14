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
import org.dasfoo.delern.test.FirebaseServerUnitTest;
import org.dasfoo.delern.views.IDelernMainView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class DelernMainActivityPresenterTest extends FirebaseServerUnitTest {

    private final static int TIMEOUT = 5000;

    @Mock
    private IDelernMainView mDelernMainView;

    private DelernMainActivityPresenter mPresenter;
    private User mUser;

    @Before
    public void setupParamPresenter() throws Exception {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mPresenter = new DelernMainActivityPresenter(mDelernMainView);
        mUser = signIn();
    }

    @Test
    //@SuppressWarnings("CheckReturnValue")
    public void createDeck() throws Exception {
        mUser.save().blockingAwait();
        mPresenter.onCreate(mUser);
        mPresenter.createNewDeck("test");
        verify(mDelernMainView, timeout(TIMEOUT)).addCardsToDeck(any(Deck.class));
    }

    @Test
    public void checkSignInCallbackOnNullUser() {
        mPresenter.onCreate(null);
        verify(mDelernMainView).signIn();
    }

    @Test
    public void checkNoDecksUserMessage() {
        mUser.save().blockingAwait();
        mPresenter.onCreate(mUser);
        mPresenter.onStart();
        verify(mDelernMainView, timeout(TIMEOUT)).showProgressBar(Boolean.FALSE);
        verify(mDelernMainView, timeout(TIMEOUT)).noDecksMessage(Boolean.TRUE);
    }

    //TODO(ksheremet) verify called asynchronously.
    /*@Test
    public void checkUserHasDecks() {
        mUser.save().blockingAwait();
        mPresenter.onCreate(mUser);
        mPresenter.onStart();
        verify(mDelernMainView).showProgressBar(Boolean.FALSE);
        verify(mDelernMainView).noDecksMessage(Boolean.TRUE);
        mPresenter.createNewDeck("test");
        verify(mDelernMainView, timeout(TIMEOUT)).addCardsToDeck(any(Deck.class));
        verify(mDelernMainView).noDecksMessage(Boolean.FALSE);
    }*/

    @Test
    public void getUserInfo() {
        mUser.save().blockingAwait();
        mPresenter.onCreate(mUser);
        mPresenter.getUserInfo();
        verify(mDelernMainView, timeout(TIMEOUT)).updateUserProfileInfo(any(User.class));
    }

    @Test
    public void getUser() {
        mPresenter.onCreate(mUser);
        User user = mPresenter.getUser();
        assertEquals(mUser, user);
    }
}
