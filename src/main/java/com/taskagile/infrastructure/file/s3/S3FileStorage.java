package com.taskagile.infrastructure.file.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.taskagile.domain.common.file.AbstractBaseFileStorage;
import com.taskagile.domain.common.file.FileStorageException;
import com.taskagile.domain.common.file.TempFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

@Component("s3FileStorage")
public class S3FileStorage extends AbstractBaseFileStorage {

  private static final Logger log = LoggerFactory.getLogger(S3FileStorage.class);

  private Environment environment;
  private String rootTempPath;
  private AmazonS3 s3;

  public S3FileStorage(Environment environment,
                       @Value("${app.file-storage.temp-folder}") String rootTempPath) {
    this.environment = environment;
    this.rootTempPath = rootTempPath;

    // 오직 활성화된 파일 저장소가 S3일 때만 S3 클라이언트를 초기화
    if ("s3FileStorage".equals(environment.getProperty("app.file-storage.active"))) {
      this.s3 = initS3Client();
    }
  }

  @Override
  public TempFile saveAsTempFile(String folder, MultipartFile multipartFile) {
    // 단순히 AbstractBaseFileStorage의 saveMultipartFileToLocalTempFolder() 메소드 호출
    return saveMultipartFileToLocalTempFolder(rootTempPath, folder, multipartFile);
  }

  @Override
  public void saveTempFile(TempFile tempFile) {
    Assert.notNull(s3, "S3FileStorage must be initialized properly");

    String fileKey = tempFile.getFileRelativePath();
    String bucketName = environment.getProperty("app.file-storage.s3-bucket-name");
    Assert.hasText(bucketName, "Property `app.file-storage.s3-bucket-name` must not be blank");

    try {
      log.debug("Saving file `{}` to s3", tempFile.getFile().getName());
      // 파일에 대한 상대경로를 버킷에 있는 파일의 키 값으로 사용헤서 PutObjectRequest 생성
      PutObjectRequest putRequest = new PutObjectRequest(bucketName, fileKey, tempFile.getFile());
      // 파일을 어디서나 접근 가능하도록 지정
      putRequest.withCannedAcl(CannedAccessControlList.PublicRead);
      // 파일을 S3에 저장하기 위해 S3 클라이언트의 PutObject()메소드를 호출
      s3.putObject(putRequest);
      log.debug("File `{}` saved to s3", tempFile.getFile().getName(), fileKey);
    } catch (Exception e) {
      log.error("Failed to save file to s3", e);
      throw new FileStorageException("Failed to save file `" + tempFile.getFile().getName() + "` to s3", e);
    }
  }

  @Override
  public String saveUploaded(String folder, MultipartFile multipartFile) {
    Assert.notNull(s3, "S3FileStorage must be initialized properly");

    String originalFileName = multipartFile.getOriginalFilename();
    // 커스텀 메타데이터인 Original-File-Name을 추가하기 위해 ObjectMetadata 인스턴스를 생성
    // Original-File-Name은 파일을 가져오기 위한 요청에 대한 응답의 헤더에 x-zmz-meta-original-file-name으로 나타난다.
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(multipartFile.getSize());
    metadata.setContentType(multipartFile.getContentType());
    metadata.addUserMetadata("Original-File-Name", originalFileName);
    String finalFileName = generateFileName(multipartFile);
    String s3ObjectKey = folder + "/" + finalFileName;

    String bucketName = environment.getProperty("app.file-storage.s3-bucket-name");
    Assert.hasText(bucketName, "Property `app.file-storage.s3-bucket-name` must not be blank");

    try {
      log.debug("Saving file `{}` to s3", originalFileName);
      PutObjectRequest putRequest = new PutObjectRequest(
        bucketName, s3ObjectKey, multipartFile.getInputStream(), metadata);
      putRequest.withCannedAcl(CannedAccessControlList.PublicRead);
      s3.putObject(putRequest);
      log.debug("File `{}` saved to s3 as `{}`", originalFileName, s3ObjectKey);
    } catch (Exception e) {
      log.error("Failed to save file to s3", e);
      throw new FileStorageException("Failed to save file `" + multipartFile.getOriginalFilename() + "` to s3", e);
    }

    return s3ObjectKey;
  }

  private AmazonS3 initS3Client() {
    String s3Region = environment.getProperty("app.file-storage.s3-region");
    Assert.hasText(s3Region, "Property `app.file-storage.s3-region` must not be blank");

    if (environment.acceptsProfiles("dev")) {
      // 개발환경일 경우, 즉 app를 로컬 PC에서 동작시킬 경우
      log.debug("Initializing dev S3 client with access key and secret key");

      // 액세스 키와 시크릿 키를 사용하여 자격 증명 정보 생성
      String s3AccessKey = environment.getProperty("app.file-storage.s3-access-key");
      String s3SecretKey = environment.getProperty("app.file-storage.s3-secret-key");

      Assert.hasText(s3AccessKey, "Property `app.file-storage.s3-access-key` must not be blank");
      Assert.hasText(s3SecretKey, "Property `app.file-storage.s3-secret-key` must not be blank");

      BasicAWSCredentials awsCredentials = new BasicAWSCredentials(s3AccessKey, s3SecretKey);
      AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);

      // S3 클라이언트 인스턴스를 생성하는데 AmazonS3ClientBuilder 사용
      AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();
      builder.setRegion(s3Region);  // 리전 정보
      builder.withCredentials(credentialsProvider); // 자격 증명 정보
      return builder.build();

    } else {
      // EC2 서버에서 app를 동작시킬 경우 EC2 인스턴스에 정의된 IAM Role을 사용
      // 하여 자격증명 정보를 제공
      log.debug("Initializing default S3 client using AIM role");
      return AmazonS3ClientBuilder.standard()
        .withCredentials(new InstanceProfileCredentialsProvider(false))
        .withRegion(s3Region)
        .build();
    }
  }
}
