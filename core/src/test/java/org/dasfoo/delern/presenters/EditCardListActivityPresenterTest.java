package org.dasfoo.delern.presenters;

import org.dasfoo.delern.listcards.EditCardListActivityPresenter;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.User;
import org.dasfoo.delern.test.FirebaseServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by katarina on 8/17/17.
 */

public class EditCardListActivityPresenterTest {

    private EditCardListActivityPresenter mPresenter;
    private Deck mDeck;

    @Rule
    public final FirebaseServerRule mFirebaseServer = new FirebaseServerRule();

    @Before
    public void setParam() throws Exception {
        mPresenter = new EditCardListActivityPresenter();
        User user = mFirebaseServer.signIn();
        user.save().blockingAwait();
        mDeck = new Deck(user);
        mDeck.setAccepted(true);
        mDeck.setName("test");
        mDeck.create().blockingAwait();
        mPresenter.onCreate(mDeck);

    }

    @Test
    public void getDeckTest() {
        assertEquals(mDeck, mPresenter.getDeck());
    }

    @Test
    public void getQueryTest() {
        assertEquals(mPresenter.getQuery(), mDeck.getChildReference(Card.class));
    }
}
