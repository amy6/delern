package org.dasfoo.delern.sharedeck;

import org.dasfoo.delern.models.User;

import java.util.Map;

/**
 * Created by katarina on 10/26/17.
 */

public interface IShareDeckView {
    void updateUserAccessInfo(Map<User, String> users);
}
