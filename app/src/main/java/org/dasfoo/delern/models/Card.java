package org.dasfoo.delern.models;

/**
 * Created by katarina on 10/4/16.
 */

public class Card {
    private String frontSide;
    private String backSide;

    public Card(String frontSide, String backSide) {
        this.frontSide = frontSide;
        this.backSide = backSide;
    }

    public String getFrontSide() {
        return frontSide;
    }

    public String getBackSide() {
        return backSide;
    }

    public void setFrontSide(String frontSide) {
        this.frontSide = frontSide;
    }

    public void setBackSide(String backSide) {
        this.backSide = backSide;
    }
}
