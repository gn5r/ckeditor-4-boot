package com.github.gn5r.boot.ckeditor4.response;

import java.io.Serializable;

import lombok.Data;

@Data
public class FileUploadReponse implements Serializable {

  private int uploaded;
  private String fileName;
  private String url;
  private FileUploadErrorResponse error;
}
