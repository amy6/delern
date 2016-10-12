package org.dasfoo.delern.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by katarina on 10/4/16.
 */

public final class DBListTest {

    public static DBListTest instansce;
    private List<List<Card>> cardsList = new ArrayList<List<Card>>(5);
    private List<String> topicList = new ArrayList<String>(5);

    public static DBListTest newInstance() {

        if (instansce == null) {
            instansce = new DBListTest();
        }

        return instansce;
    }

    private DBListTest() {
        List<Card> cards1 = new ArrayList<Card>(5);
        cards1.add(new Card("die Mutter","mama"));
        cards1.add(new Card("der Vater ","papa"));
        cards1.add(new Card("der Bruder","brat"));
        cards1.add(new Card("das Schwester","sestrs"));
        cardsList.add(cards1);

        List<Card> cards2 = new ArrayList<Card>(5);
        cards2.add(new Card("mother","mama"));
        cards2.add(new Card("father ","papa"));
        cards2.add(new Card("brother","brat"));
        cardsList.add(cards2);

        List<Card> cards3 = new ArrayList<Card>(5);
        cards3.add(new Card("Queue","Goog structure. FIFO"));
        cards3.add(new Card("QuickSort ","Sorting algorithm"));
        cards3.add(new Card("Big 0 notations","for complexity of algorithms"));
        cardsList.add(cards3);

        List<Card> cards4 = new ArrayList<Card>(5);
        cards4.add(new Card("My card2","Something1"));
        cards4.add(new Card("My card3 ","Something3"));
        cards4.add(new Card("My card4","Something4"));
        cardsList.add(cards4);

        Log.v("ListSIze", String.valueOf(cardsList.size()));

        topicList.add("Deutsch");
        topicList.add("English");
        topicList.add("Algorithms");
        topicList.add("My cards");
    }

    public List<Card> getCardsById(int i) {
        List<Card> list = cardsList.get(i);
        Log.v("ListResult", list.toString());
        return list;
    }

    public List<String> getTopicList() {
        return topicList;
    }

    public void addNewTopic(String topic) {
        topicList.add(topic);
    }
}
