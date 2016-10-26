package org.dasfoo.delern.viewholders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.dasfoo.delern.R;
import org.dasfoo.delern.callbacks.OnDeckViewHolderClick;

/**
  Created by Katarina Sheremet on 9/22/16 1:11 AM.
  Provide a reference to the views for each data item
  Complex data items may need more than one view per item, and
  you provide access to all the views for a data item in a view holder
 */

public class DeckViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final String TAG = DeckViewHolder.class.getSimpleName();

    private TextView mDesktopTextView;
    private Button mAddCardButton;
    private OnDeckViewHolderClick onViewClick;

    public DeckViewHolder(View v) {
        super(v);
        mDesktopTextView = (TextView) itemView.findViewById(R.id.desktop_text_view);
        mDesktopTextView.setOnClickListener(this);
        mAddCardButton = (Button) itemView.findViewById(R.id.add_card_button);
        mAddCardButton.setOnClickListener(this);
    }

    public TextView getmDesktopTextView() {
        return mDesktopTextView;
    }

    public void setmDesktopTextView(TextView mDesktopTextView) {
        this.mDesktopTextView = mDesktopTextView;
    }

    public void setOnViewClick(OnDeckViewHolderClick onViewClick) {
        this.onViewClick = onViewClick;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_card_button) {
            Log.v(TAG, "Button was pressed" + getAdapterPosition());
            onViewClick.doOnAddCardButtonClick(getAdapterPosition());

        }

        if (v.getId() == R.id.desktop_text_view){
            Log.v(TAG, "wow, it is desktop" + getAdapterPosition());
            onViewClick.doOnTextViewClick(getAdapterPosition());

        }

    }
}
