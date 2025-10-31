package com.ucamp.greenmap.Kakao;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

    @Component
    public class JwtTokenProvider {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());
        private final Key key;

        public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            this.key = Keys.hmacShaKeyFor(keyBytes);
        }

        public String accessTokenGenerate(Long subject, Date expiredAt) {
            return Jwts.builder()
                    .setSubject(String.valueOf(subject))    //uid
                    .setExpiration(expiredAt)
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();
        }

        public String refreshTokenGenerate(Date expiredAt) {
            return Jwts.builder()
                    .setExpiration(expiredAt)
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();
        }

        public boolean validateToken(String token) {
            try {
                Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        public String getSubject(String token) {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        }

        public Long getMemberId(String token) {
            return Long.valueOf(getSubject(token));
        }

    }
