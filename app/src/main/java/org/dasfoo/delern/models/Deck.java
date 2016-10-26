package org.dasfoo.delern.models;

import java.util.List;

/**
 * Created by katarina on 10/11/16.
 */

public class Deck {
    private String uid;
    private String name;

    public Deck(){

    }

    public Deck(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
