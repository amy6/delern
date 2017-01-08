package org.dasfoo.delern.adapters;

import android.util.Log;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import org.dasfoo.delern.handlers.OnCardViewHolderClick;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.util.LogUtil;
import org.dasfoo.delern.viewholders.CardViewHolder;

/**
 * Created by katarina on 11/19/16.
 */

public class CardRecyclerViewAdapter extends FirebaseRecyclerAdapter<Card, CardViewHolder> {

    private static final String TAG = LogUtil.tagFor(CardRecyclerViewAdapter.class);

    private final OnCardViewHolderClick mOnCardViewHolderClick;

    /**
     * Create a new FirebaseRecyclerAdapter.
     *
     * @param builder inner class with all the properties
     */
    public CardRecyclerViewAdapter(final Builder builder) {
        super(builder.mNestedModelClass, builder.mNestedLayout, builder.mNestedViewHolderClass,
                builder.mNestedQuery);
        this.mOnCardViewHolderClick = builder.mNestedOnClickListener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void populateViewHolder(final CardViewHolder viewHolder, final Card card,
                                      final int position) {
        viewHolder.getFrontTextView().setText(card.getFront());
        viewHolder.getBackTextView().setText(card.getBack());
        viewHolder.setOnViewClick(mOnCardViewHolderClick);
    }

    /**
     * Builder class for easy creation of CardRecyclerViewAdapter.
     */
    public static class Builder {
        private final Class<Card> mNestedModelClass;
        private final int mNestedLayout;
        private final Class<CardViewHolder> mNestedViewHolderClass;
        private final Query mNestedQuery;
        private OnCardViewHolderClick mNestedOnClickListener;

        /**
         * Constructor with required parameters.
         *
         * @param nestedModelClass ViewAdapter model class
         * @param nestedLayout     ViewAdapter layout
         * @param nestedViewHolder ViewAdapter holder
         * @param nestedQuery      ViewAdapter query
         */
        public Builder(final Class<Card> nestedModelClass, final int nestedLayout,
                       final Class<CardViewHolder> nestedViewHolder, final Query nestedQuery) {
            this.mNestedModelClass = nestedModelClass;
            this.mNestedLayout = nestedLayout;
            this.mNestedViewHolderClass = nestedViewHolder;
            this.mNestedQuery = nestedQuery;
        }

        /**
         * Sets the onClick listener of this view.
         *
         * @param nestedOnClickListener callback
         * @return this
         */
        public Builder setOnClickListener(final OnCardViewHolderClick nestedOnClickListener) {
            this.mNestedOnClickListener = nestedOnClickListener;
            return this;
        }

        /**
         * Build a new instance based on the fields in this builder.
         *
         * @return ViewAdapter with all the necessary fields set
         * @throws InstantiationException if not all required fields are set
         */
        public CardRecyclerViewAdapter build() throws InstantiationException {
            if (this.mNestedOnClickListener == null) {
                Log.e(TAG, "Set OnClickListener");
                throw new InstantiationException("OnClickListener is required");
            }
            return new CardRecyclerViewAdapter(this);
        }
    }
}
