package org.dasfoo.delern.models;

/**
 * Created by katarina on 10/4/16.
 */

public class Card {
    private String uid;
    private String backside;
    private String frontside;

    public Card(){

    }

    public Card(String backSide, String frontSide) {
        this.backside = backSide;
        this.frontside = frontSide;
    }

    public Card(String uid, String backSide, String frontSide) {
        this.uid = uid;
        this.backside = backSide;
        this.frontside = frontSide;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBackSide() {
        return backside;
    }

    public void setBackSide(String backSide) {
        this.backside = backSide;
    }

    public String getFrontSide() {
        return frontside;
    }

    public void setFrontSide(String frontSide) {
        this.frontside = frontSide;
    }
}
