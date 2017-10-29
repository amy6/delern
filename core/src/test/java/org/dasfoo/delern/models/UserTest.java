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

import io.reactivex.ObservableSource;

import static org.junit.Assert.assertTrue;

/**
 * Test for User model.
 */
public class UserTest {

    private User mUser;

    @Rule
    public final FirebaseServerRule mFirebaseServer = new FirebaseServerRule();

    @Before
    public void createUser() throws Exception {
        mUser = mFirebaseServer.signIn();
    }

    @Test
    public void user_savedAndFetched() throws Exception {
        User user = mUser.save().andThen((ObservableSource<User>) observer ->
                mUser.watch(User.class).subscribe(observer)
        ).firstOrError().blockingGet();
        assertTrue(user.getName().startsWith("Bob "));
    }
}
