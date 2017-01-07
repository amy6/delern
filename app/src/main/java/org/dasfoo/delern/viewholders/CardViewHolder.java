package org.dasfoo.delern.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.dasfoo.delern.R;
import org.dasfoo.delern.callbacks.OnCardViewHolderClick;

/**
 * Created by katarina on 11/14/16.
 */
public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mFrontTextView;
    private TextView mBackTextView;
    private OnCardViewHolderClick mOnViewClick;

    public CardViewHolder(final View itemView) {
        super(itemView);
        mFrontTextView = (TextView) itemView.findViewById(R.id.front_textview);
        mBackTextView = (TextView) itemView.findViewById(R.id.back_textview);
        LinearLayout linearLayout = (LinearLayout) itemView.findViewById(R.id.card_edit_click);
        linearLayout.setOnClickListener(this);
    }

    public TextView getFrontTextView() {
        return mFrontTextView;
    }

    public void setmFrontTextView(final TextView frontTextView) {
        this.mFrontTextView = frontTextView;
    }

    public TextView getBackTextView() {
        return mBackTextView;
    }

    public void setBackTextView(final TextView mBackTextView) {
        this.mBackTextView = mBackTextView;
    }

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
