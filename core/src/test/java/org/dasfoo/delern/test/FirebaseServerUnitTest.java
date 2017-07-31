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

import com.google.api.client.util.Base64;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredential;
import com.google.firebase.auth.GoogleOAuthAccessToken;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.tasks.Task;
import com.google.firebase.tasks.Tasks;

import org.dasfoo.delern.models.User;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.File;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class FirebaseServerUnitTest {

    private static final int PORT = 5533;
    private static final String HOST = "localhost";
    private static final String US_ASCII_ENCODING = "US-ASCII";

    private static String mNode;
    private static String mServer;
    private static String mRules;
    private FirebaseServerRunner mFirebaseServer;

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
                if (f.getName().equals("rules.json")) {
                    mRules = f.getAbsolutePath();
                }
            }
            if (mServer != null && mNode != null && mRules != null) {
                break;
            }
        }
    }

    @Before
    public void startFirebaseServer() throws Exception {
        // Add setVerbose() before start() to get more logs.
        mFirebaseServer = new FirebaseServerRunner(mNode, mServer)
                .setHost(HOST)
                .setPort(String.valueOf(PORT))
                // TODO(dotdoom): once firebase-server is fixed: .setRules(mRules)
                .start();
    }


    private static String createJWT(final String subject, final long issuedAt) throws Exception {
        final JSONObject token = new JSONObject();
        token.put("sub", subject);
        token.put("iat", issuedAt);
        return Base64.encodeBase64String("{}".getBytes(US_ASCII_ENCODING)) + "." +
                Base64.encodeBase64String(token.toString().getBytes(US_ASCII_ENCODING)) + ".";
    }

    public String initializeAndAuth(final String id) throws Exception {
        String userId = id;
        if (id == null) {
            userId = UUID.randomUUID().toString();
        }

        final long currentTime = System.currentTimeMillis();
        final String token = createJWT(userId,
                currentTime / 1000 - TimeUnit.SECONDS.convert(1, TimeUnit.HOURS));

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredential(new FirebaseCredential() {
                    @Override
                    public Task<GoogleOAuthAccessToken> getAccessToken() {
                        return Tasks.forResult(new GoogleOAuthAccessToken(token,
                                currentTime + TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)
                        ));
                    }
                })
                .setDatabaseUrl(new URI("ws", null, HOST, PORT, null, null, null).toString())
                .build();
        if (id == null) {
            FirebaseApp.initializeApp(options);
        } else {
            FirebaseApp.initializeApp(options, id);
        }

        return userId;
    }

    private User mCurrentUser;

    @Before
    public void initializeCurrentUser() throws Exception {
        String userId = initializeAndAuth(null);
        mCurrentUser = new User(FirebaseDatabase.getInstance());
        mCurrentUser.setKey(userId);
        mCurrentUser.setEmail("alice@example.com");
        mCurrentUser.setName("Alice Test");
    }

    protected User currentUser() {
        return mCurrentUser;
    }

    private CountDownLatch mTestLatch;

    @Before
    public void installLatch() {
        mTestLatch = new CountDownLatch(1);
    }

    protected void testSucceeded() {
        mTestLatch.countDown();
    }

    @After
    public void waitLatchAndStopServer() throws Exception {
        assertTrue("Timed out waiting for the test (did you forget to call testSucceeded()?)",
                mTestLatch.await(5, TimeUnit.SECONDS));
        mFirebaseServer.stop();
    }
}
