package org.dasfoo.delern;

import android.app.Activity;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.text.InputType;
import android.util.Log;

import org.dasfoo.delern.signin.SignInActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withInputType;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityTestRule<DelernMainActivity> mActivityRule = new ActivityTestRule<>(
            DelernMainActivity.class);

    @Before
    public void setUp() {
        // Raise Idling policy timeout because CI emulator can be really slow.
        IdlingPolicies.setIdlingResourceTimeout(2, TimeUnit.MINUTES);
        IdlingPolicies.setMasterPolicyTimeout(2, TimeUnit.MINUTES);

        Espresso.registerIdlingResources(new IdlingResource() {
            private ResourceCallback mResourceCallback;
            private boolean mIsIdle = false;

            private Activity getCurrentActivity() {
                return Iterables.getFirst(ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(Stage.RESUMED), null);
            }

            @Override
            public String getName() {
                return "Sign In";
            }

            @Override
            public boolean isIdleNow() {
                if (!mIsIdle) {
                    Activity currentActivity = getCurrentActivity();
                    if (currentActivity != null &&
                            currentActivity.getClass() != SignInActivity.class) {
                        mIsIdle = true;
                        mResourceCallback.onTransitionToIdle();
                    }
                }
                return mIsIdle;
            }

            @Override
            public void registerIdleTransitionCallback(final ResourceCallback callback) {
                mResourceCallback = callback;
            }
        });
    }

    @After
    public void tearDown() {
        for (IdlingResource rc : Espresso.getIdlingResources()) {
            Espresso.unregisterIdlingResources(rc);
        }
    }

    @Test
    public void addDeck() {
        onView(withId(R.id.fab)).perform(click());
        onView(withInputType(InputType.TYPE_CLASS_TEXT))
                .perform(typeTextIntoFocusedView("Espresso"), closeSoftKeyboard());
        onView(withText(R.string.add)).perform(click());
        onView(withId(R.id.f_add_card_button)).check(matches(isDisplayed()));
        pressBack();
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
    }
}
