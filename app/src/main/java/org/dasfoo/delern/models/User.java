package org.dasfoo.delern.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by katarina on 10/12/16.
 * Class keeps user information.
 */

public final class User {

    @Exclude
    private static final String USERS = "users";

    private String name;
    private String email;
    private String photoUrl;

    public User(String name, String email, String photoUrl) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    /**
     * Firebase apps automatically handle temporary network interruptions. Cached data will
     * still be available while offline and your writes will be resent when network connectivity is
     * recovered. Enabling disk persistence allows our app to also keep all of its state even after
     * an app restart.
     */
    @Exclude
    public static void enableDiskPersistence() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    @Exclude
    public static DatabaseReference getFirebaseUsersRef() {
        return FirebaseDatabase.getInstance().getReference().child(USERS);
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
