package com.taskagile.domain.model.user;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * UserDetails를 구현한 SimpleUser(Read only)
 * - SimpleUser 개게의 데이터를 작게 유지하는 것이 중요
 *   왜냐하면 UserDetails 객체는 Authentication 객체에 저장되고
 *   Authentication 객체는 HttpSession에 저장되기 때문이다.
 * - username은 SimpleUser에 보관되어 인증된 사용자가 누군지 알고 싶을 때
 *   사용할 수 있다.
 */
public class SimpleUser implements UserDetails {

  private static final long serialVersionUID = -7144174657188362966L;

  private UserId userId;
  private String username;
  private String password;

  public SimpleUser(User user) {
    this.userId = user.getId();
    this.username = user.getUsername();
    this.password = user.getPassword();
  }

  public UserId getUserId() {
    return userId;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SimpleUser)) return false;
    SimpleUser that = (SimpleUser) o;
    return Objects.equals(username, that.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username);
  }

  @Override
  public String toString() {
    return "SimpleUser {" +
      "userId=" + userId +
      ", username='" + username + '\'' +
      ", password='" + password + '\'' + '}';
  }


}
