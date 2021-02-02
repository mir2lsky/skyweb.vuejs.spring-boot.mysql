package com.taskagile.web.apis.authenticate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.taskagile.utils.JsonUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

  public AuthenticationFilter() {
    // "/api/authentications" 결로로 오는 HTTP POST요청을 처리하도록 설정
    super(new AntPathRequestMatcher("/api/authentications", "POST"));
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, IOException, ServletException {

    log.debug("Processing login request");

    // request 내용을 읽어서 requestBody를 만들고 LoginRequest 객체로 변환처리
    // request의 내용은 JOSN 형식일 것으로 전제
    String requestBody = IOUtils.toString(request.getReader());
    LoginRequest loginRequest = JsonUtils.toObject(requestBody, LoginRequest.class);

    // LoginRequest가 유효하지 않으면 예외를 던진다. 여기서 던져진 예외는 결국
    // AuthenticationFailureHandler에 의해 처리됨
    if (loginRequest == null || loginRequest.isValid()) {
      throw new InsufficientAuthenticationException("Invalid authentication request");
    }

    // LoginRequest가 유효하면 인증 토큰을 생성
    UsernamePasswordAuthenticationToken token =
      new UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password);

    // AuthenticationManagers는 토근에 대한 실제 인증을 수행
    return this.getAuthenticationManager().authenticate(token);
  }

  /**
   * JSON 문자열을 파싱하는데 사용하는 필터 내의 내부 클래스
   */
  static class LoginRequest {
    private String username;
    private String password;

    public boolean isValid() {
      return StringUtils.isBlank(username) || StringUtils.isBlank(password);
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }

}
