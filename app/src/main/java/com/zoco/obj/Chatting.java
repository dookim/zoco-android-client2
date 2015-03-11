package com.zoco.obj;

/**
 * Created by dookim on 2/24/15.
 */
public class Chatting {
    public String id;
    public String text;
    public Who who;

    public enum Who {
        me, opposite;
    }



}
