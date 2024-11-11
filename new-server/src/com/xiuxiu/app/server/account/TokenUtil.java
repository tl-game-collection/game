package com.xiuxiu.app.server.account;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TokenUtil {
    private static final HashMap<String, Object> HEAD = new HashMap<>();

    public static final String KEY_LOGIN_TYPE = "loginType";
    public static final String KEY_USER_NAME= "userName";
    public static final String KEY_USER_PASSWD = "userPasswd";

    static {
        HEAD.put("alg", "HS256");
        HEAD.put("typ", "JWT");
    }

    public static String getToken(int loginType, String userName, String userPasswd, String key) {
        return JWT.create().withHeader(HEAD).withClaim(KEY_LOGIN_TYPE, loginType).withClaim(KEY_USER_NAME, userName).withClaim(KEY_USER_PASSWD, userPasswd).sign(Algorithm.HMAC256(key));
    }

    public static Map<String, String> getInfoByToken(String token, String key) {
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(key)).build();
            jwt = verifier.verify(token);
            Map<String, String> info = new HashMap<>();
            for (Map.Entry<String, Claim> entry : jwt.getClaims().entrySet()) {
                info.put(entry.getKey(), entry.getValue().as(String.class));
            }
            return info;
        } catch (JWTVerificationException e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_MAP;
    }
}
