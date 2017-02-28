package org.dasfoo.delern.util;

import android.animation.Animator;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewAnimationUtils;

/**
 * Class for animation logic.
 */
public final class Animation {

    /**
     * Hide utility class default constructor.
     */
    private Animation() {
    }

    /**
     * Creates appearance animation for a view.
     *
     * @param view view
     * @return animation for view
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Animator appearanceAnimation(final View view) {
        // get the center for the clipping circle
        int cx = view.getWidth() / 2;
        int cy = view.getHeight() / 2;
        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);
        return ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
    }
}
