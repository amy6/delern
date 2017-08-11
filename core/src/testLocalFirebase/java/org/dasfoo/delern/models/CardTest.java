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

import org.dasfoo.delern.test.FirebaseServerUnitTest;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.ObservableSource;

import static org.junit.Assert.assertTrue;

public class CardTest extends FirebaseServerUnitTest {

    private User mUser;

    @Before
    public void createUser() throws Exception {
        mUser = signIn();
    }

    @Test
    public void cards_createdAndFetched() throws Exception {
        final Deck deck = new Deck(mUser);
        List<Card> cards = mUser.save().andThen((final CompletableObserver cs) -> {
            deck.setName("CreateCards");
            deck.setAccepted(true);
            deck.create().subscribe(cs);
        }).andThen((ObservableSource<List<Deck>>) observer ->
                mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                        .subscribe(observer)
        ).firstOrError().flatMapCompletable(data -> {
            assertTrue(data.size() == 1 && data.get(0).getName().equals("CreateCards"));
            Card newCard = new Card(data.get(0));
            return newCard.create("frontSide", "backSide");
        }).andThen((ObservableSource<List<Card>>) observer ->
                deck.fetchChildren(deck.getChildReference(Card.class), Card.class)
                        .subscribe(observer)
        ).firstOrError().blockingGet();
        assertTrue(cards.size() == 1 && cards.get(0).getFront().equals("frontSide") &&
                cards.get(0).getBack().equals("backSide"));
    }

    @Test
    public void cards_createdAndDeleted() {
        final Deck deck = new Deck(mUser);
        List<Card> cards = mUser.save().andThen((final CompletableObserver cs) -> {
            deck.setName("TestDelete");
            deck.setAccepted(true);
            deck.create().subscribe(cs);
        }).andThen((ObservableSource<List<Deck>>) observer ->
                mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                        .subscribe(observer)
        ).firstOrError().flatMapCompletable(data -> {
            assertTrue(data.size() == 1 && data.get(0).getName().equals("TestDelete"));
            Card newCard = new Card(data.get(0));
            return newCard.create("frontSide", "backSide");
        }).andThen((ObservableSource<List<Card>>) observer ->
                deck.fetchChildren(deck.getChildReference(Card.class), Card.class)
                        .subscribe(observer)
        ).firstOrError().flatMapCompletable(data -> {
            assertTrue(data.size() == 1 && data.get(0).getFront().equals("frontSide") &&
                    data.get(0).getBack().equals("backSide"));
            return data.get(0).delete();
        }).andThen((ObservableSource<List<Card>>) observer ->
                deck.fetchChildren(deck.getChildReference(Card.class), Card.class)
                        .subscribe(observer)
        ).firstOrError().blockingGet();
        assertTrue(cards.size() == 0);
    }

    @Test
    public void cards_createdAndEdited() {
        final Deck deck = new Deck(mUser);
        List<Card> cards = mUser.save().andThen((final CompletableObserver cs) -> {
            deck.setName("TestRename");
            deck.setAccepted(true);
            deck.create().subscribe(cs);
        }).andThen((ObservableSource<List<Deck>>) observer ->
                mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                        .subscribe(observer)
        ).firstOrError().flatMapCompletable(data -> {
            assertTrue(data.size() == 1 && data.get(0).getName().equals("TestRename"));
            Card newCard = new Card(data.get(0));
            return newCard.create("frontSide", "backSide");
        }).andThen((ObservableSource<List<Card>>) observer ->
                deck.fetchChildren(deck.getChildReference(Card.class), Card.class)
                        .subscribe(observer)
        ).firstOrError().flatMapCompletable(data -> {
            assertTrue((data.size() == 1 && data.get(0).getFront().equals("frontSide") &&
                    data.get(0).getBack().equals("backSide")));
            data.get(0).setFront("frontSide2");
            data.get(0).setBack("backSide2");
            return data.get(0).save();
        }).andThen((ObservableSource<List<Card>>) observer ->
                deck.fetchChildren(deck.getChildReference(Card.class), Card.class)
                        .subscribe(observer)
        ).firstOrError().blockingGet();
        assertTrue(cards.size() == 1 && cards.get(0).getFront().equals("frontSide2") &&
                cards.get(0).getBack().equals("backSide2"));
    }

    // TODO(ksheremet): add test for "solve card 1", "solve card 2", end.

    @Test
    public void cards_createdAndAnsweredTrue() {
        final Deck deck = new Deck(mUser);
        List<ScheduledCard> scs = mUser.save().andThen((final CompletableObserver cs) -> {
            deck.setName("TestAnswer");
            deck.setAccepted(true);
            deck.create().subscribe(cs);
        }).andThen((ObservableSource<List<Deck>>) observer ->
                mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                        .subscribe(observer)
        ).firstOrError().flatMapCompletable((final List<Deck> data) -> {
            assertTrue(data.size() == 1 && data.get(0).getName().equals("TestAnswer"));
            Card newCard = new Card(data.get(0));
            return newCard.create("frontSide", "backSide");
        }).andThen((ObservableSource<Card>) observer ->
                deck.startScheduledCardWatcher().subscribe(observer)
        ).firstOrError().flatMapCompletable((final Card card) ->
                card.answer(true)
        ).andThen((ObservableSource<List<ScheduledCard>>) observer ->
                deck.fetchChildren(deck.getChildReference(ScheduledCard.class), ScheduledCard.class)
                        .subscribe(observer)
        ).firstOrError().blockingGet();
        assertTrue(scs.size() == 1
                && scs.get(0).getLevel().equals(Level.L1.name()));
    }

    @Test
    public void cards_createdAndAnsweredFalse() {
        final Deck deck = new Deck(mUser);
        List<ScheduledCard> scs = mUser.save().andThen((final CompletableObserver cs) -> {
            deck.setName("TestAnswer");
            deck.setAccepted(true);
            deck.create().subscribe(cs);
        }).andThen((ObservableSource<List<Deck>>) observer ->
                mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                        .subscribe(observer)
        ).firstOrError().flatMapCompletable((final List<Deck> data) -> {
            assertTrue(data.size() == 1 && data.get(0).getName().equals("TestAnswer"));
            Card newCard = new Card(data.get(0));
            return newCard.create("frontSide", "backSide");
        }).andThen((ObservableSource<Card>) observer ->
                deck.startScheduledCardWatcher().subscribe(observer)
        ).firstOrError().flatMapCompletable((final Card card) ->
                card.answer(false)
        ).andThen((ObservableSource<List<ScheduledCard>>) observer ->
                deck.fetchChildren(deck.getChildReference(ScheduledCard.class), ScheduledCard.class)
                        .subscribe(observer)
        ).firstOrError().blockingGet();
        assertTrue(scs.size() == 1
                && scs.get(0).getLevel().equals(Level.L0.name()));
    }

}
