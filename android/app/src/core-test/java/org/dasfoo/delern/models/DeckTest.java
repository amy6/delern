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

import org.dasfoo.delern.test.FirebaseServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.ObservableSource;

import static org.junit.Assert.assertTrue;

/**
 * Test for Deck model.
 */
public class DeckTest {

    private User mUser;

    @Rule
    public final FirebaseServerRule mFirebaseServer = new FirebaseServerRule();

    @Before
    public void createUser() throws Exception {
        mUser = mFirebaseServer.signIn();
    }

    @Test
    public void decks_createdAndFetched() throws Exception {
        List<Deck> data = mUser.save().andThen((final CompletableObserver cs) -> {
            Deck deck = new Deck(mUser);
            deck.setName("My Deck");
            deck.setAccepted(true);
            deck.create().subscribe(cs);
        }).andThen((ObservableSource<List<Deck>>) observer ->
                mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                        .subscribe(observer)
        ).firstOrError().blockingGet();
        assertTrue(data.size() == 1 && data.get(0).getName().equals("My Deck"));
    }

    @Test
    public void decks_renamed() {
        List<Deck> renamedData = mUser.save().andThen((final CompletableObserver cs) -> {
            Deck deck = new Deck(mUser);
            deck.setName("ToRename");
            deck.setAccepted(true);
            deck.create().subscribe(cs);
        }).andThen((ObservableSource<List<Deck>>) observer ->
                mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                        .subscribe(observer)
        ).firstOrError().flatMapCompletable(data -> {
            assertTrue(data.size() == 1 && data.get(0).getName().equals("ToRename"));
            Deck fetchedDeck = data.get(0);
            fetchedDeck.setName("Renamed");
            return fetchedDeck.save();
        }).andThen((ObservableSource<List<Deck>>) observer ->
                mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                        .subscribe(observer)
        ).firstOrError().blockingGet();
        assertTrue(renamedData.size() == 1 && renamedData.get(0).getName()
                .equals("Renamed"));
    }

    @Test
    public void decks_changedDeckType() {
        List<Deck> deckTypeData = mUser.save().andThen((final CompletableObserver cs) -> {
            Deck deck = new Deck(mUser);
            deck.setName("DeckType");
            deck.setAccepted(true);
            deck.create().subscribe(cs);
        }).andThen((ObservableSource<List<Deck>>) observer ->
                mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                        .subscribe(observer)
        ).firstOrError().flatMapCompletable(data -> {
            assertTrue(data.size() == 1 && data.get(0).getName().equals("DeckType"));
            Deck fetchedDeck = data.get(0);
            fetchedDeck.setDeckType(DeckType.SWISS.name());
            return fetchedDeck.save();
        }).andThen((ObservableSource<List<Deck>>) observer ->
                mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                        .subscribe(observer)
        ).firstOrError().blockingGet();
        assertTrue(deckTypeData.size() == 1 && deckTypeData.get(0).getDeckType()
                .equals(DeckType.SWISS.name()));
    }

    @Test
    public void decks_checkedUserDeck() {
        List<Deck> userDeck = mUser.save().andThen((final CompletableObserver cs) -> {
            Deck deck = new Deck(mUser);
            deck.setName("UsersDeck");
            deck.setAccepted(true);
            deck.create().subscribe(cs);
        }).andThen((ObservableSource<List<Deck>>) observer ->
                mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                        .subscribe(observer)
        ).firstOrError().blockingGet();
        assertTrue(userDeck.size() == 1 && userDeck.get(0).getName().equals("UsersDeck") &&
                mUser.equals(userDeck.get(0).getUser()));
    }

    @Test
    public void decks_createdAndDeleted() throws Exception {
        List<Deck> deletedDeck = mUser.save().andThen((final CompletableObserver cs) -> {
            Deck deck = new Deck(mUser);
            deck.setName("Created");
            deck.setAccepted(true);
            deck.create().subscribe(cs);
        }).andThen((ObservableSource<List<Deck>>) observer ->
                mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                        .subscribe(observer)
        ).firstOrError().flatMapCompletable(data -> {
            assertTrue(data.size() == 1 && data.get(0).getName().equals("Created"));
            return data.get(0).delete();
        }).andThen((ObservableSource<List<Deck>>) observer ->
                mUser.fetchChildren(mUser.getChildReference(Deck.class), Deck.class)
                        .subscribe(observer)
        ).firstOrError().blockingGet();
        assertTrue(deletedDeck.size() == 0);
    }
}
