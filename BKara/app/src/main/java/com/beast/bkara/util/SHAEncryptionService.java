package com.beast.bkara.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by VINH on 5/2/2016.
 */
public class SHAEncryptionService {
    private static SHAEncryptionService instance;

    private SHAEncryptionService(){}

    public synchronized String encrypt(String plaintext) {
        MessageDigest md = null;
        try
        {
            md = MessageDigest.getInstance("SHA-1"); //step 2
        }
        catch(NoSuchAlgorithmException e)
        {

        }

        try
        {
            md.update(plaintext.getBytes("UTF-8")); //step 3
        }
        catch(UnsupportedEncodingException e)
        {

        }

        byte raw[] = md.digest(); //step 4

        String hash = Base64.encodeToString(raw,Base64.DEFAULT);//  encode(raw); //step 5
        return hash; //step 6
    }

    public static synchronized SHAEncryptionService getInstance() //step 1
    {
        if(instance == null)
        {
            instance = new SHAEncryptionService();
        }
        return instance;
    }
}
