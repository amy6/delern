package org.dasfoo.delern.models;

import java.util.List;

/**
 * Created by katarina on 10/11/16.
 */

public class Desktop {
    private String name;
    private List<Card> cardList;

    public Desktop(){

    }

    public Desktop(String name, List<Card> cardList) {
        this.name = name;
        this.cardList = cardList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Card> getCardList() {
        return cardList;
    }

    public void setCardList(List<Card> cardList) {
        this.cardList = cardList;
    }
}
