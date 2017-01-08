package org.dasfoo.delern.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by katarina on 10/12/16.
 * Class keeps user information.
 */

@SuppressWarnings({"checkstyle:MemberName", "checkstyle:HiddenField"})
public final class User {

    @Exclude
    private static final String USERS = "users";

    private String name;
    private String email;
    private String photoUrl;

    public User(final String name, final String email, final String photoUrl) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    @Exclude
    public static DatabaseReference getFirebaseUserRef() {
        FirebaseUser user = getCurrentUser();
        return FirebaseDatabase.getInstance().getReference()
                .child(USERS)
                .child(user.getUid());
    }

    @Exclude
    public static boolean isSignedIn() {
        FirebaseUser user = getCurrentUser();
        return user != null;
    }

    @Exclude
    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(final String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
