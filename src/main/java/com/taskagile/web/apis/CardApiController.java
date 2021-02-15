package com.taskagile.web.apis;

import java.util.List;

import com.taskagile.domain.application.CardService;
import com.taskagile.domain.application.commands.AddCardAttachmentCommand;
import com.taskagile.domain.application.commands.AddCardCommentCommand;
import com.taskagile.domain.common.file.FileUrlCreator;
import com.taskagile.domain.common.security.CurrentUser;
import com.taskagile.domain.model.activity.Activity;
import com.taskagile.domain.model.attachment.Attachment;
import com.taskagile.domain.model.card.Card;
import com.taskagile.domain.model.card.CardId;
import com.taskagile.domain.model.user.SimpleUser;
import com.taskagile.web.payload.AddCardCommentPayload;
import com.taskagile.web.payload.AddCardPayload;
import com.taskagile.web.payload.ChangeCardDescriptionPayload;
import com.taskagile.web.payload.ChangeCardPositionsPayload;
import com.taskagile.web.payload.ChangeCardTitlePayload;
import com.taskagile.web.results.AddCardResult;
import com.taskagile.web.results.ApiResult;
import com.taskagile.web.results.AttachmentResult;
import com.taskagile.web.results.AttachmentResults;
import com.taskagile.web.results.CardActivitiesResult;
import com.taskagile.web.results.CardResult;
import com.taskagile.web.results.CommentActivityResult;
import com.taskagile.web.results.Result;
import com.taskagile.web.updater.CardUpdater;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class CardApiController {

  private CardService cardService;
  private CardUpdater cardUpdater;
  private FileUrlCreator fileUrlCreator;

  public CardApiController(CardService cardService,
                           CardUpdater cardUpdater,
                           FileUrlCreator fileUrlCreator) {
    this.cardService = cardService;
    this.cardUpdater = cardUpdater;
    this.fileUrlCreator = fileUrlCreator;
  }

  @PostMapping("/api/cards")
  public ResponseEntity<ApiResult> addCard(@RequestBody AddCardPayload payload,
                                           @CurrentUser SimpleUser currentUser) {
    Card card = cardService.addCard(payload.toCommand(currentUser.getUserId()));
    // 카드가 추가될 때 RealTime Client를 업데이트 처리
    cardUpdater.onCardAdded(payload.getBoardId(), card);
    return AddCardResult.build(card);
  }

  @PostMapping("/api/cards/positions")
  public ResponseEntity<ApiResult> changeCardPositions(@RequestBody ChangeCardPositionsPayload payload) {
    cardService.changePositions(payload.toCommand());
    return Result.ok();
  }

  @GetMapping("/api/cards/{cardId}")
  public ResponseEntity<ApiResult> getCard(@PathVariable long cardId) {
    Card card = cardService.findById(new CardId(cardId));
    return CardResult.build(card);
  }

  @PutMapping("/api/cards/{cardId}/title")
  public ResponseEntity<ApiResult> changeTitle(@PathVariable long cardId,
                                               @RequestBody ChangeCardTitlePayload payload) {
    cardService.changeCardTitle(payload.toCommand(cardId));
    return Result.ok();
  }

  @PutMapping("/api/cards/{cardId}/description")
  public ResponseEntity<ApiResult> changeDescription(@PathVariable long cardId,
                                                     @RequestBody ChangeCardDescriptionPayload payload) {
    cardService.changeCardDescription(payload.toCommand(cardId));
    return Result.ok();
  }

  @PostMapping("/api/cards/{cardId}/comments")
  public ResponseEntity<ApiResult> addCardComment(@PathVariable long cardId,
                                                  @RequestBody AddCardCommentPayload payload,
                                                  @CurrentUser SimpleUser currentUser) {
    AddCardCommentCommand command = payload.toCommand(new CardId(cardId), currentUser.getUserId());
    Activity activity = cardService.addComment(command);
    return CommentActivityResult.build(activity);
  }

  @GetMapping("/api/cards/{cardId}/activities")
  public ResponseEntity<ApiResult> getCardActivities(@PathVariable long cardId) {
    List<Activity> activities = cardService.findCardActivities(new CardId(cardId));
    return CardActivitiesResult.build(activities);
  }

  @PostMapping("/api/cards/{cardId}/attachments")
  public ResponseEntity<ApiResult> addAttachment(@PathVariable long cardId,
                                                 @RequestParam("file") MultipartFile file,
                                                 @CurrentUser SimpleUser currentUser) {
    AddCardAttachmentCommand command = new AddCardAttachmentCommand(cardId, file, currentUser.getUserId());
    Attachment attachment = cardService.addAttachment(command);
    return AttachmentResult.build(attachment, fileUrlCreator);
  }

  @GetMapping("/api/cards/{cardId}/attachments")
  public ResponseEntity<ApiResult> getAttachments(@PathVariable long cardId) {
    List<Attachment> attachments = cardService.getAttachments(new CardId(cardId));
    return AttachmentResults.build(attachments, fileUrlCreator);
  }

}
