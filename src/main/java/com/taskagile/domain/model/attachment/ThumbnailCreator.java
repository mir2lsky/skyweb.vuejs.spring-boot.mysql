package com.taskagile.domain.model.attachment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import com.taskagile.domain.common.file.FileStorage;
import com.taskagile.domain.common.file.TempFile;
import com.taskagile.utils.ImageUtils;
import com.taskagile.utils.Size;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class ThumbnailCreator {

  private final static Logger log = LoggerFactory.getLogger(ThumbnailCreator.class);
  private final static Set<String> SUPPORTED_EXTENSIONS = new HashSet<>();
  private final static int MAX_WIDTH = 300;
  private final static int MAX_HEIGHT = 300;

  static {
    SUPPORTED_EXTENSIONS.add("png");
    SUPPORTED_EXTENSIONS.add("jpg");
    SUPPORTED_EXTENSIONS.add("jpeg");
  }

  private ImageProcessor imageProcessor;

  public ThumbnailCreator(ImageProcessor imageProcessor) {
    this.imageProcessor = imageProcessor;
  }

  /**
   * Create a thumbnail file and save to the storage
   *
   * @param fileStorage file storage
   * @param tempImageFile a temp image file
   */
  public void create(FileStorage fileStorage, TempFile tempImageFile) {
    // 소스파일인 tempImageFile이 서버에 존재하는지 확인
    Assert.isTrue(tempImageFile.getFile().exists(), "Image file `" +
      tempImageFile.getFile().getAbsolutePath() + "` must exist");

    // 이미지의 확장자를 확인하고 섬네일 생성이 가능한 이미지인지 확인
    String ext = FilenameUtils.getExtension(tempImageFile.getFile().getName());
    if (!SUPPORTED_EXTENSIONS.contains(ext)) {
      throw new ThumbnailCreationException("Not supported image format for creating thumbnail");
    }

    log.debug("Creating thumbnail for file `{}`", tempImageFile.getFile().getName());

    try {
      String sourceFilePath = tempImageFile.getFile().getAbsolutePath();
      if (!sourceFilePath.endsWith("." + ext)) {
        throw new IllegalArgumentException("Image file's ext doesn't match the one in file descriptor");
      }
      // 소스파일을 기반으로 .thumbnail을 확장자 전에 추가하여 섬네일 파일의 경로를 생성
      // ex) /data/temp/image.jpg -> /data/temp/image.thumbnail.jpg
      String tempThumbnailFilePath = ImageUtils.getThumbnailVersion(tempImageFile.getFile().getAbsolutePath());
      //생성할 섬네일의 크기를 취득
      Size resizeTo = getTargetSize(sourceFilePath);
      imageProcessor.resize(sourceFilePath, tempThumbnailFilePath, resizeTo);

      fileStorage.saveTempFile(TempFile.create(tempImageFile.tempRootPath(), Paths.get(tempThumbnailFilePath)));
      // Delete temp thumbnail file
      Files.delete(Paths.get(tempThumbnailFilePath));
    } catch (Exception e) {
      log.error("Failed to create thumbnail for file `" + tempImageFile.getFile().getAbsolutePath() + "`", e);
      throw new ThumbnailCreationException("Creating thumbnail failed", e);
    }
  }

  private Size getTargetSize(String imageFilePath) throws IOException {
    // 소스 이미지 파일의 실제 크기를 가져오기 위해 ImgaeProcessor의 getSize() 호출
    Size actualSize = imageProcessor.getSize(imageFilePath);
    if (actualSize.getWidth() <= MAX_WIDTH && actualSize.getHeight() <= MAX_HEIGHT) {
      // 실제 크기가 정의된 최대 넓이와 최대 높이보다 작으면 그냥 현 실제 크기를 리턴
      return actualSize;
    }

    // 섬네일의 크기를 계산
    if (actualSize.getWidth() > actualSize.getHeight()) {
      // 실제 크기의 넓이가 높이보다 크면 최대 넓이를 기준으로 높이를 계산
      int width = MAX_WIDTH;
      int height = (int) Math.floor(((double)width / (double)actualSize.getWidth()) * actualSize.getHeight());
      return new Size(width, height);
    } else {
      // 아니면 최대 높이를 기준으로 넓이를 계산
      int height = MAX_HEIGHT;
      int width = (int) Math.floor(((double) height / (double) actualSize.getHeight()) * actualSize.getWidth());
      return new Size(width, height);
    }
  }
}
