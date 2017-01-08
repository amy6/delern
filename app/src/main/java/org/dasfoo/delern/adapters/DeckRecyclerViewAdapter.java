package org.dasfoo.delern.adapters;

import android.content.Context;
import android.util.Log;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.dasfoo.delern.callbacks.OnDeckViewHolderClick;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.util.LogUtil;
import org.dasfoo.delern.viewholders.DeckViewHolder;

/**
 * Created by katarina on 11/19/16.
 */

public class DeckRecyclerViewAdapter extends FirebaseRecyclerAdapter<Deck, DeckViewHolder> {

    private static final String TAG = LogUtil.tagFor(DeckRecyclerViewAdapter.class);

    private OnDeckViewHolderClick mOnDeckViewHolderClick;
    private Context mContext;
    /**
     * @param modelClass      Firebase will marshall the data at a location into an instance
     *                        of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list.
     *                        You will be responsible for populating an instance of the
     *                        corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice
     *                        of a location, using some
     */
    public DeckRecyclerViewAdapter(final Class<Deck> modelClass, final int modelLayout,
                                   final Class<DeckViewHolder> viewHolderClass, final Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    /**
     * Each time the data at the given Firebase location changes, this method will be called for each item that needs
     * to be displayed. The first two arguments correspond to the mLayout and mModelClass given to the constructor of
     * this class. The third argument is the item's position in the list.
     * <p>
     * Your implementation should populate the view using the data contained in the model.
     *
     * @param viewHolder The view to populate
     * @param deck      The object containing the data used to populate the view
     * @param position   The position in the list of the view being populated
     */
    @Override
    protected void populateViewHolder(final DeckViewHolder viewHolder, final Deck deck,
                                      final int position) {
        viewHolder.getDesktopTextView().setText(deck.getName());
        viewHolder.setOnViewClick(mOnDeckViewHolderClick);
        viewHolder.setContext(mContext);
        Log.v(TAG, deck.toString());
        Log.v(TAG, String.valueOf(getRef(position).getKey()));
        // TODO(ksheremet): unregister somewhere
        Card.fetchCardsFromDeckToRepeat(getRef(position).getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Log.v(TAG, String.valueOf(dataSnapshot.getChildrenCount()));
                viewHolder.getCountToLearnTextView().setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                Log.v(TAG, databaseError.getMessage());
            }
        });
    }

    public void setOnDeckViewHolderClick(final OnDeckViewHolderClick onDeckViewHolderClick) {
        this.mOnDeckViewHolderClick = onDeckViewHolderClick;
    }

    public void setContext(final Context context) {
        this.mContext = context;
    }
}
