package org.dasfoo.delern.sharedeck;

import com.google.firebase.database.Query;

import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by katarina on 10/25/17.
 */

public class ShareDeckActivityPresenter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShareDeckActivityPresenter.class);
    private final IShareDeckView mView;
    private final Deck mDeck;

    public ShareDeckActivityPresenter(final IShareDeckView view, final Deck deck) {
        this.mView = view;
        this.mDeck = deck;
    }

    public Deck getDeck() {
        return mDeck;
    }

    public int getDefaultUserAccess(final DeckAccess deckAccess) {
        if ("read".equals(deckAccess.getAccess())) {
            return 1;
        }
        return 0;
    }

    public Query getReference() {
        return mDeck.getChildReference(DeckAccess.class).orderByChild("access");
    }
}
