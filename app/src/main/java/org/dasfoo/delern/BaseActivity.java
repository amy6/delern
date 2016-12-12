package org.dasfoo.delern;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.dasfoo.delern.signin.SignInActivity;

/**
 * It has basic implementation for all activities such as toolbar.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public FirebaseAnalytics mFirebaseAnalytics;

    protected GoogleApiClient mGoogleApiClient;

    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        configureToolbar();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    enableToolbarArrow(true);
                } else {
                    enableToolbarArrow(false);
                }
            }
        });

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


    }

    protected abstract int getLayoutResource();

    private void configureToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    protected void enableToolbarArrow(boolean value) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(value);
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

    /**
     * Checks current user in Firebase. If user doesn't exist
     * return false.
     *
     * @return true if user signed in
     */
    protected boolean isUserSignedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }
}
