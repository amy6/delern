package org.dasfoo.delern.adapters;

import android.util.Log;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import org.dasfoo.delern.callbacks.OnCardViewHolderClick;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.util.LogUtil;
import org.dasfoo.delern.viewholders.CardViewHolder;

/**
 * Created by katarina on 11/19/16.
 */

public class CardRecyclerViewAdapter extends FirebaseRecyclerAdapter<Card, CardViewHolder> {

    private static final String TAG = LogUtil.tagFor(CardRecyclerViewAdapter.class);

    private OnCardViewHolderClick onCardViewHolderClick;

    private CardRecyclerViewAdapter(Builder builder) {
        super(builder.nestedModelClass, builder.nestedLayout, builder.nestedViewHolderClass, builder.nestedQuery);
        this.onCardViewHolderClick = builder.nestedOnClickListener;
    }

    /**
     * Each time the data at the given Firebase location changes, this method will be called for each item that needs
     * to be displayed. The first two arguments correspond to the mLayout and mModelClass given to the constructor of
     * this class. The third argument is the item's position in the list.
     * <p>
     * Your implementation should populate the view using the data contained in the model.
     *
     * @param viewHolder The view to populate
     * @param card       The object containing the data used to populate the view
     * @param position   The position in the list of the view being populated
     */
    @Override
    protected void populateViewHolder(CardViewHolder viewHolder, Card card, int position) {
        viewHolder.getmFrontTextView().setText(card.getFront());
        viewHolder.getmBackTextView().setText(card.getBack());
        viewHolder.setOnViewClick(onCardViewHolderClick);
    }

    public static class Builder {
        private Class<Card> nestedModelClass;
        private int nestedLayout;
        private Class<CardViewHolder> nestedViewHolderClass;
        private Query nestedQuery;
        private OnCardViewHolderClick nestedOnClickListener;

        /**
         * Required parameters
         */
        public Builder(final Class<Card> nestedModelClass, final int nestedLayout,
                       final Class<CardViewHolder> nestedViewHolder, final Query nestedQuery) {
            this.nestedModelClass = nestedModelClass;
            this.nestedLayout = nestedLayout;
            this.nestedViewHolderClass = nestedViewHolder;
            this.nestedQuery = nestedQuery;
        }

        public Builder setOnClickListener(final OnCardViewHolderClick nestedOnClickListener) {
            this.nestedOnClickListener = nestedOnClickListener;
            return this;
        }

        public CardRecyclerViewAdapter build() throws InstantiationException {
            if (this.nestedOnClickListener == null) {
                Log.e(TAG, "Set OnClickListener");
                throw new InstantiationException("OnClickListener is required");
            }
            return new CardRecyclerViewAdapter(this);
        }
    }
}
