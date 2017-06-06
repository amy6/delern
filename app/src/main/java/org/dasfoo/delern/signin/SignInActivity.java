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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.dasfoo.delern.BuildConfig;
import org.dasfoo.delern.DelernMainActivity;
import org.dasfoo.delern.R;
import org.dasfoo.delern.listeners.OnFbOperationCompleteListener;
import org.dasfoo.delern.models.User;
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
    private final Context mContext = this;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);

        // AuthStateListener that responds to changes in the user's sign-in state
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                FirebaseUser user = User.getCurrentUser();
                if (user == null) {
                    // User is signed out
                    Log.i(TAG, "onAuthStateChanged:signed_out");
                } else {
                    Log.i(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    User changedUser = new User(user.getDisplayName(), user.getEmail(), null);
                    if (user.getPhotoUrl() != null) {
                        changedUser.setPhotoUrl(user.getPhotoUrl().toString());
                    }
                    User.writeUser(changedUser,
                            new OnFbOperationCompleteListener(TAG, mContext) {
                                @Override
                                public void onOperationSuccess() {
                                    Log.i(TAG, "Writing new  user to FB  was successful");
                                }
                            });
                }
            }
        };

        // TODO(ksheremet): Move to instrumented flavour package
        if (BuildConfig.ENABLE_ANONYMOUS_SIGNIN) {
            // Force logging by using Log.e because ProGuard removes Log.w.
            Log.e(TAG, "Running from an instrumented test: forcing anonymous sign in");
            FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(this,
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Log.e(TAG, task.toString());
                                return;
                            }
                                final User changedUser = new User("anonymous",
                                        "instrumented.test@example.com",
                                        "http://example.com/anonymous");
                                User.writeUser(changedUser,
                                        new OnFbOperationCompleteListener(TAG, mContext) {
                                            @Override
                                            public void onOperationSuccess() {
                                                startActivity(new Intent(SignInActivity.this,
                                                        DelernMainActivity.class));
                                                finish();
                                            }
                                        });
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
                // Google Sign In failed
                Log.e(TAG, "Google Sign In failed:" + result.getStatus());
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.i(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        Log.i(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            /*
                                The following lines will add PII to crash reports.
                                Consider privacy issues before uncommenting them.
                                Crashlytics.setUserIdentifier(acct.getId());
                                Crashlytics.setUserEmail(acct.getEmail());
                                Crashlytics.setUserName(acct.getDisplayName());
                            */
                            startActivity(new Intent(SignInActivity.this,
                                    DelernMainActivity.class));
                            finish();
                        } else {
                            Log.e(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
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
        Log.e(TAG, "Google API is not available:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
