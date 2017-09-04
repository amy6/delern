package org.dasfoo.delern.idlingresouces;

import android.app.Activity;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.core.internal.deps.guava.collect.Iterables;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.v7.widget.AppCompatButton;
import android.widget.Button;

import org.dasfoo.delern.listdecks.DelernMainActivity;
import org.dasfoo.delern.R;
import org.dasfoo.delern.SplashScreenActivity;
import org.dasfoo.delern.addupdatecard.AddEditCardActivity;
import org.dasfoo.delern.signin.SignInActivity;

/**
 * Created by katarina on 8/29/17.
 */

public class RequestIdlingResource implements IdlingResource {

    private ResourceCallback mResourceCallback;
    private boolean mIsIdle = false;

    @Override
    public String getName() {
        return RequestIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        if (mIsIdle) return true;

        Activity activity = getCurrentActivity();
        if (activity == null) return false;

        idlingCheck(activity);

        if (mIsIdle) {
            mResourceCallback.onTransitionToIdle();
        }
        return mIsIdle;
    }

    private static Activity getCurrentActivity() {
        final Activity[] activity = new Activity[1];
        java.util.Collection<Activity> activities = ActivityLifecycleMonitorRegistry
                .getInstance().getActivitiesInStage(Stage.RESUMED);
        activity[0] = Iterables.getOnlyElement(activities);
        return activity[0];
    }

    @Override
    public void registerIdleTransitionCallback(
            ResourceCallback resourceCallback) {
        this.mResourceCallback = resourceCallback;
    }

    private void idlingCheck(Activity activity) {

        if (activity == null) {
            mIsIdle = false;
            return;
        }
        if (activity.getClass() == AddEditCardActivity.class) {
            Button btnAddCardToDb = activity.findViewById(R.id.add_card_to_db);
            mIsIdle = (btnAddCardToDb != null && btnAddCardToDb.getElevation() == 0);
            return;
        }
        if (activity.getClass() == SignInActivity.class &&
                activity.getClass() == SplashScreenActivity.class) {
            mIsIdle = false;
            return;
        }
        if (activity.getClass() == DelernMainActivity.class) {
            // Add deck when Add button in AlertDialog was pressed
            AppCompatButton appCompatButton = activity.findViewById(android.R.id.button1);
            mIsIdle = (appCompatButton != null && !appCompatButton.isPressed());
        }
        mIsIdle = true;
    }
}
