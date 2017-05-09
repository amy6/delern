package org.dasfoo.delern.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import org.dasfoo.delern.listeners.AbstractOnFbOperationCompleteListener;


/**
 * Created by katarina on 10/12/16.
 * Class keeps user information.
 */

@SuppressWarnings({"checkstyle:MemberName", "checkstyle:HiddenField"})
public final class User {
    /**
     * User data field name in Firebase.
     */
    @Exclude
    public static final String NAME = "name";

    /**
     * User data field email in Firebase.
     */
    @Exclude
    public static final String EMAIL = "email";

    /**
     * User data field photoUrl in Firebase.
     */
    @Exclude
    public static final String PHOTO_URL = "photoUrl";

    @Exclude
    private static final String USERS = "users";

    private String name;
    private String email;
    private String photoUrl;

    /**
     * Create user instance using name, email, photo url of user.
     *
     * @param name     name of user.
     * @param email    email of user.
     * @param photoUrl photo url of user.
     */
    public User(final String name, final String email, final String photoUrl) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    /**
     * Gets database reference in Firebase to current user.
     *
     * @return database reference to current user.
     */
    @Exclude
    public static DatabaseReference getFirebaseUserRef() {
        FirebaseUser user = getCurrentUser();
        return FirebaseDatabase.getInstance().getReference()
                .child(USERS)
                .child(user.getUid());
    }

    /**
     * Writes user data to firebase.
     *
     * @param user user data.
     * @param listener handler when operation was completed.
     */
    @Exclude
    public static void writeUser(final User user,
                                 final AbstractOnFbOperationCompleteListener<String> listener) {
        User.getFirebaseUserRef().setValue(user).addOnCompleteListener(listener);
    }

    /**
     * Checks whether user is signed in.
     * It uses Firebase Auth to check whether user is signed in.
     *
     * @return true if user is signed in, false if not.
     */
    @Exclude
    public static boolean isSignedIn() {
        FirebaseUser user = getCurrentUser();
        return user != null;
    }

    /**
     * Gets current user using FirebaseAuth.
     *
     * @return returns current user.
     */
    @Exclude
    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Getter for name of user.
     *
     * @return name of user.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name of user.
     *
     * @param name name of user.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Getter of email of user.
     *
     * @return email of user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter for email of user.
     *
     * @param email email of user.
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Getter for photo url of user.
     *
     * @return photo url of user.
     */
    public String getPhotoUrl() {
        return photoUrl;
    }

    /**
     * Setter of photo url of user.
     *
     * @param photoUrl photo url.
     */
    public void setPhotoUrl(final String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }
}
