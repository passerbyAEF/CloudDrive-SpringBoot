package com.clouddrive.common.core.util;

import com.clouddrive.common.core.constant.SecurityConstants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    //超时时间（30天
    static final long EXPIRE = 2592000;

    static final byte[] APP_SECRET = "cloud_drive_1234567890987654321_lkfjaslfhlhhzcklvjhlagslfkslhlhc".getBytes(StandardCharsets.UTF_8);


    public static String createJwt(Map<String, String> body) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .setClaims(body)
                .signWith(Keys.hmacShaKeyFor(APP_SECRET), SignatureAlgorithm.HS256)
                .compact();
    }

    public static Jws<Claims> decodeJwt(String jwtString) throws JwtException {
        if (!StringUtils.hasLength(jwtString)) {
            throw new JwtException("空值！");
        }
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(APP_SECRET))
                .build().parseClaimsJws(jwtString);
    }

    public static String getID(String jwtString) {
        return getValue(jwtString, SecurityConstants.USER_ID);
    }

    public static String getUUID(String jwtString) {
        return getValue(jwtString, SecurityConstants.USER_UUID);
    }

    public static String getUserName(String jwtString) {
        return getValue(jwtString, SecurityConstants.USER_NAME);
    }

    public static String getValue(String jwtString, String key) {
        Jws<Claims> claimsJws = decodeJwt(jwtString);
        return claimsJws.getBody().get(key).toString();
    }
}
