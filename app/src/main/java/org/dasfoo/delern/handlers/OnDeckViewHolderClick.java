package org.dasfoo.delern.handlers;

import org.dasfoo.delern.models.DeckType;

/**
 * Created by katarina on 10/14/16.
 * Manages clicks on RecyclerView
 */

public interface OnDeckViewHolderClick {

    /**
     * Manages text click on deck.
     *
     * @param position position of the clicked element in the list
     */
    void doOnTextViewClick(int position);

    /**
     * "Rename" menu item of a deck.
     *
     * @param position position of the element in the list
     */
    void doOnRenameMenuClick(int position);

    /**
     * "Edit" menu item of a deck.
     *
     * @param position position of the element in the list
     */
    void doOnEditMenuClick(int position);

    /**
     * "Deletes" menu of deck.
     *
     * @param position position of the element in the list
     */
    void doOnDeleteMenuClick(int position);


    /**
     * Sets type of deck.
     *
     * @param position position of the element in the list
     * @param deckType type of deck
     */
    void doOnDeckTypeClick(int position, DeckType deckType);
}
