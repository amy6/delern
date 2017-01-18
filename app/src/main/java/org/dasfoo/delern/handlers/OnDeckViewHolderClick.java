package org.dasfoo.delern.handlers;

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
     * Sets cards type for the deck.
     *
     * @param position position of the element in the list
     */
    void doOnCardsTypeClick(int position);
}
