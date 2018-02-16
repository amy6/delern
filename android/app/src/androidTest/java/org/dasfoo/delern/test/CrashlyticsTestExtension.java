package org.dasfoo.delern.test;

import com.crashlytics.android.Crashlytics;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Extensions to Crashlytics to aid in instrumented tests.
 */
public class CrashlyticsTestExtension {
    /**
     * Unconditionally send all fatal and non-fatal crash reports now (may be asynchronous).
     */
    public static void sendReportsNow() {
        // Terrible, terrible hack because Crashlytics don't offer this functionality:
        // https://stackoverflow.com/questions/47377319/
        try {
            Method getController = Crashlytics.getInstance().core.getClass().
                    getDeclaredMethod("getController");
            getController.setAccessible(true);
            Object controller = getController.invoke(Crashlytics.getInstance().core);

            // When Crashlytics is disabled, controller may be null.
            if (controller != null) {
                Method openSession = controller.getClass().getDeclaredMethod("doOpenSession");
                openSession.setAccessible(true);
                openSession.invoke(controller);

                Method doInBackground = Crashlytics.getInstance().core.getClass().
                        getDeclaredMethod("doInBackground");
                doInBackground.setAccessible(true);
                doInBackground.invoke(Crashlytics.getInstance().core);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
