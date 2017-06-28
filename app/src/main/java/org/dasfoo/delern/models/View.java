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

/**
 * Created by katarina on 2/23/17.
 * Model class for view.
 */
@SuppressWarnings({"checkstyle:MemberName", "checkstyle:HiddenField"})
public class View extends AbstractModel {

    private String cardId;
    private String levelBefore;
    private String reply;
    private long timestamp;

    /**
     * An empty constructor is required for Firebase deserialization.
     */
    private View() {
        super(null);
    }

    /**
     * Create a view object associated with a Card.
     *
     * @param card Card which this View belongs to.
     */
    public View(final Card card) {
        super(card);
        this.cardId = card.getKey();
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Getter for card Id.
     *
     * @return id of card.
     */
    public String getCardId() {
        return cardId;
    }

    /**
     * Setter for card Id.
     *
     * @param cardId id of card.
     */
    public void setCardId(final String cardId) {
        this.cardId = cardId;
    }

    /**
     * Getter for card level before learning card.
     *
     * @return level of card before leaning it.
     */
    public String getLevelBefore() {
        return levelBefore;
    }

    /**
     * Sets level of card before reply(Y/N) on it.
     *
     * @param levelBefore level of card before learning it.
     */
    public void setLevelBefore(final String levelBefore) {
        this.levelBefore = levelBefore;
    }

    /**
     * Getter for user reply by learning card. It can be (Y or N).
     *
     * @return user reply
     */
    public String getReply() {
        return reply;
    }

    /**
     * Setter for user reply by learning card.
     *
     * @param reply user reply (Y or N)
     */
    public void setReply(final String reply) {
        this.reply = reply;
    }

    /**
     * Getter for time when user looked at card.
     *
     * @return time
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets time to View when user looked at card.
     *
     * @param timestamp time (ServerValue.Timestamt)
     */
    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> DatabaseReference getChildReference(final Class<T> childClass) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "View{" + super.toString() +
                ", cardId='" + cardId + '\'' +
                ", levelBefore='" + levelBefore + '\'' +
                ", reply='" + reply + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
