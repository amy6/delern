package org.dasfoo.delern.models;

import com.google.api.client.util.Base64;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredential;
import com.google.firebase.auth.GoogleOAuthAccessToken;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.tasks.Task;
import com.google.firebase.tasks.Tasks;

import org.dasfoo.delern.models.listeners.OnOperationCompleteListener;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    final User currentUser = new User();

    @Before
    public void setUp() throws Exception {
        final long currentTime = System.currentTimeMillis();
        final String userId = "iAmUniqueUserId";
        final JSONObject token = new JSONObject();
        token.put("sub", userId);
        token.put("iat", currentTime / 1000 - TimeUnit.SECONDS.convert(1, TimeUnit.HOURS));
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredential(new FirebaseCredential() {
                    @Override
                    public Task<GoogleOAuthAccessToken> getAccessToken() {
                        return Tasks.forResult(new GoogleOAuthAccessToken(
                                Base64.encodeBase64String("{}".getBytes()) + "." +
                                Base64.encodeBase64String(token.toString().getBytes()) + ".",
                                currentTime + TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)
                        ));
                    }
                })
                .setDatabaseUrl("ws://localhost:5533/")
                .build();
        FirebaseApp.initializeApp(options);

        User.initializeDatabase(false);

        final CountDownLatch connect = new CountDownLatch(1);
        FirebaseDatabase.getInstance().getReference(".info/connected").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(Boolean.class)) {
                            connect.countDown();
                        }
                    }

                    @Override
                    public void onCancelled(final DatabaseError databaseError) {
                        // TODO(dotdoom): fail!
                    }
                });

        assertTrue("Connect to the database", connect.await(5, TimeUnit.SECONDS));

        currentUser.setKey(userId);
    }

    @Test
    public void user_isSaved() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);
        currentUser.setName("Test");
        currentUser.setEmail("test@example.com");
        currentUser.save(new OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                lock.countDown();
            }
        });
        assertTrue("Save User", lock.await(5, TimeUnit.SECONDS));
    }
}
