package com.mv2studio.tswp.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;

public abstract class CommonUtils {

	/**
	 * hash the password, to send it in secure form
	 * @param password pass in raw form
	 * @return password in hashed form
	 */
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
	
	/**
	 * remove accents from string. For example "čšť" transforms to "cst"
	 * @param string string with accents
	 * @return string without accents
	 */
	public static String removeAccents(String string){
		string = Normalizer.normalize(string, Normalizer.Form.NFD);
		string = string.replaceAll("[^\\p{ASCII}]", "");
		return string;
	}
}
