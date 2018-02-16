/*
 * Copyright (C) 2017 Katarina Sheremet
 * This file is part of Delern.
 *
 * Delern is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Delern is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with  Delern.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.dasfoo.delern.util;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper for tracking for Performance traces and Events analytics, together.
 */
public final class PerfEventTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(PerfEventTracker.class);
    private static Map<Event, Trace> sRunningEvents = new ConcurrentHashMap<>();

    private PerfEventTracker() {
    }

    /**
     * Track a one-time Analytics event (no span or Trace associated).
     *
     * @param event   event type to be tracked (only one event per type can be tracked at a time).
     * @param context {@link FirebaseAnalytics#getInstance(Context)}
     * @param bundle  {@link FirebaseAnalytics#logEvent(String, Bundle)}
     */
    public static void trackEvent(@NonNull final Event event, @NonNull final Context context,
                                  @Nullable final Bundle bundle) {
        FirebaseAnalytics.getInstance(context).logEvent(event.track(), bundle);
    }

    /**
     * Like {@link #trackEvent(Event, Context, Bundle)}, but also starts a performance Trace.
     *
     * @param event     {@link #trackEvent(Event, Context, Bundle)}
     * @param context   {@link #trackEvent(Event, Context, Bundle)}
     * @param bundle    {@link #trackEvent(Event, Context, Bundle)}
     * @param container lifecycle to attach Trace to (onDestroy will stop the trace).
     */
    public static void trackEventStart(@NonNull final Event event, @NonNull final Context context,
                                       @Nullable final Bundle bundle,
                                       @Nullable final LifecycleOwner container) {
        trackEvent(event, context, bundle);
        if (sRunningEvents.containsKey(event)) {
            LOGGER.error("Event {} triggered while performance counting for it was already in " +
                    "progress! Finishing now", event, new Throwable());
            trackEventFinish(event);
        }

        Trace t = FirebasePerformance.startTrace(event.track());
        sRunningEvents.put(event, t);
        t.start();

        if (container != null) {
            // Container can be null if we test cross-activity lifecycle
            container.getLifecycle().addObserver(new LifecycleObserver() {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                public void stopTrace() {
                    trackEventFinish(event);
                }
            });
        }
    }

    /**
     * While a Trace started with {@link #trackEventStart(Event, Context, Bundle, LifecycleOwner)}
     * is in progress, increments counter, associated with that Trace, by 1.
     *
     * @param event   {@link #trackEventStart(Event, Context, Bundle, LifecycleOwner)}
     * @param counter counter name.
     */
    public static void trackEventCounter(@NonNull final Event event,
                                         @NonNull final Counter counter) {
        Trace t = sRunningEvents.get(event);
        if (t == null) {
            LOGGER.error("Attempt to increment counter {} for trace event {}, but trace is not " +
                    "running! Ignoring", counter, event, new Throwable());
        } else {
            t.incrementCounter(counter.track());
        }
    }

    /**
     * Marks stop of a performance Trace associated with the event.
     *
     * @param event {@link #trackEventStart(Event, Context, Bundle, LifecycleOwner)}
     */
    public static void trackEventFinish(@NonNull final Event event) {
        Trace t = sRunningEvents.remove(event);
        // Do not log error for already stopped events, this unnecessarily complicates caller code.
        if (t != null) {
            t.stop();
        }
    }

    /**
     * Events that we register (not necessarily spanning - therefore, not necessarily traced.
     */
    public enum Event {
        /**
         * App launched -> all decks loaded (not including per-deck access and number of cards).
         */
        DECKS_LOAD,

        /**
         * Clicking on a deck in decks list -> first card appearing.
         */
        DECK_LEARNING_START,

        /**
         * Clicking on a deck in decks list -> closing learning activity.
         */
        DECK_LEARNING_SESSION,

        /**
         * Clicking on Yes/No on the card -> next card or "Finished" appearing.
         */
        DECK_LEARNING_NEXT_CARD,

        /**
         * Clicking on "Add" in "New deck name" dialog -> "Add card" appearing.
         */
        DECK_CREATE,

        /**
         * Clicking on "Settings" in deck menu.
         */
        DECK_SETTINGS_OPEN,

        /**
         * Clicking "back" in deck settings.
         */
        DECK_SETTINGS_SAVE,

        /**
         * Clicking on ">>" in sharing -> remote function returns.
         */
        DECK_SHARE,

        /**
         * Clicking on "Edit" in card viewer.
         */
        CARD_EDIT_OPEN,

        /**
         * Clicking on "Add" in card editor -> "Added" toast appearing.
         */
        CARD_SAVE,

        /**
         * Clicking "Invite" in "User you are trying to share with does not exist".
         */
        INVITE;

        /**
         * Generate an identifier for this event.
         *
         * @return name of this event to use in Analytics and Traces.
         */
        public String track() {
            if (this == DECK_SHARE) {
                return FirebaseAnalytics.Event.SHARE;
            }
            return name().toLowerCase(Locale.ROOT);
        }
    }

    /**
     * Counters for Traces that can be incremented via trackEventCounter.
     */
    public enum Counter {
        /**
         * Incremented for each card shown during a single deck learning session.
         */
        DECK_LEARNING_SESSION_CARDS;

        /**
         * Generate an identifier for this counter.
         *
         * @return name of this counter for Traces.
         */
        public String track() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
