package com.taskagile.domain.application.impl;

//import javax.transaction.Transactional;

import com.taskagile.domain.application.UserService;
import com.taskagile.domain.application.commands.RegistrationCommand;
import com.taskagile.domain.model.user.RegistrationException;

import org.springframework.stereotype.Service;
//import org.springframework.util.Assert;

@Service
//@Transactional
public class UserServiceImpl implements UserService {

  // private RegistrationManagement registrationManagement;
  // private DomainEventPublisher domainEventPublisher;
  // private MailManager mailManager;

  // public UserServiceImpl(RegistrationManagement registrationManagement,
  //                         DomainEventPublisher domainEventPublisher,
  //                         MailManager mailManager) {
  //   this.registrationManagement = registrationManagement;
  //   this.domainEventPublisher = domainEventPublisher;
  //   this.mailManager = mailManager;
  // }

  @Override
  public void register(RegistrationCommand command) throws RegistrationException {
    //Assert.notNull(command, "Parameter `command` must not be null");
  }


}
