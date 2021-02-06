package com.taskagile.domain.application.commands;

import com.taskagile.domain.model.team.TeamId;
import com.taskagile.domain.model.user.UserId;

/** === Board 관련 Command(VO) ===
* 오직 App Service에서만 Command 객체를 사용한다.
* 이유는 Command객체는 App Service의 API 계약의 일부이고 나중에 변경될 수 있기 때문
* Command에 대한 변경사항이 도메인에 영향을 주지 않아야 한다.
* App Service는 도메인에 의존해야 하지만 도메인 서비스는 App 서비스에 의존하면 안된다.
*/
public class CreateBoardCommand {

  private UserId userId;
  private String name;
  private String description;
  private TeamId teamId;

  public CreateBoardCommand(UserId userId, String name, String description, TeamId teamId) {
    this.userId = userId;
    this.name = name;
    this.description = description;
    this.teamId = teamId;
  }

  public UserId getUserId() {
    return userId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public TeamId getTeamId() {
    return teamId;
  }
}
