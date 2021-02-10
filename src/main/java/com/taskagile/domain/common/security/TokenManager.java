package com.taskagile.domain.common.security;

import com.taskagile.domain.model.user.UserId;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

/**
 * 웹소켓 연결에 대한 인증을 수행하기 위한 JWT 실시간 토큰을 생성하고
 * 또한 검증하는 기능을 수행
 */
@Component
public class TokenManager {

  private Key secretKey;

  // application.properties에 추가되는 비밀키를 TokenManager에 주입
  public TokenManager(@Value("${app.token-secret-key}") String secretKey) {
    this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
  }

  /**
   * Generate a JWT with user's id as its subject
   * UserId를 기반으로 JWT문자열를 생성
   *
   * @param userId the id of the user
   * @return a JWT value
   */
  public String jwt(UserId userId) {
    return Jwts.builder()
      .setSubject(String.valueOf(userId.value()))
      .signWith(secretKey).compact();
  }

  /**
   * Get user id out of a JWT value
   * 클라이언트로부터 받은 토큰을 감증
   *
   * @param jws the jwt string
   * @return user id
   */
  public UserId verifyJwt(String jws) {
    String userIdValue = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jws).getBody().getSubject();
    return new UserId(Long.valueOf(userIdValue));
  }
}
