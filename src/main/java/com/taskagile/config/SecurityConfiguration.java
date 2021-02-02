package com.taskagile.config;

import com.taskagile.web.apis.authenticate.AuthenticationFilter;
import com.taskagile.web.apis.authenticate.SimpleAuthenticationFailureHandler;
import com.taskagile.web.apis.authenticate.SimpleAuthenticationSuccessHandler;
import com.taskagile.web.apis.authenticate.SimpleLogoutSuccessHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/** =======================
 *   Spring Security 설정
 *  ======================= */
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
      .authorizeRequests()                //  http 요청에 기반한 접근 제한임을 알림
        .antMatchers(PUBLIC).permitAll()  // 누구나 허용되는 경로 설정
        .anyRequest().authenticated()     // PUBLIC외 다른 요청은 인증된 사용자만 접근 가능
      .and()                              // 메소드 호출 체인읠 http 객체로 복원
        // UsernamePasswordAuthenticationFilter를 AuthenticationFilter로 대체
        .addFilterAt(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .formLogin()                      // App가 form 기반 인증 설정
          .loginPage("/login")            // 로그인 페이지 경로 설정
      .and()
        .logout()                         // 로그아웃의 동작을 설정
          .logoutUrl("/logout")           // 로그아웃 경로 설정(default)
          .logoutSuccessUrl("/login?logged-out")  // 로그아웃 후 리다이렉트되는 경로
          // logoutSuccessHandler를 SimpleLogoutSuccessHandler로 변경
          .logoutSuccessHandler(logoutSuccessHandler())
      .and()
        //Cross-Site Request Forgery(크로스 사이트 요청 위조)기능을 disable
        .csrf().disable()
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

  @Bean
  public AuthenticationFilter authenticationFilter() throws Exception {
    // AuthenticationFilter 객체를 생성하고 필터에 핸들러와 AuthenticationManager를 제공
    // AuthenticationManager는 상위 클래스인 WebSecurityConfigurationAdapter가 제공하는
    // 메서드인 authenticationManagerBean()으로 가져와서 제공한다.
    AuthenticationFilter authenticationFilter = new AuthenticationFilter();
    authenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
    authenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
    authenticationFilter.setAuthenticationManager(authenticationManagerBean());

    return authenticationFilter;
  }

  @Bean
  public AuthenticationSuccessHandler authenticationSuccessHandler() {
    return new SimpleAuthenticationSuccessHandler();
  }

  @Bean
  public AuthenticationFailureHandler authenticationFailureHandler() {
    return new SimpleAuthenticationFailureHandler();
  }

  @Bean
  public LogoutSuccessHandler logoutSuccessHandler() {
    return new SimpleLogoutSuccessHandler();
  }

}
