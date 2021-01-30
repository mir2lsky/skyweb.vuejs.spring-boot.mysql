package com.taskagile.domain.model.user;

import com.taskagile.domain.model.user.events.UserRegisteredEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;

public class UserRegisteredEventHandler {

  private final static Logger log = LoggerFactory.getLogger(UserRegisteredEventHandler.class);

  @EventListener(UserRegisteredEvent.class)
  public void handleEvent(UserRegisteredEvent event) {
    log.debug("Handling `{}` registration event", event.getUser().getEmailAddress());
  }
}
