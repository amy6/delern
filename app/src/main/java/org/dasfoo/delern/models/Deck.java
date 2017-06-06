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

package org.dasfoo.delern.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.dasfoo.delern.listeners.AbstractOnDataChangeListener;
import org.dasfoo.delern.listeners.OnFbOperationCompleteListener;
import org.dasfoo.delern.util.LogUtil;
import org.dasfoo.delern.util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by katarina on 10/11/16.
 * Model class for accessing deck/userId node.
 */
@SuppressWarnings({"checkstyle:MemberName", "checkstyle:HiddenField"})
public class Deck implements Parcelable {

    /**
     * Classes implementing the Parcelable interface must also have a non-null static
     * field called CREATOR of a type that implements the Parcelable.Creator interface.
     * https://developer.android.com/reference/android/os/Parcelable.html
     */
    public static final Creator<Deck> CREATOR = new Creator<Deck>() {
        @Override
        public Deck createFromParcel(final Parcel in) {
            return new Deck(in);
        }

        @Override
        public Deck[] newArray(final int size) {
            return new Deck[size];
        }
    };

    @Exclude
    private static final String TAG = LogUtil.tagFor(Deck.class);
    @Exclude
    private static final String DECKS = "decks";

    @Exclude
    private String dId;
    private String name;
    private String deckType;
    private String category;
    // TODO(ksheremet): sync when app has the Internet.
    private long lastSyncAt;
    private boolean accepted;

    /**
     * The empty constructor is required for Firebase de-serialization.
     */
    public Deck() {
        // This constructor is intentionally left empty.
    }

    /**
     * Constructor for deck.
     *
     * @param name         name of deck.
     * @param dType        sets type of deck from DeckType class.
     * @param userAccepted whether user is accepted deck or not (for sharing).
     */
    public Deck(final String name, final String dType, final boolean userAccepted) {
        this.name = name;
        this.deckType = dType;
        this.accepted = userAccepted;
        this.lastSyncAt = System.currentTimeMillis();
    }

    protected Deck(final Parcel in) {
        dId = in.readString();
        name = in.readString();
        deckType = in.readString();
        category = in.readString();
        lastSyncAt = in.readLong();
        // Reading and writing boolean for parceable
        // https://goo.gl/PLRLWY
        accepted = in.readByte() != 0;
    }

    /**
     * Gets reference to decks in Firebase.
     *
     * @return firebase database reference.
     */
    @Exclude
    public static DatabaseReference getFirebaseDecksRef() {
        DatabaseReference deckDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child(DECKS).child(User.getCurrentUser().getUid());
        // keep sync special location for offline use
        // https://firebase.google.com/docs/database/android/offline-capabilities
        deckDatabaseReference.keepSynced(true);
        return deckDatabaseReference;
    }

    /**
     * Returns decks of user.
     *
     * @return query decks of user.
     */
    @Exclude
    public static Query getUsersDecks() {
        // TODO(ksheremet): check whether this null is possible or not
        if (User.getCurrentUser() == null) {
            Log.e(TAG, "getUsersDecks: User is not signed in");
            return null;
        } else {
            return getFirebaseDecksRef();
        }

    }

    /**
     * Creates new deck in Firebase.
     *
     * @param deck               new deck.
     * @param onCompleteListener listener for handling onComplete.
     * @param listener           handles on data change. It is needed for offline capabilities.
     */
    @Exclude
    public static void createNewDeck(final Deck deck,
                                     final OnFbOperationCompleteListener onCompleteListener,
                                     final AbstractOnDataChangeListener listener) {
        DatabaseReference reference = getFirebaseDecksRef().push();
        reference.addListenerForSingleValueEvent(listener);

        String key = reference.getKey();
        // Write deckAccess
        DeckAccess deckAccess = new DeckAccess("owner");
        Map<String, Object> newDeck = new ConcurrentHashMap<>();
        newDeck.put(DeckAccess.getDeckAccessNodeByDeckId(key), deckAccess.getAccess());
        newDeck.put(getDeckNodeById(key), deck);

        Log.d(TAG, newDeck.toString());
        FirebaseDatabase
                .getInstance()
                .getReference()
                .updateChildren(newDeck)
                .addOnCompleteListener(onCompleteListener);
    }

    /**
     * Gets deck node for the user. deck/userId/deckId
     *
     * @param deckId id of deck.
     * @return deck node of the user.
     */
    @Exclude
    public static String getDeckNodeById(final String deckId) {
        return StringUtil.joinFirebasePath(Deck.DECKS, User.getCurrentUser().getUid(), deckId);
    }

    /**
     * Remove deck by ID.
     *
     * @param deckId   deck ID for removing.
     * @param listener listener handles on success and on failure results.
     */
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    @Exclude
    public static void deleteDeck(final String deckId,
                                  final OnFbOperationCompleteListener listener) {
        // Values must be null. It is impossible with ConcurentHashMap. For deleting deck
        // concurrent map is unused (always 1 flow).
        Map<String, Object> removeDeck = new HashMap<>();
        removeDeck.put(Deck.getDeckNodeById(deckId), null);
        removeDeck.put(Card.getCardsNodeByDeckId(deckId), null);
        removeDeck.put(ScheduledCard.getScheduledCardNodeByDeckId(deckId), null);
        removeDeck.put(View.getViewsNodeByDeckId(deckId), null);
        removeDeck.put(DeckAccess.getDeckAccessNodeByDeckId(deckId), null);

        Log.v(TAG, "Deck removal operation:" + removeDeck.toString());

        FirebaseDatabase
                .getInstance()
                .getReference()
                .updateChildren(removeDeck)
                .addOnCompleteListener(listener);
    }

    /**
     * Renames deck.
     *
     * @param deck     deck to rename.
     * @param listener listener for handling on success and failure.
     */
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    @Exclude
    public static void updateDeck(final Deck deck, final OnFbOperationCompleteListener
            listener) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(deck.getdId(), deck);
        getFirebaseDecksRef().updateChildren(childUpdates).addOnCompleteListener(listener);
    }

    /**
     * Getter for ID of deck.
     *
     * @return ID of deck.
     */
    @Exclude
    public String getdId() {
        return dId;
    }

    /**
     * Setter for deck ID.
     *
     * @param dId ID of deck.
     */
    @Exclude
    public void setdId(final String dId) {
        this.dId = dId;
    }

    /**
     * Getter for name of deck.
     *
     * @return name of deck.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name of deck.
     *
     * @param name name of deck.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Getter for cards type in deck.
     *
     * @return type of cards in deck.
     */
    public String getDeckType() {
        if (deckType == null) {
            return DeckType.BASIC.name();
        }
        return deckType;
    }

    /**
     * Setter for cards type in deck.
     *
     * @param deckType type of cards in deck.
     */
    public void setDeckType(final String deckType) {
        this.deckType = deckType;
    }

    /**
     * Getter for time when deck was synced.
     *
     * @return time when deck was synced.
     */
    public long getLastSyncAt() {
        return lastSyncAt;
    }

    /**
     * Update time when deck was synced to current.
     */
    public void setLastSyncAt() {
        this.lastSyncAt = System.currentTimeMillis();
    }

    /**
     * Getter for category of deck.
     *
     * @return category of deck.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Setter for category of deck.
     *
     * @param category category of deck.
     */
    public void setCategory(final String category) {
        this.category = category;
    }

    /**
     * Whether deck is accepted by user of not.
     *
     * @return true if deck is accepted, otherwise false.
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * User can accept shared deck or not.
     *
     * @param accepted true of false
     */
    public void setAccepted(final boolean accepted) {
        this.accepted = accepted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Deck{" +
                "dId='" + dId + '\'' +
                ", name='" + name + '\'' +
                ", deckType='" + deckType + '\'' +
                ", category='" + category + '\'' +
                ", accepted='" + accepted + '\'' +
                ", lastSyncAt='" + lastSyncAt + '\'' +
                '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(final Parcel parcel, final int i) {
        parcel.writeString(this.dId);
        parcel.writeString(this.name);
        parcel.writeString(this.deckType);
        parcel.writeString(this.category);
        parcel.writeLong(this.lastSyncAt);
        if (accepted) {
            parcel.writeByte((byte) 1);
        } else {
            parcel.writeByte((byte) 0);
        }
    }
}
