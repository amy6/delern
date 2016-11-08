package org.dasfoo.delern.callbacks;

/**
 * Created by katarina on 10/14/16.
 * Manages clicks on RecyclerView
 */

public interface OnDeckViewHolderClick {
    void doOnAddCardButtonClick(int position);
    void doOnTextViewClick(int position);
    void doOnRenameMenuClick(int position);
    void doOnEditMenuClick(int position);
    void doOnDeleteMenuClick(int position);
}
