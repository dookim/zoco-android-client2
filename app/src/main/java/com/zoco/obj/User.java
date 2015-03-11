package com.zoco.obj;

/**
 * Created by dookim on 2/8/15.
 */
public class User {

    public String email;
    public String nickname;
    public String provider;
    public String univ;
    public String password;
    public String id;


    /**
     * @param nickname
     * in order to check whether client is registered or not
     */
    public User(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @param nickname
     * in order to login, if client login succeed, client will receive "user object"
     * u can use this object using gson
     * if fail, client will receive "cannot login" string
     */
    public User(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }

    /**
     *
     * @param email
     * @param nickname
     * @param provider
     * @param univ
     * @param password
     * in order to register user to server, client should send this information below
     * whether client is registered or not, server always send "success"
     */
    public User(String email, String nickname, String provider, String univ, String password) {
        this.email = email;
        this.nickname = nickname;
        this.provider = provider;
        this.univ = univ;
        this.password = password;
    }




}
