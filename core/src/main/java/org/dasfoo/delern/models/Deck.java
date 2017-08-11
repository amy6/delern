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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.Query;

import org.dasfoo.delern.models.helpers.MultiWrite;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by katarina on 10/11/16.
 * Model class for deck.
 */
@SuppressWarnings({"checkstyle:MemberName", "checkstyle:HiddenField"})
public class Deck extends AbstractModel implements Parcelable {

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

    private String name;
    private String deckType;
    private String category;
    // TODO(ksheremet): sync when app has the Internet.
    private long lastSyncAt;
    private boolean accepted;

    /**
     * An empty constructor is required for Firebase deserialization.
     */
    private Deck() {
        super(null, null);
    }

    /**
     * Create a deck object with User as a parent.
     *
     * @param parent User which this deck belongs to.
     */
    public Deck(final User parent) {
        super(parent, null);
        lastSyncAt = System.currentTimeMillis();
    }

    /**
     * Parcelable deserializer.
     *
     * @param in parcel.
     */
    // TODO(dotdoom): investigate the possible issues here
    @SuppressWarnings("PMD.UseProperClassLoader")
    protected Deck(final Parcel in) {
        super((User) in.readParcelable(User.class.getClassLoader()), in.readString());
        name = in.readString();
        deckType = in.readString();
        category = in.readString();
        lastSyncAt = in.readLong();
        // Reading and writing boolean for parceable
        // https://goo.gl/PLRLWY
        accepted = in.readByte() != 0;
    }

    /**
     * Get requested amount of cards for learning.
     *
     * @param limit maximum number of cards the query can return.
     * @return a Query that will fetch ScheduledCards.
     */
    @Exclude
    public Query fetchCardsToRepeatWithLimitQuery(final int limit) {
        long time = System.currentTimeMillis();
        return getChildReference(ScheduledCard.class)
                .orderByChild("repeatAt")
                .endAt(time)
                .limitToFirst(limit);
    }

    /**
     * Create new deck with "owner" access.
     *
     * @return FirebaseTaskAdapter for the write operation.
     */
    @Exclude
    public Completable create() {
        DeckAccess deckAccess = new DeckAccess(this);
        deckAccess.setKey(getUser().getKey());
        deckAccess.setAccess("owner");
        return new MultiWrite()
                .save(this)
                .save(deckAccess)
                .write();
    }

    /**
     * Remove the current deck from the database, including Cards, ScheduledCards, Views and access.
     *
     * @return FirebaseTaskAdapter for the delete operation.
     */
    @Exclude
    public Completable delete() {
        return new MultiWrite()
                .delete(this)
                .delete(this.getChildReference(Card.class))
                .delete(this.getChildReference(ScheduledCard.class))
                .delete(this.getChildReference(View.class))
                .delete(this.getChildReference(DeckAccess.class))
                .write();
    }

    /**
     * Start a watcher that will trigger for every top-1 card that needs to be learned, in sequence.
     *
     * @return Multi-shot TaskAdapter, called initially and after each change to ScheduledCard. The
     * parameter will be a Card associated with the ScheduledCard, and ScheduledCard will be
     * set as its parent.
     */
    @Exclude
    public Observable<Card> startScheduledCardWatcher() {
        return fetchChildren(fetchCardsToRepeatWithLimitQuery(1), ScheduledCard.class)
                .takeUntil((final List<ScheduledCard> scs) -> scs.isEmpty())
                .flatMap(scs -> {
                    if (scs.isEmpty()) {
                        return Completable.complete().toObservable();
                    }
                    ScheduledCard sc = scs.get(0);
                    return sc.fetchChild(getChildReference(Card.class, sc.getKey()), Card.class)
                            .firstOrError().toObservable();
                });
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
    // TODO(ksheremet): should this be named "getAccepted"?
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
        return "Deck{" + super.toString() +
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
    public void writeToParcel(final Parcel parcel, final int flags) {
        parcel.writeParcelable(getUser(), flags);
        parcel.writeString(this.getKey());
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

    /**
     * Get the User this Deck is associated with.
     *
     * @return AbstractModel parent casted to User (if set).
     */
    @Exclude
    public User getUser() {
        return (User) getParent();
    }

    /**
     * {@inheritDoc}
     */
    @Exclude
    @Override
    public <T> DatabaseReference getChildReference(final Class<T> childClass) {
        if (childClass == Card.class) {
            DatabaseReference reference = getParent().getChildReference(childClass).child(getKey());
            reference.keepSynced(true);
            return reference;
        }
        return getParent().getChildReference(childClass).child(getKey());
    }
}
