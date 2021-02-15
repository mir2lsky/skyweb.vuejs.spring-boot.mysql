package com.taskagile.domain.common.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {

  /**
   * Save a file
   * MultipartFile 인스턴스를 application.preperties에 명시된 서버의
   * temp 폴더에 저장하는데 사용됨
   * 파일을 S3에 저장하기 전에 섬네일 이미지를 만들 필요가 있을 때 유용
   *
   * @param folder the folder the file will be saved into
   * @param file the file to save
   * @return the saved file's path
   */
  TempFile saveAsTempFile(String folder, MultipartFile file);

  /**
   * Save a temp file to its target location
   * 임시 파일을 최종 목적지에 저장하는데 사용
   * @param tempFile a temp file
   */
  void saveTempFile(TempFile tempFile);

  /**
   * Save uploaded file to its target location
   * 업로드된 MultipartFile을 최종 목적지에 저장하는데 사용
   *
   * @param folder the folder the file will be saved into
   * @param file the file to save
   * @return saved file's relative path
   */
  String saveUploaded(String folder, MultipartFile file);
}
