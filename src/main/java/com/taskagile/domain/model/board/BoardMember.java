package com.taskagile.domain.model.board;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.taskagile.domain.common.model.AbstractBaseEntity;
import com.taskagile.domain.model.user.UserId;

@Entity
@Table(name = "board_member")
public class BoardMember extends AbstractBaseEntity {

  private static final long serialVersionUID = 1101935717986500672L;

  // EmbeddedId와 Embeddable 애노테이션을 적용하여 BoardMemberId를 생성하면
  // boardId와 userId를 BoradMember Entity의 복합 ID로 활용할 수 있다.
  @EmbeddedId
  private BoardMemberId id;

  // BoardMember 생성 팩토리 메서드
  public static BoardMember create(BoardId boardId, UserId userId) {
    BoardMember boardMember = new BoardMember();
    boardMember.id = new BoardMemberId(boardId, userId);
    return boardMember;
  }

  public BoardId getBoardId() {
    return id.getBoardId();
  }

  public UserId getUserId() {
    return id.getUserId();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BoardMember)) return false;
    BoardMember that = (BoardMember) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "BoardMember {" +
      "id=" + id +
      '}';
  }

  @Embeddable
  public static class BoardMemberId implements Serializable {

    private static final long serialVersionUID = -5739169913659318896L;

    @Column(name = "board_id")
    private long boardId;

    @Column(name = "user_id")
    private long userId;

    public BoardMemberId() {
    }

    private BoardMemberId(BoardId boardId, UserId userId) {
      this.boardId = boardId.value();
      this.userId = userId.value();
    }

    public BoardId getBoardId() {
      return new BoardId(boardId);
    }

    public UserId getUserId() {
      return new UserId(userId);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof BoardMemberId)) return false;

      // BoardMemberId의 boardId와 userId가 같으면 동일하다 ?
      BoardMemberId that = (BoardMemberId) o;
      return boardId == that.boardId &&
        userId == that.userId;
    }

    @Override
    public int hashCode() {
      return Objects.hash(boardId, userId);
    }

    @Override
    public String toString() {
      return "BoardMemberId {" +
        "boardId=" + boardId +
        ", userId=" + userId +
        '}';
    }
  }


}
