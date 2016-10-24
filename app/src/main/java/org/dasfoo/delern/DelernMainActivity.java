package org.dasfoo.delern;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;

import org.dasfoo.delern.card.AddNewCardFragment;
import org.dasfoo.delern.card.CardFragment;
import org.dasfoo.delern.controller.FirebaseController;
import org.dasfoo.delern.models.User;
import org.dasfoo.delern.signin.SignInActivity;

public class DelernMainActivity extends AppCompatActivity
        implements CardFragment.OnFragmentInteractionListener,
        GoogleApiClient.OnConnectionFailedListener,
        AddNewCardFragment.OnFragmentInteractionListener {

    public static final String ANONYMOUS = "anonymous";
    private static final String TAG = "DelernMailActivity";
    private static final int REQUEST_INVITE = 1;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String mUsername;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseController firebaseController = FirebaseController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delern_main);
        // By initializing Firebase Analytics it will automatically
        // track the lifecycle of your application throughout user sessions.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Set default username is anonymous.
        mUsername = ANONYMOUS;
        FirebaseUser user = firebaseController.getFirebaseAuth().getCurrentUser();
        if (user == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .addApi(AppInvite.API)
                .build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        DelernMainActivityFragment listFragment = new DelernMainActivityFragment();
        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, listFragment).commit();
        getSupportFragmentManager().addOnBackStackChangedListener(new OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    enableToolbarArrow(true);
                } else {
                    enableToolbarArrow(false);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_delern_main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.invite_menu:
                sendInvitation();
                return true;
            case R.id.sign_out_menu:
                firebaseController.getFirebaseAuth().signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "sent");
                // TODO(ksheremet): move to FirebaseController
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE,
                        payload);
                // Check how many invitations were sent and log.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode,
                        data);
                Log.d(TAG, "Invitations sent: " + ids.length);
            } else {
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "not sent");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE,
                        payload);
                // Sending failed or it was canceled, show failure message to
                // the user
                Log.d(TAG, "Failed to send invitation.");
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        // TODO(ksheremet): add this to errors that crashed apps for crash reports Firebase
        FirebaseCrash.logcat(Log.ERROR, TAG, connectionResult.getErrorMessage());
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void sendInvitation() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    private void enableToolbarArrow(boolean value) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(value);
        getSupportActionBar().setHomeButtonEnabled(value);
    }
}
