package com.by.tsgl.util;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.by.tsgl.bean.BasicUser;
import com.by.tsgl.bean.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.auth0.jwt.JWT;

/**
 * @author leibaoyu
 * @version 1.0
 * @date 2021/11/13 15:15
 */
public class TokenUtil {
    private static final long EXPIRE_TIME= 10*60*60*1000;
    private static final String TOKEN_SECRET="txdy";  //密钥盐
    /**
     * @description 登陆的时候自动生成token串
     * @author leibaoyu
     * @date 2021/11/13 16:41
     * @param account
     * @param password
     * @return java.lang.String
     * @throws
     */
    public static String sign(String account,String password){
        String token = null;
        try {
            Date expiresAt = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            token = JWT.create()
                    .withIssuer("auth0")
                    .withClaim("account", account)
                    .withClaim("password",password)
                    .withExpiresAt(expiresAt)
                    // 使用了HMAC256加密算法。
                    .sign(Algorithm.HMAC256(TOKEN_SECRET));
        } catch (Exception e){
            e.printStackTrace();
        }
        return token;
    }

    /**
     * @description 签名验证
     * @author leibaoyu
     * @date 2021/11/13 16:41
     * @param token
     * @return boolean
     * @throws
     */
    public static boolean verify(String token){
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("auth0").build();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DecodedJWT jwt = verifier.verify(token);
            System.out.println("认证通过：");
            System.out.println("account: " + jwt.getClaim("account").asString());
            System.out.println("过期时间：" + sdf.format(jwt.getExpiresAt()));
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
