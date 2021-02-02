package com.taskagile.web.apis.authenticate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.taskagile.utils.JsonUtils;
import com.taskagile.web.results.ApiResult;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class SimpleAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {

    response.setStatus(HttpStatus.OK.value());
    // response 객체에 인증 성공에 대한 결과 메시지를 추가
    JsonUtils.write(response.getWriter(), ApiResult.message("authenticated"));
  }

}
