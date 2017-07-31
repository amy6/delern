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

import org.dasfoo.delern.models.listeners.OnOperationCompleteListener;
import org.dasfoo.delern.test.FirebaseServerUnitTest;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.cgom/tools/testing">Testing documentation</a>
 */
public class UserTest extends FirebaseServerUnitTest {

    @Test
    public void save_succeeds() throws Exception {
        currentUser().save(new OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                testSucceeded();
            }
        });
    }
}
