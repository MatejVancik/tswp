package com.mv2studio.tswp.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class CommonUtils {

	public static String getHashedString(String password) {
        MessageDigest md;
        String ret = "";
        try {
            md = MessageDigest.getInstance("SHA-512");

            md.update(password.getBytes());
            byte[] mb = md.digest();
            for (int i = 0; i < mb.length; i++) {
                byte temp = mb[i];
                String s = Integer.toHexString(new Byte(temp));
                while (s.length() < 2) {
                    s = "0" + s;
                }
                s = s.substring(s.length() - 2);
                ret += s;
            }
            System.out.println(ret.length());
            System.out.println("CRYPTO: " + ret);

        } catch (NoSuchAlgorithmException e) {
            System.out.println("ERROR: " + e.getMessage());
            return null;
        }
        return ret;
    }
	
}
