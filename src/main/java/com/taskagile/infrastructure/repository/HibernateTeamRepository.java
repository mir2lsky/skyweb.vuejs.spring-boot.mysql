package com.taskagile.infrastructure.repository;

import java.util.List;

import javax.persistence.EntityManager;

import com.taskagile.domain.model.team.Team;
import com.taskagile.domain.model.team.TeamRepository;
import com.taskagile.domain.model.user.UserId;

import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateTeamRepository extends HibernateSupport<Team> implements TeamRepository {

  HibernateTeamRepository(EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  public List<Team> findTeamsByUserId(UserId userId) {
    // 사용자가 생성한 팀과 사용자가 보드에 가입했기 때문에 접근이 가능한 팀을
    // UNION 해서 가져온다.
    String sql =
      " SELECT t.* FROM team t WHERE t.user_id = :userId " +
      " UNION " +
      " ( " +
      "   SELECT t.* FROM team t, board b, board_member bm " +
      "   WHERE t.id = b.team_id AND bm.board_id = b.id AND bm.user_id = :userId " +
      " ) ";
    NativeQuery<Team> query = getSession().createNativeQuery(sql, Team.class);
    query.setParameter("userId", userId.value());
    return query.list();
  }
}

