package org.dasfoo.delern.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.dasfoo.delern.R;
import org.dasfoo.delern.handlers.OnCardViewHolderClick;

/**
 * Created by katarina on 11/14/16.
 * Describes an item view and metadata about its place within the RecyclerView.
 */
public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final TextView mFrontTextView;
    private final TextView mBackTextView;
    private OnCardViewHolderClick mOnViewClick;

    /**
     * Constructor. It initializes variable that describe how to place card.
     *
     * @param itemView item view.
     */
    public CardViewHolder(final View itemView) {
        super(itemView);
        mFrontTextView = (TextView) itemView.findViewById(R.id.front_textview);
        mBackTextView = (TextView) itemView.findViewById(R.id.back_textview);
        LinearLayout linearLayout = (LinearLayout) itemView.findViewById(R.id.card_edit_click);
        linearLayout.setOnClickListener(this);
    }

    /**
     * Getter for front side of card. It references to R.id.front_textview.
     *
     * @return reference to R.id.front_textview.
     */
    public TextView getFrontTextView() {
        return mFrontTextView;
    }

    /**
     * Getter for back side of card. It references to R.id.back_textview.
     *
     * @return reference to R.id.back_textview.
     */
    public TextView getBackTextView() {
        return mBackTextView;
    }

    /**
     * Setter for mOnViewClick. It listeners clicks on cards.
     *
     * @param onViewClick onCardViewHolderClick.
     */
    public void setOnViewClick(final OnCardViewHolderClick onViewClick) {
        this.mOnViewClick = onViewClick;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.card_edit_click) {
            mOnViewClick.onCardClick(getAdapterPosition());
        }
    }
}
