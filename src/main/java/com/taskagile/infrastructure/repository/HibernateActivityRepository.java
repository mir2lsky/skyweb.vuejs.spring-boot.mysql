package com.taskagile.infrastructure.repository;

import java.util.List;

import javax.persistence.EntityManager;

import com.taskagile.domain.model.activity.Activity;
import com.taskagile.domain.model.activity.ActivityRepository;
import com.taskagile.domain.model.card.CardId;

import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateActivityRepository extends HibernateSupport<Activity> implements ActivityRepository {

  HibernateActivityRepository(EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  public List<Activity> findCardActivities(CardId cardId) {
    String sql = "SELECT a.* FROM activity a WHERE a.card_id = :cardId order by id desc";
    NativeQuery<Activity> query = getSession().createNativeQuery(sql, Activity.class);
    query.setParameter("cardId", cardId.value());
    return query.list();
  }
}
