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
import android.widget.Toast;

import org.dasfoo.delern.R;
import org.dasfoo.delern.handlers.OnDeckViewHolderClick;
import org.dasfoo.delern.models.DeckType;
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
    private static final String NULL_CARDS = "0";

    private final TextView mDeckTextView;
    private final TextView mCountToLearnTextView;
    private OnDeckViewHolderClick mOnViewClick;
    private Context mContext;
    private String mCheckedDeckType;

    /**
     * Constructor. It initializes variable that describe how to place deck.
     *
     * @param v item view
     */
    public DeckViewHolder(final View v) {
        super(v);
        mDeckTextView = (TextView) itemView.findViewById(R.id.deck_text_view);
        mCountToLearnTextView = (TextView) itemView.findViewById(R.id.count_to_learn_textview);
        // Set default mCountToLearnTextView to N/A
        mCountToLearnTextView.setText(R.string.n_a);

        mDeckTextView.setOnClickListener(this);
        ImageView popupMenuImageView = (ImageView) itemView.findViewById(R.id.deck_popup_menu);
        popupMenuImageView.setOnClickListener(this);
    }

    /**
     * Getter to reference to R.id.deck_text_view.
     *
     * @return textview of deck
     */
    public TextView getDeckTextView() {
        return mDeckTextView;
    }

    /**
     * Getter for number of cards to learn. mCountToLearnTextView references to
     * R.id.count_to_learn_textview.
     *
     * @return textview with number of cards to learn
     */
    public TextView getCountToLearnTextView() {
        return mCountToLearnTextView;
    }

    /**
     * Setter for mOnViewClick. It listeners clicks on deck name and
     * popup menu.
     *
     * @param onViewClick onDeckViewClickListener
     */
    public void setOnViewClick(final OnDeckViewHolderClick onViewClick) {
        this.mOnViewClick = onViewClick;
    }

    /**
     * Setter for context.
     * Context is needed for creating popup menu for every deck.
     *
     * @param context context
     */
    public void setContext(final Context context) {
        this.mContext = context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.deck_text_view) {
            // if number of cards is 0, show message to user
            String cardCount = mCountToLearnTextView.getText().toString();
            if (NULL_CARDS.equals(cardCount)) {
                Toast.makeText(mContext, R.string.no_card_message, Toast.LENGTH_SHORT).show();
            } else {
                mOnViewClick.doOnTextViewClick(getAdapterPosition());
            }
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
        setDeckType(popup);
    }

    /**
     * Sets current deck type in popup menu and make disable this deck type.
     * Disabled menuItem doesn't allow to write in Firebase the same deck type
     * if user touch it.
     *
     * @param popup popup menu
     */
    private void setDeckType(final PopupMenu popup) {
        MenuItem menuItem = popup.getMenu().findItem(R.id.basic_type);
        if (DeckType.SWISS.name().equalsIgnoreCase(mCheckedDeckType)) {
            menuItem = popup.getMenu().findItem(R.id.swissgerman_type);
        }
        if (DeckType.GERMAN.name().equalsIgnoreCase(mCheckedDeckType)) {
            menuItem = popup.getMenu().findItem(R.id.german_type);
        }
        menuItem.setChecked(true);
        menuItem.setEnabled(false);
    }

    /**
     * {@inheritDoc}
     */
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
            case R.id.basic_type:
                mOnViewClick.doOnDeckTypeClick(getAdapterPosition(), DeckType.BASIC);
                return true;
            case R.id.german_type:
                mOnViewClick.doOnDeckTypeClick(getAdapterPosition(), DeckType.GERMAN);
                return true;
            case R.id.swissgerman_type:
                Log.v(TAG, "Swiss deck");
                mOnViewClick.doOnDeckTypeClick(getAdapterPosition(), DeckType.SWISS);
                return true;
            default:
                Log.v(TAG, "Menu Item is not implemented yet");
                return false;
        }
    }

    /**
     * Setter for deck type.
     *
     * @param checkedDeckType deck type
     */
    public void setDeckCardType(final String checkedDeckType) {
        this.mCheckedDeckType = checkedDeckType;
    }
}
