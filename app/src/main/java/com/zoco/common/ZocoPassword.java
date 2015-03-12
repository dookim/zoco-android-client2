package com.zoco.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by user on 2015-03-08.
 */
public class ZocoPassword {

    public static String createPassword(String str) {
        String pw = "";
        try {
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(str.getBytes());
            byte byteData[] = sh.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            pw = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            pw = null;
        }
        return pw;
    }
}
