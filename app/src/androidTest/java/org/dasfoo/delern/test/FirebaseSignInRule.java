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

import android.util.Base64;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class FirebaseSignInRule extends ExternalResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseSignInRule.class);

    private final boolean mAutoSignIn;

    private String serviceAccount;
    private Key serviceAccountKey;

    public FirebaseSignInRule(final boolean autoSignIn) {
        mAutoSignIn = autoSignIn;

        byte[] keyDataBytes;
        try (InputStream keyFileStream = getClass().getClassLoader()
                .getResourceAsStream("firebase-adminsdk.json")) {
            if (keyFileStream == null) {
                throw new IOException("Firebase Admin SDK credentials resource is missing");
            }
            keyDataBytes = new byte[keyFileStream.available()];
            keyFileStream.read(keyDataBytes);
        } catch (IOException e) {
            LOGGER.error("Cannot read Firebase Admin SDK credentials", e);
            return;
        }

        try {
            JSONObject keyData = new JSONObject(new String(keyDataBytes, "UTF-8"));
            serviceAccount = keyData.getString("client_email");
            serviceAccountKey = KeyFactory.getInstance("RSA").generatePrivate(
                    new PKCS8EncodedKeySpec(
                            Base64.decode(keyData.getString("private_key")
                                    .replace("-----BEGIN PRIVATE KEY-----", "")
                                    .replace("-----END PRIVATE KEY-----", ""), Base64.DEFAULT)));
        } catch (UnsupportedEncodingException | JSONException | GeneralSecurityException e) {
            LOGGER.error("Cannot parse Firebase Admin SDK credentials", e);
        }
    }

    @Override
    protected void before() throws Throwable {
        if (mAutoSignIn) {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                if (customSignInAvailable()) {
                    signIn("bob");
                } else {
                    FirebaseAuth.getInstance().signInAnonymously();
                }
            }
        } else {
            FirebaseAuth.getInstance().signOut();
        }
    }

    public boolean customSignInAvailable() {
        return serviceAccount != null && serviceAccountKey != null;
    }

    public String signIn(final String deviceLocalUserId) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.HOUR, 1);
        Date expiresAt = calendar.getTime();

        String email = deviceLocalUserId + "@" + FirebaseInstanceId.getInstance().getId() +
                ".example.com";
        String id = "test-" + email.toLowerCase().replaceAll("[^a-z0-9]", "-");

        FirebaseAuth.getInstance().signInWithCustomToken(Jwts.builder()
                .setIssuer(serviceAccount)
                .setSubject(serviceAccount)
                .setAudience("https://identitytoolkit.googleapis.com/" +
                        "google.identity.identitytoolkit.v1.IdentityToolkit")
                .setIssuedAt(now)
                .setExpiration(expiresAt)
                .claim("uid", id)
                .signWith(SignatureAlgorithm.RS256, serviceAccountKey)
                .compact()).addOnSuccessListener(auth -> {
            auth.getUser().updateEmail(email);
        });

        return email;
    }
}
