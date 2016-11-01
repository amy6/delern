package org.dasfoo.delern.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by katarina on 10/30/16.
 */

public class View {
    private String vId;
    private Object time;
    private Object repeat;
    private String reply;
    private String level;

    public View() {
        this.reply = Reply.DONT_KNOW.name();
        this.time = new HashMap<>();
        this.repeat = new HashMap<>();
        this.time = ServerValue.TIMESTAMP;
        this.repeat = ServerValue.TIMESTAMP;
        this.level = Level.L0.name();
    }

    @Exclude
    public long getTimeLong(){
        return (long)time;
    }

    @Exclude
    public long getRepeatLong() {
        return (long)repeat;
    }

    public String getvId() {
        return vId;
    }

    public void setvId(String vId) {
        this.vId = vId;
    }

    public Object getTime() {
        return time;
    }

    public void setTime(Object time) {
        this.time = time;
    }

    public Object getRepeat() {
        return repeat;
    }

    public void setRepeat(Object repeat) {
        this.repeat = repeat;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
