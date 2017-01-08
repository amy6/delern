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

    private final OnCardViewHolderClick mOnCardViewHolderClick;

    public CardRecyclerViewAdapter(final Builder builder) {
        super(builder.mNestedModelClass, builder.mNestedLayout, builder.mNestedViewHolderClass, builder.mNestedQuery);
        this.mOnCardViewHolderClick = builder.mNestedOnClickListener;
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
    protected void populateViewHolder(final CardViewHolder viewHolder, final Card card,
                                      final int position) {
        viewHolder.getFrontTextView().setText(card.getFront());
        viewHolder.getBackTextView().setText(card.getBack());
        viewHolder.setOnViewClick(mOnCardViewHolderClick);
    }

    public static class Builder {
        private final Class<Card> mNestedModelClass;
        private final int mNestedLayout;
        private final Class<CardViewHolder> mNestedViewHolderClass;
        private final Query mNestedQuery;
        private OnCardViewHolderClick mNestedOnClickListener;

        /**
         * Required parameters
         */
        public Builder(final Class<Card> nestedModelClass, final int nestedLayout,
                       final Class<CardViewHolder> nestedViewHolder, final Query nestedQuery) {
            this.mNestedModelClass = nestedModelClass;
            this.mNestedLayout = nestedLayout;
            this.mNestedViewHolderClass = nestedViewHolder;
            this.mNestedQuery = nestedQuery;
        }

        public Builder setOnClickListener(final OnCardViewHolderClick nestedOnClickListener) {
            this.mNestedOnClickListener = nestedOnClickListener;
            return this;
        }

        public CardRecyclerViewAdapter build() throws InstantiationException {
            if (this.mNestedOnClickListener == null) {
                Log.e(TAG, "Set OnClickListener");
                throw new InstantiationException("OnClickListener is required");
            }
            return new CardRecyclerViewAdapter(this);
        }
    }
}
