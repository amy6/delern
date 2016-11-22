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
    private OnCardViewHolderClick onViewClick;

    public CardViewHolder(View itemView) {
        super(itemView);
        mFrontTextView = (TextView) itemView.findViewById(R.id.front_textview);
        mBackTextView = (TextView) itemView.findViewById(R.id.back_textview);
        LinearLayout linearLayout = (LinearLayout) itemView.findViewById(R.id.card_edit_click);
        linearLayout.setOnClickListener(this);
    }

    public TextView getmFrontTextView() {
        return mFrontTextView;
    }

    public void setmFrontTextView(TextView mFrontTextView) {
        this.mFrontTextView = mFrontTextView;
    }

    public TextView getmBackTextView() {
        return mBackTextView;
    }

    public void setmBackTextView(TextView mBackTextView) {
        this.mBackTextView = mBackTextView;
    }

    public void setOnViewClick(OnCardViewHolderClick onViewClick) {
        this.onViewClick = onViewClick;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.card_edit_click) {
            onViewClick.onCardClick(getAdapterPosition());
        }
    }
}
