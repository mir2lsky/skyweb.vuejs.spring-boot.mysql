package com.taskagile.web.results;

import com.taskagile.domain.model.board.Board;

import org.springframework.http.ResponseEntity;

/**
 *  웹 요청에 대한 처리 결과를 ApiResult 객체에 담아서
 *  ResponseEntity에 넘겨주는 객체
 *  */
public class CreateBoardResult {

  public static ResponseEntity<ApiResult> build(Board board) {
    ApiResult apiResult = ApiResult.blank()
      .add("id", board.getId().value())
      .add("name", board.getName())
      .add("description", board.getDescription())
      .add("teamId", board.getTeamId().value());

    return Result.ok(apiResult);
  }

}
