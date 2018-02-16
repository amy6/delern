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


import android.content.Intent;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests for ParcelableUser.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ParcelableUserTest {

    private User mUser;

    @Before
    public void setupParam() {
        mUser = new User(null);
        mUser.setKey("user");
        mUser.setName("Bob");
    }

    @Test
    public void fetchParcelableUser() {
        String paramName = "user";
        Intent intent = new Intent();
        ParcelableUser parcelableUser = new ParcelableUser(mUser);
        intent.putExtra(paramName, parcelableUser);
        User fetchedUser = ParcelableUser.get(intent.getParcelableExtra(paramName));
        assertEquals(mUser, fetchedUser);
    }

    @Test
    public void fetchNotEqualUser() {
        String paramName = "user";
        Intent intent = new Intent();
        ParcelableUser parcelableUser = new ParcelableUser(mUser);
        intent.putExtra(paramName, parcelableUser);
        User fetchedUser = ParcelableUser.get(intent.getParcelableExtra(paramName));

        User user = new User(null);
        user.setName("New");
        user.setKey("kew");
        Intent newIntent = new Intent();
        ParcelableUser newParcelableUser = new ParcelableUser(user);
        newIntent.putExtra(paramName, newParcelableUser);
        User newFetchedUser = ParcelableUser.get(newIntent.getParcelableExtra(paramName));
        assertNotEquals(fetchedUser, newFetchedUser);
    }
}
