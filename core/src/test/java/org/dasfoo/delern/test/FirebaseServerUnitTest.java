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

package org.dasfoo.delern.test;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredential;
import com.google.firebase.auth.GoogleOAuthAccessToken;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.tasks.Task;
import com.google.firebase.tasks.Tasks;

import org.dasfoo.delern.models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.jsonwebtoken.Jwts;

import static org.junit.Assert.assertTrue;

public class FirebaseServerUnitTest {

    private static final int PORT = 5533;
    private static final String HOST = "localhost";

    private static String mNode;
    private static String mServer;
    private static String mRules;

    private FirebaseServerRunner mFirebaseServer;
    private CountDownLatch mTestLatch;

    @BeforeClass
    public static void findDependencies() {
        findDependencies(new File(System.getProperty("user.dir")));
    }

    private static void findDependencies(File directory) {
        for (File f : directory.listFiles()) {
            if (f.isDirectory()) {
                findDependencies(f);
            } else {
                if (f.getName().equals("firebase-server") && f.canExecute()) {
                    mServer = f.getAbsolutePath();
                }
                if (f.getName().equals("node") && f.canExecute()) {
                    mNode = f.getAbsolutePath();
                }
                if (f.getName().equals("delern-rules.json")) {
                    mRules = f.getAbsolutePath();
                }
            }
            if (mServer != null && mNode != null && mRules != null) {
                break;
            }
        }
    }

    @Before
    public void createLatchAndStartServer() throws Exception {
        // Add setVerbose() before start() to get more logs.
        mFirebaseServer = new FirebaseServerRunner(mNode, mServer)
                .setHost(HOST)
                .setPort(String.valueOf(PORT))
                .setRules(mRules)
                .start();
        mTestLatch = new CountDownLatch(1);
    }

    protected void testSucceeded() {
        mTestLatch.countDown();
    }

    @After
    public void waitLatchAndStopServer() throws Exception {
        try {
            assertTrue("Timed out waiting for the test (did you forget to call testSucceeded()?)",
                    mTestLatch.await(5, TimeUnit.SECONDS));
            for (FirebaseApp app : FirebaseApp.getApps()) {
                app.delete();
            }
        } finally {
            mFirebaseServer.stop();
        }
    }

    public User signIn() throws Exception {
        String userId = UUID.randomUUID().toString();

        final String token = Jwts.builder().setSubject(userId).setIssuedAt(new Date()).compact();
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredential(new FirebaseCredential() {
                    @Override
                    public Task<GoogleOAuthAccessToken> getAccessToken() {
                        return Tasks.forResult(new GoogleOAuthAccessToken(token,
                                System.currentTimeMillis() +
                                        TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)
                        ));
                    }
                })
                .setDatabaseUrl(new URI("ws", null, HOST, PORT, null, null, null).toString())
                .build();

        User user = new User(FirebaseDatabase.getInstance(FirebaseApp.initializeApp(options,
                userId)));
        user.setKey(userId);
        user.setName("Bob " + userId);
        user.setEmail("bob-" + userId + "@example.com");
        return user;
    }
}
