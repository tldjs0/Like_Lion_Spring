package com.example.demo.security;

import com.example.demo.enums.RoleType;
import com.example.demo.exception.HandleJwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtUtility {
    private final SecretKey secretKey;

    private static final long expirationTime = 1000 * 60 * 60;
    private final ServerProperties serverProperties;

    public JwtUtil(@Value("${jwt.base64Secret}") String base64Secret, ServerProperties serverProperties){
        byte[] decodedkey = Base64.getDecoder().decode(base64Secret);
        this.secretKey = Keys.hmacShaKeyFor(decodedkey);
        this.serverProperties = serverProperties;
    }

    public String generateJwt(String userId, String username, RoleType roleType){

        Instant now = Instant.now();

        return Jwts.builder()

                .setSubject(userId)

                .claim("name", username)
                .claim("role",roleType.name())

                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationTime)))

                .signWith(SignatureAlgorithm.HS256, secretKey)

                .compact();
    }

    public boolean validateJwt(String jwt){
        try{
            if(jwt.startsWith("Bearer ")) {
                jwt = jwt.substring(7);
            }

            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwt);


            return true;
        } catch (ExpiredJwtException e){

            throw new HandleJwtException("만요된 JWT");
        } catch (UnsupportedJwtException e){

            throw new HandleJwtException("지원되지 않는 JWT 형식");
        } catch (MalformedJwtException e){

            throw new HandleJwtException("손상된 JWT");
        } catch (SecurityException e){

            throw new HandleJwtException("서명이 올바르지 않은 JWT");
        } catch (IllegalArgumentException e){

            throw new HandleJwtException("JWT가 NULL이거나 빈 문자열임");
        } catch (JwtException e){

            throw new HandleJwtException("기타 JWT관련 예외");
        }
    }

    public Claims getClaimsFromJwt(String jwt){

        String noneBearerJwt = jwt;

        if(jwt.startsWith("Bearer ")){
            noneBearerJwt = jwt.substring(7);
        }

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(noneBearerJwt)
                .getBody();
    }
}
