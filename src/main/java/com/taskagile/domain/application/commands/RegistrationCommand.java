package com.taskagile.domain.application.commands;

import java.util.Objects;

import org.springframework.util.Assert;

/** === Board 관련 Command(VO) ===
* 오직 App Service에서만 Command 객체를 사용한다.
* 이유는 Command객체는 App Service의 API 계약의 일부이고 나중에 변경될 수 있기 때문
* Command에 대한 변경사항이 도메인에 영향을 주지 않아야 한다.
* App Service는 도메인에 의존해야 하지만 도메인 서비스는 App 서비스에 의존하면 안된다.
*/
public class RegistrationCommand {
  private String username;
  private String emailAddress;
  private String firstName;
  private String lastName;
  private String password;

  public RegistrationCommand(String username, String emailAddress,
                  String firstName, String lastName, String password) {
    Assert.hasText(username, "Parameter `username` must not be empty");
    Assert.hasText(emailAddress, "Parameter `emailAddress` must not be empty");
    Assert.hasText(firstName, "Parameter `firstName` must not be empty");
    Assert.hasText(lastName, "Parameter `lastName` must not be empty");
    Assert.hasText(password, "Parameter `password` must not be empty");

    this.username = username;
    this.emailAddress = emailAddress;
    this.firstName = firstName;
    this.lastName = lastName;
    this.password = password;
  }

  public String getPassword() {
    return password;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getUsername() {
    return username;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof RegistrationCommand)) return false;

    RegistrationCommand that = (RegistrationCommand) o;
    return Objects.equals(username, that.username) &&
      Objects.equals(emailAddress, that.emailAddress) &&
      Objects.equals(firstName, that.firstName) &&
      Objects.equals(lastName, that.lastName) &&
      Objects.equals(password, that.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, emailAddress, firstName, lastName, password);
  }

  @Override
  public String toString() {
    return "RegistrationCommand {" +
      "username='" + username + '\'' +
      ", emailAddress='" + emailAddress + '\'' +
      ", firstName='" + firstName + '\'' +
      ", lastName='" + lastName + '\'' +
      ", password='" + password + '\'' +
      '}';
  }
}
