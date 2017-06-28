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

package org.dasfoo.delern.signin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import org.dasfoo.delern.BuildConfig;
import org.dasfoo.delern.DelernMainActivity;
import org.dasfoo.delern.R;
import org.dasfoo.delern.models.User;
import org.dasfoo.delern.models.listeners.AbstractDataAvailableListener;
import org.dasfoo.delern.util.LogUtil;

/**
 * Activity that perform SignIn for user using GoogleApiClient and FirebaseAuth.
 * Sign In is implemented using Google.
 */
public class SignInActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    /**
     * Class information for logging.
     */
    private static final String TAG = LogUtil.tagFor(SignInActivity.class);
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;

    /**
     * Method starts SignInActivity.
     *
     * @param context context of Activity from what method was called.
     */
    public static void startActivity(final Context context) {
        // Per https://goo.gl/qHTbjw and https://goo.gl/rnD2g3.
        Intent signInIntent = new Intent(context, SignInActivity.class);
        signInIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(signInIntent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);

        // TODO(ksheremet): Move to instrumented flavour package
        if (BuildConfig.ENABLE_ANONYMOUS_SIGNIN) {
            Log.w(TAG, "Running from an instrumented test: forcing anonymous sign in");

            User.signIn(null, new AbstractDataAvailableListener<User>(this) {
                @Override
                public void onData(@Nullable final User data) {
                    Intent intent = new Intent(SignInActivity.this,
                            DelernMainActivity.class);
                    intent.putExtra(DelernMainActivity.USER, data);
                    startActivity(intent);
                    finish();
                }
            });
            return;
        }

        // Assign fields
        SignInButton mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        // Set click listeners
        mSignInButton.setOnClickListener(this);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(
                        this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            default:
                Log.d(TAG, "Button is not implemented yet");
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                LogUtil.error(TAG, "Google Sign In activity failed: " + result.getStatus());
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.i(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        User.signIn(credential, new AbstractDataAvailableListener<User>(this) {
            @Override
            public void onData(@Nullable final User data) {
                Intent intent = new Intent(SignInActivity.this,
                        DelernMainActivity.class);
                intent.putExtra(DelernMainActivity.USER, data);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        LogUtil.error(TAG, "Google API is not available: " + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
