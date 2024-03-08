package com.healthmap.myway.auth;

import com.healthmap.myway.member.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j

// 토큰을 발급하고, 서명위조를 검사하는 객체
public class TokenProvider {

    // 서명할 때 사용하는 비밀키(512비트 이상의 랜덤 문자열)
    @Value("${jwt.secret}")
    private String SECRET_KEY;


    /**
     * Json Web Token을 생성하는 메서드
     * @param memberEntity 토큰의 내용(클레임)에 포함될 유저정보
     * @return 생성된 json을 암호화한 토큰값
     */
    public String createToken(Member memberEntity) {

        // 토큰 만료시간 생성
        Date expiry = Date.from(
                Instant.now().plus(1, ChronoUnit.DAYS)
        );

        // 추가 클레임 정의
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", memberEntity.getEmail());
        claims.put("role", memberEntity.getRole());

        return Jwts.builder()
                // token header에 들어갈 서명
                .signWith(
                        Keys.hmacShaKeyFor(SECRET_KEY.getBytes())
                        , SignatureAlgorithm.HS512
                )
                // token payload에 들어갈 클레임 설정
                .setClaims(claims)                  // 추가 클레임(사용자 지정)은 맨 위에 설정
                .setIssuer("운영자")               // iss : 발급자 정보
                .setIssuedAt(new Date())            // iat : 발급 시간
                .setExpiration(expiry)              // exp : 만료시간
                .setSubject(memberEntity.getEmail())   // sub : 토큰을 식별할 수 있는 주요데이터
                .compact();
    }

    /**
     * 클라이언트가 전송한 토큰을 디코딩하여 토큰의 위조여부를 확인
     * 토큰을 json으로 파싱해서 클레임(토큰정보)를 리턴
     * @param token
     * @return - 토큰 안에있는 인증된 유저정보를 반환
     */
    public TokenUserInfo validateAndGetTokenUserInfo(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        log.info("claims: {}", claims);

        return TokenUserInfo.builder()
                .email(claims.get("email", String.class))
                .role(Member.Role.valueOf(claims.get("role", String.class)))
                .build();
    }

}
