package com.taskagile.infrastructure.repository;

import javax.persistence.EntityManager;

import com.taskagile.domain.model.user.User;
import com.taskagile.domain.model.user.UserRepository;

import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateUserRepository extends HibernateSupport<User> implements UserRepository {

  public HibernateUserRepository(EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  public User findByUsername(String username) {
    Query<User> query = getSession().createQuery("from User where username = :username", User.class);
    query.setParameter("username", username);
    return query.uniqueResult();
  }

  @Override
  public User findByEmailAddress(String emailAddress) {
    Query<User> query = getSession().createQuery("from User where emailAddress = :emailAddress", User.class);
    query.setParameter("emailAddress", emailAddress);
    return query.uniqueResult();
  }

  // HibernateSupport에서 처리되므로 필요없어서 제거함
  // @Override
  // public void save(User user) {
  //   entityManager.persist(user);
  //   entityManager.flush();
  // }

}
