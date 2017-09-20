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
import org.dasfoo.delern.models.User;
import org.dasfoo.delern.test.FirebaseServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class EditDeckActivityPresenterTest {

    private final static int TIMEOUT = 5000;

    @Rule
    public final FirebaseServerRule mFirebaseServer = new FirebaseServerRule();

    @Mock
    private IEditDeckView mEditDeckViev;
    @InjectMocks
    private EditDeckActivityPresenter mPresenter;

    private User mUser;

    @Before
    public void setupParamPresenter() throws Exception {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        mUser = mFirebaseServer.signIn();
    }

    /*@Test
    public void deleteDeckWithListener() {
        mUser.save().blockingAwait();
        Deck newDeck = new Deck(mUser);
        newDeck.setName("test");
        newDeck.setDeckType(DeckType.BASIC.name());
        newDeck.setAccepted(true);
        newDeck.create().blockingAwait();
        mPresenter.deleteDeck(newDeck);
        // TODO(ksheremet): Verify that activity closed
        verify(mDelernMainView, timeout(TIMEOUT).times(2)).showProgressBar(Boolean.FALSE);
        verify(mDelernMainView, timeout(TIMEOUT)).noDecksMessage(Boolean.TRUE);
    }*/
}
