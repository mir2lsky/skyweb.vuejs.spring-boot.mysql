package com.taskagile.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** =======================
 *   Spring Security 설정
    ======================= */
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  // 누구나 허용되는 경로를 정의
  private static final String[] PUBLIC = new String[] {
    "/error", "/login", "/logout", "/register", "/api/registrations"
  };

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // === Http 요청에 대한 security 설정 ===
    http
      .authorizeRequests()    //  http 요청에 기반한 접근 제한임을 알림
        .antMatchers(PUBLIC).permitAll()  // 누구나 허용되는 경로 설정
        .anyRequest().authenticated()     // PUBLIC외 다른 요청은 인증된 사용자만 접근 가능
      .and()  // 메소드 호출 체인읠 http 객체로 복원
        //.addFilterAt(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .formLogin()  // App가 form 기반 인증 설정
          .loginPage("/login")  // 로그인 페이지 경로 설정
      .and()
        .logout()     // 로그아웃의 동작을 설정
          .logoutUrl("/logout")   // 로그아웃 경로 설정(default)
          .logoutSuccessUrl("/login?logged-out")  // 로그아웃 후 리다이렉트되는 경로
          //.logoutSuccessHandler(LogoutSuccessHandler())
      .and()
        .csrf().disable()    //Cross-Site Request Forgery(크로스 사이트 요청 위조)기능을 disable
      ;
  }

  @Override
  public void configure(WebSecurity web) {
    // web 요청에 대한 security 설정
    // 아래의 경로에 대해서는 security 처리를 하지 않도록 설정하는 것으로 보임
    web.ignoring().antMatchers("/static/**", "/js/**", "/css/**", "/images/**", "/favicon.ico");
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
