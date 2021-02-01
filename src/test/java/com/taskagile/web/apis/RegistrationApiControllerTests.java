package com.taskagile.web.apis;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import com.taskagile.config.SecurityConfiguration;
import com.taskagile.domain.application.UserService;
import com.taskagile.domain.model.user.EmailAddressExistsException;
import com.taskagile.domain.model.user.UsernameExistsException;
import com.taskagile.utils.JsonUtils;
import com.taskagile.web.payload.RegistrationPayload;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { SecurityConfiguration.class, RegistrationApiController.class })
@WebMvcTest
public class RegistrationApiControllerTests {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private UserService serviceMock;

  @Test
  public void register_blankPayload_shouldFailAndReturn400() throws Exception {
    mvc.perform(post("/api/registrations"))
      .andExpect(status().is(400));

  }

  @Test
  public void register_existedUsername_shouldFailAndReturn400() throws Exception {
    RegistrationPayload payload = new RegistrationPayload();
    payload.setUsername("exist");
    payload.setEmailAddress("test@taskagile.com");
    payload.setPassword("MyPassword!");

    // 아래의 mvc객체를 통해 register()가 호출될 때
    // serviceMock이 UsernameExistsException을 던지도록 설정.
    doThrow(UsernameExistsException.class)
      .when(serviceMock)
      .register(payload.toCommand());

    mvc.perform(
      post("/api/registrations")
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtils.toJson(payload)))
      .andExpect(status().is(400))
      .andExpect(jsonPath("$.message").value("Username already exists"));
  }

  @Test
  public void register_existedEmailAddress_shouldFailAndReturn400() throws Exception {
    RegistrationPayload payload = new RegistrationPayload();
    payload.setUsername("test");
    payload.setEmailAddress("exist@taskagile.com");
    payload.setPassword("MyPassword!");

    // 아래의 mvc객체를 통해 register()가 호출될 때
    // serviceMock이 EmailAddressExistsException 을 던지도록 설정.
    doThrow(EmailAddressExistsException.class)
      .when(serviceMock)
      .register(payload.toCommand());

    mvc.perform(
      post("/api/registrations")
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtils.toJson(payload)))
      .andExpect(status().is(400))
      .andExpect(jsonPath("$.message").value("Email address already exists"));
  }

  @Test
  public void register_validPayload_shouldSucceedAndReturn201() throws Exception {
    RegistrationPayload payload = new RegistrationPayload();
    payload.setUsername("test");
    payload.setEmailAddress("exist@taskagile.com");
    payload.setPassword("MyPassword!");


    // 아래의 mvc객체를 통해 register()가 호출될 때 아무런 예외도 발생하지 않도록 설정.
    // 이유는 서비스 메서드의 반환값이 void이고
    // 회원 가입이 성공했을 때 어떤 예외도 발생하지 않기 때문
    doNothing().when(serviceMock)
      .register(payload.toCommand());

    mvc.perform(
      post("/api/registrations")
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtils.toJson(payload)))
      .andExpect(status().is(201));

  }

}
