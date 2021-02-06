package com.taskagile.web.payload;

import com.taskagile.domain.application.commands.CreateBoardCommand;
import com.taskagile.domain.model.team.TeamId;
import com.taskagile.domain.model.user.UserId;

/**
 * Request Body에 있는 매개변수를 가져오기 위한 클래스
 */
public class CreateBoardPayload {

  private String name;
  private String description;
  private long teamId;

  // payload 객체로부터 내부적으로 사용할 VO 객체 생성
  public CreateBoardCommand toCommand(UserId userId) {
    return new CreateBoardCommand(userId, name, description, new TeamId(teamId));
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setTeamId(long teamId) {
    this.teamId = teamId;
  }

}
