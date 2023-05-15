package com.dvt.joblisting.ultis;

import com.dvt.joblisting.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

public class SendToken {
    private final ObjectMapper objectMapper;
    public SendToken(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }
    public static String getJWTToken(User user) {
        String secret = "duongvantuanduongvantuanduongvantuanduongvantuan";
        Date expirationDate = new Date(System.currentTimeMillis() + 15 * 24 * 60 * 60 * 1000);

        String token = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        return token;
    }
    public static ResponseEntity sendToken(HttpServletResponse response, User user) throws Exception {
        String token = SendToken.getJWTToken(user);
        Instant expirationTime = Instant.now().plus(Duration.ofDays(15));
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(Math.toIntExact(Duration.between(Instant.now(), expirationTime).getSeconds()));
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        response.addCookie(cookie);

        return new ResponseEntity("Gửi token thành công", HttpStatus.OK);
    }
}
