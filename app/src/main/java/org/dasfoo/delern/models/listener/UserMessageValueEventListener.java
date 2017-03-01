package org.dasfoo.delern.models.listener;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.dasfoo.delern.util.LogUtil;

/**
 * Created by katarina on 3/1/17.
 * Abstract class that implement onCancel for all listeners.
 */
public abstract class UserMessageValueEventListener implements ValueEventListener {

    private static final String TAG = LogUtil.tagFor(UserMessageValueEventListener.class);

    private Context mContext;

    /**
     * Constructor. Gets context to write message to user in case of error.
     *
     * @param context Context
     */
    public UserMessageValueEventListener(final Context context) {
        this.mContext = context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void onDataChange(DataSnapshot dataSnapshot);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCancelled(final DatabaseError databaseError) {
        Log.e(TAG, databaseError.getDetails());
        if (mContext != null) {
            Toast.makeText(mContext, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
