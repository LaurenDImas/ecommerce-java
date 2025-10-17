package com.fastcampus.ecommerce.service;

import com.fastcampus.ecommerce.common.DateUtil;
import com.fastcampus.ecommerce.config.JwtSecretConfig;
import com.fastcampus.ecommerce.model.UserInfo;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final JwtSecretConfig jwtSecretConfig;
    private final SecretKey signKey;

    @Override
    public String generateToken(UserInfo userInfo) {
        LocalDateTime expiration = LocalDateTime.now().plus(jwtSecretConfig.getJwtExpirationTime());
        Date expirationDate = DateUtil.convertLocalDateTimeToDate(expiration);

        return Jwts.builder()
                .subject(userInfo.getUsername())
                .issuedAt(new Date())
                .expiration(expirationDate)
                .signWith(signKey)
                .compact();

    }

    @Override
    public boolean validateToken(String token) {
        try {
            JwtParser parser = Jwts.parser()
                    .verifyWith(signKey)
                    .build();

            parser.parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        JwtParser parser = Jwts.parser()
                .verifyWith(signKey)
                .build();

        return parser.parseSignedClaims(token).getPayload().getSubject();
    }
}
