package org.dasfoo.delern.models;

import com.google.firebase.database.Exclude;

import java.util.List;

/**
 * Created by katarina on 10/11/16.
 */

public class Deck {
    @Exclude
    private String uid;
    private String name;

    public Deck(){

    }

    public Deck(String name) {
        this.name = name;
    }

    @Exclude
    public String getUid() {
        return uid;
    }

    @Exclude
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Deck{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
