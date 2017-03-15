package org.dasfoo.delern.listeners;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by katarina on 3/7/17.
 * Listeners whether operation in Firebase was completed. If not, writes log message.
 * @param <T> describes type parameter.
 * TODO(ksheremet): Write message to user.
 */
public class OnFbOperationCompleteListener<T> implements OnCompleteListener<Void> {

    private final String mTag;
    private T mSavedParameter;

    /**
     * Tag for logging. It describes from what class was called.
     *
     * @param tag tag for logging.
     */
    public OnFbOperationCompleteListener(final String tag) {
        this.mTag = tag;
    }

    /**
     * {@inheritDoc}
     * Writes log on failure. Logic for success must be implemented in inherited class.
     */
    @Override
    public void onComplete(@NonNull final Task task) {
        if (!task.isSuccessful()) {
            Log.e(mTag, "Operation is not completed:", task.getException());
        }
    }

    /**
     * Getter for saver paramater.
     *
     * @return paramater.
     */
    public T getSavedParameter() {
        return mSavedParameter;
    }

    /**
     * Setter for saved parameter.
     *
     * @param savedParameter parameter to save.
     */
    public void setSavedParameter(final T savedParameter) {
        this.mSavedParameter = savedParameter;
    }
}
