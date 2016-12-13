package org.dasfoo.delern;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;

import org.dasfoo.delern.models.User;
import org.dasfoo.delern.signin.SignInActivity;
import org.dasfoo.delern.util.LogUtil;

import de.hdodenhof.circleimageview.CircleImageView;

public class DelernMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_INVITE = 1;
    private static final String TAG = LogUtil.tagFor(DelernMainActivity.class);

    public FirebaseAnalytics mFirebaseAnalytics;
    protected GoogleApiClient mGoogleApiClient;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delern_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        configureToolbar();

        if (!User.isSignedIn()) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }

        DelernMainActivityFragment listFragment = new DelernMainActivityFragment();
        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, listFragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        configureProfileInfo(navigationView);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .addApi(AppInvite.API)
                .build();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.delern_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // When you successfully handle a menu item, return true.
        // If you don't handle the menu item, you should call the superclass implementation of
        // onOptionsItemSelected() (the default implementation returns false).
        // https://developer.android.com/guide/topics/ui/menus.html#options-menu
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                FirebaseAuth.getInstance().signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                startActivity(new Intent(this, SignInActivity.class));
                break;
            // TODO(ksheremet): Remove unused code
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    break;
                }
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_import) {
            Log.v(TAG, "Import is not implemented yet");
        } else if (id == R.id.nav_export) {
            Log.v(TAG, "Export is not implemented yet");
        } else if (id == R.id.nav_manage) {
            Log.v(TAG, "Tools is not implemented yet");
        } else if (id == R.id.nav_share) {
            Log.v(TAG, "Share is not implemented yet");
        } else if (id == R.id.nav_send) {
            Log.v(TAG, "Send is not implemented yet");
        } else if (id == R.id.nav_invite) {
            sendInvitation();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sendInvitation() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    private void configureToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    private void configureProfileInfo(NavigationView navigationView) {
        View hView = navigationView.getHeaderView(0);
        CircleImageView fotoUser = (CircleImageView) hView.findViewById(R.id.profile_image);
        TextView userName = (TextView) hView.findViewById(R.id.user_name);
        TextView userEmail = (TextView) hView.findViewById(R.id.user_email);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userName.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());
            Glide.with(DelernMainActivity.this)
                    .load(user.getPhotoUrl())
                    .into(fotoUser);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "sent");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE,
                        payload);
            } else {
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "not sent");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE,
                        payload);
                Toast.makeText(this, R.string.invitation_failed_message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        FirebaseCrash.logcat(Log.ERROR, TAG, connectionResult.getErrorMessage());
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
