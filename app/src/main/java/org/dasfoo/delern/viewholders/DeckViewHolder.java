package org.dasfoo.delern.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.dasfoo.delern.R;
import org.dasfoo.delern.callbacks.OnDeckViewHolderClick;

/**
 * Created by Katarina Sheremet on 9/22/16 1:11 AM.
 * Provide a reference to the views for each data item
 * Complex data items may need more than one view per item, and
 * you provide access to all the views for a data item in a view holder
 */

public class DeckViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        PopupMenu.OnMenuItemClickListener {

    private static final String TAG = DeckViewHolder.class.getSimpleName();

    private TextView mDesktopTextView;
    private Button mAddCardButton;
    private ImageView popupMenuImageView;
    private OnDeckViewHolderClick onViewClick;
    private Context context;


    public DeckViewHolder(View v) {
        super(v);
        mDesktopTextView = (TextView) itemView.findViewById(R.id.desktop_text_view);
        mDesktopTextView.setOnClickListener(this);
        mAddCardButton = (Button) itemView.findViewById(R.id.add_card_button);
        mAddCardButton.setOnClickListener(this);
        popupMenuImageView = (ImageView) itemView.findViewById(R.id.deck_popup_menu);
        popupMenuImageView.setOnClickListener(this);
    }

    public TextView getmDesktopTextView() {
        return mDesktopTextView;
    }

    public void setOnViewClick(OnDeckViewHolderClick onViewClick) {
        this.onViewClick = onViewClick;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_card_button) {
            Log.v(TAG, "Button was pressed" + getAdapterPosition());
            onViewClick.doOnAddCardButtonClick(getAdapterPosition());

        }

        if (v.getId() == R.id.desktop_text_view) {
            Log.v(TAG, "wow, it is desktop" + getAdapterPosition());
            onViewClick.doOnTextViewClick(getAdapterPosition());

        }

        if (v.getId() == R.id.deck_popup_menu) {
            showPopup(v);
        }

    }

    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.deck_menu, popup.getMenu());
        popup.show();
    }


    /**
     * This method will be invoked when a menu item is clicked if the item
     * itself did not already handle the event.
     *
     * @param item the menu item that was clicked
     * @return {@code true} if the event was handled, {@code false}
     * otherwise
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rename_deck_menu:
                onViewClick.doOnRenameMenuClick(getAdapterPosition());
                return true;
            case R.id.edit_deck_menu:
                onViewClick.doOnEditMenuClick(getAdapterPosition());
                return true;
            case R.id.delete_deck_menu:
                onViewClick.doOnDeleteMenuClick(getAdapterPosition());
                return true;
            default:
                Log.v(TAG, "Menu Item is not implemented yet");
                return false;
        }
    }
}
