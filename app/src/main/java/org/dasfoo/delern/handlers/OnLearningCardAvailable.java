package org.dasfoo.delern.handlers;

/**
 * Created by katarina on 3/1/17.
 */

public interface OnLearningCardAvailable {
    /**
     * Handler on new card available for learning.
     */
    void onNewCard();

    /**
     * Handler on no cards available for learning.
     */
    void onNoCards();
}
