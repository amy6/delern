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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

/**
 * Created by katarina on 2/20/17.
 * Model class for learning.
 */
@SuppressWarnings({"checkstyle:MemberName", "checkstyle:HiddenField"})
public class ScheduledCard extends AbstractModel {

    private String level;
    private long repeatAt;

    /**
     * An empty constructor is required for Firebase deserialization.
     */
    private ScheduledCard() {
        super(null);
    }

    /**
     * Create a ScheduledCard object associated with a deck.
     *
     * @param deck Deck this ScheduledCard belongs to.
     */
    public ScheduledCard(final Deck deck) {
        super(deck);
    }

    /**
     * Get the Deck this ScheduledCard is associated with.
     *
     * @return AbstractModel parent casted to Deck (if set).
     */
    @Exclude
    public Deck getDeck() {
        return (Deck) getParent();
    }

    /**
     * Gets level of card.
     *
     * @return level of card.
     */
    public String getLevel() {
        return level;
    }

    /**
     * Sets level of card.
     *
     * @param level level of card.
     */
    public void setLevel(final String level) {
        this.level = level;
    }

    /**
     * Gets the next time for card to repeat.
     *
     * @return time for the next repeating card
     */
    public long getRepeatAt() {
        return repeatAt;
    }

    /**
     * Sets the next time for card to repeat.
     *
     * @param repeatAt time for the next repeating card.
     */
    public void setRepeatAt(final long repeatAt) {
        this.repeatAt = repeatAt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ScheduledCard{" + super.toString() +
                ", level='" + level + '\'' +
                ", repeatAt=" + repeatAt +
                '}';
    }

    /**
     * {@inheritDoc}
     */
    @Exclude
    @Override
    public <T> DatabaseReference getChildReference(final Class<T> childClass) {
        if (childClass == Card.class) {
            return getDeck().getChildReference(Card.class);
        }
        if (childClass == View.class) {
            return getDeck().getChildReference(View.class, getKey());
        }
        return null;
    }
}
