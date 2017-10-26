package org.dasfoo.delern.sharedeck;

import org.dasfoo.delern.models.Deck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by katarina on 10/25/17.
 */

public class ShareDeckActivityPresenter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShareDeckActivityPresenter.class);
    private IShareDeckView mView;
    private Deck mDeck;

    public ShareDeckActivityPresenter(final IShareDeckView view, final Deck deck) {
        this.mView = view;
        this.mDeck = deck;
    }

    /*//TODO(ksheremet): Refactor.
    public void getUsersUsingDeck() {
        ArrayList<String> ids;
        Map<User, String> users = new HashMap<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("deck_access")
                .child(mDeck.getKey());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    String uid = data.getKey();
                    String access = data.getValue(String.class);
                    LOGGER.debug("User id" + uid);
                    LOGGER.debug("User access" + access);
                    User user = getUserById(uid);

                }
                LOGGER.debug("Users fetched:", users);
                mView.updateUserAccessInfo(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //TODO: move to model
    private User getUserById(String uid) {
        DatabaseReference userReference = FirebaseDatabase.getInstance()
                .getReference("users").child(uid);
        LOGGER.debug("User inf ref" + userReference);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                LOGGER.debug("Fetched User" + user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return User.
    }*/
}
