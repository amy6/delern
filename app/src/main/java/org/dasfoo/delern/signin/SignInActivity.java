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
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import org.dasfoo.delern.R;
import org.dasfoo.delern.listdecks.DelernMainActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity that perform SignIn for user using GoogleApiClient and FirebaseAuth.
 * Sign In is implemented using Google.
 */
public class SignInActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    /**
     * Class information for logging.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SignInActivity.class);
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

        FirebaseAuth.getInstance().addAuthStateListener(auth -> {
            org.dasfoo.delern.models.Auth.setCurrentUser(auth.getCurrentUser());
            if (org.dasfoo.delern.models.Auth.isSignedIn()) {
                DelernMainActivity.startActivity(this,
                        org.dasfoo.delern.models.Auth.getCurrentUser());
                finish();
            }
        });

        ButterKnife.bind(this);
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
                FirebaseAuth.getInstance().signInWithCredential(GoogleAuthProvider.getCredential(
                        result.getSignInAccount().getIdToken(), null));
            } else {
                LOGGER.error("Google Sign In activity failed: {}", result.getStatus());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        LOGGER.error("Google API is not available: {}", connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.sign_in_button)
    /* default */ void signInClick() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
