package com.actioncrafter.core;

import com.pusher.client.AuthorizationFailureException;
import com.pusher.client.Authorizer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class PrivateKeyAuthorizer implements Authorizer
{

    private final String mKey;
    private final String mSecret;
    private final HMAC mHMAC;

    public PrivateKeyAuthorizer(String key, String secret)
    {
        mKey = key;
        mSecret = secret;
        mHMAC = new HMAC();
    }


    @Override
    public String authorize(String channelName, String socketId) throws AuthorizationFailureException
    {
        String stringToSign = socketId + ":" + channelName;
        String signature = mHMAC.hmacDigest(stringToSign, mSecret, "HmacSHA256");

        String auth =  "{\"auth\":\"" + mKey + ":" + signature + "\"}";
        return auth;
    }


    // copied from http://www.supermind.org/blog/1102/generating-hmac-md5-sha1-sha256-etc-in-java
    public class HMAC {

        public String hmacDigest(String msg, String keyString, String algo) {
            String digest = null;
            try {
                SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), algo);
                Mac mac = Mac.getInstance(algo);
                mac.init(key);

                byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));

                StringBuffer hash = new StringBuffer();
                for (int i = 0; i < bytes.length; i++) {
                    String hex = Integer.toHexString(0xFF & bytes[i]);
                    if (hex.length() == 1) {
                        hash.append('0');
                    }
                    hash.append(hex);
                }
                digest = hash.toString();
            } catch (UnsupportedEncodingException e) {
            } catch (InvalidKeyException e) {
            } catch (NoSuchAlgorithmException e) {
            }
            return digest;
        }
    }
}
