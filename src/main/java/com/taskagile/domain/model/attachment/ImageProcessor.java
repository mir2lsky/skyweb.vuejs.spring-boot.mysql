package com.taskagile.domain.model.attachment;

import java.io.IOException;
import java.util.List;

import com.taskagile.utils.Size;

import org.apache.commons.lang3.math.NumberUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.ImageCommand;
import org.im4java.process.ArrayListOutputConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class ImageProcessor {

  private String commandSearchPath;

  public ImageProcessor(@Value("${app.image.command-search-path}") String commandSearchPath) {
    this.commandSearchPath = commandSearchPath;
  }

  // 이미지의 실제 크기를 조정
  // im4java API를 사용해서 다음과 같은 그래픽스 매직의 conver 명령어와 비슷한 명령어를 만드는 처리
  // ex) gm convet -resize 300*185 -quality 70 source.jpg source.thumbnail.jpg
  public void resize(String sourceFilePath, String targetFilePath, Size resizeTo) throws Exception {
    Assert.isTrue(resizeTo.getHeight() > 0, "Resize height must be greater than 0");
    Assert.isTrue(resizeTo.getWidth() > 0, "Resize width must be greater than 0");

    // ConvertCom 인스턴스의 검색경로를 설정하는 이유는 맥(Mac)에서 그래픽스 매직이 Homebrew를 통해
    // 설치되면 그래픽스 매직 명령어가 /usr/bin 경로가 아닌 /usr/local/bin 경로에 존재하기 때문
    ConvertCmd cmd = new ConvertCmd(true);
    cmd.setSearchPath(commandSearchPath);

    IMOperation op = new IMOperation();
    op.addImage(sourceFilePath);
    op.quality(70d);
    op.resize(resizeTo.getWidth(), resizeTo.getHeight());
    op.addImage(targetFilePath);
    cmd.run(op);
  }

  // 이미지의 실제 크기를 취득
  // getSize가 하는 일은 다음과 같은 명령어를 만드는 것이며 그래픽스 매직의 identify 명령어를
  // 활용해서 이미지의 기본 정보인 너비와 높이를 가져온다.
  // ex) gm identify -format '%w, %h' image.jpg
  public Size getSize(String imagePath) throws IOException {
    try {
      ImageCommand cmd = new ImageCommand();
      cmd.setCommand("gm", "identify");
      cmd.setSearchPath(commandSearchPath);

      ArrayListOutputConsumer outputConsumer = new ArrayListOutputConsumer();
      cmd.setOutputConsumer(outputConsumer);

      IMOperation op = new IMOperation();
      op.format("%w,%h");
      op.addImage(imagePath);
      cmd.run(op);

      List<String> cmdOutput = outputConsumer.getOutput();
      String result = cmdOutput.get(0);
      Assert.hasText(result, "Result of command `gm identify` must not be blank");

      String[] dimensions = result.split(",");
      return new Size(NumberUtils.toInt(dimensions[0]), NumberUtils.toInt(dimensions[1]));
    } catch (Exception e) {
      throw new IOException("Failed to get image's height/width", e);
    }
  }
}
