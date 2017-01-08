package org.dasfoo.delern.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.dasfoo.delern.R;
import org.dasfoo.delern.handlers.OnDeckViewHolderClick;
import org.dasfoo.delern.util.LogUtil;

/**
 * Created by Katarina Sheremet on 9/22/16 1:11 AM.
 * Provide a reference to the views for each data item
 * Complex data items may need more than one view per item, and
 * you provide access to all the views for a data item in a view holder
 */

public class DeckViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        PopupMenu.OnMenuItemClickListener {

    private static final String TAG = LogUtil.tagFor(DeckViewHolder.class);

    private final TextView mDesktopTextView;
    private final TextView mCountToLearnTextView;
    private OnDeckViewHolderClick mOnViewClick;
    private Context mContext;


    public DeckViewHolder(final View v) {
        super(v);
        mDesktopTextView = (TextView) itemView.findViewById(R.id.desktop_text_view);
        mCountToLearnTextView = (TextView) itemView.findViewById(R.id.count_to_learn_textview);

        mDesktopTextView.setOnClickListener(this);
        ImageView popupMenuImageView = (ImageView) itemView.findViewById(R.id.deck_popup_menu);
        popupMenuImageView.setOnClickListener(this);
    }

    public TextView getDesktopTextView() {
        return mDesktopTextView;
    }

    public TextView getCountToLearnTextView() {
        return mCountToLearnTextView;
    }

    public void setOnViewClick(final OnDeckViewHolderClick onViewClick) {
        this.mOnViewClick = onViewClick;
    }

    public void setContext(final Context context) {
        this.mContext = context;
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.desktop_text_view) {
            mOnViewClick.doOnTextViewClick(getAdapterPosition());
        }
        if (v.getId() == R.id.deck_popup_menu) {
            showPopup(v);
        }
    }

    private void showPopup(final View v) {
        PopupMenu popup = new PopupMenu(mContext, v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.deck_menu, popup.getMenu());
        popup.show();
    }

    /** {@inheritDoc} */
    @Override
    public boolean onMenuItemClick(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rename_deck_menu:
                mOnViewClick.doOnRenameMenuClick(getAdapterPosition());
                return true;
            case R.id.edit_deck_menu:
                mOnViewClick.doOnEditMenuClick(getAdapterPosition());
                return true;
            case R.id.delete_deck_menu:
                mOnViewClick.doOnDeleteMenuClick(getAdapterPosition());
                return true;
            default:
                Log.v(TAG, "Menu Item is not implemented yet");
                return false;
        }
    }
}
