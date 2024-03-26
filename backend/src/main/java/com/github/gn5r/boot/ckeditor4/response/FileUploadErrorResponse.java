package com.github.gn5r.boot.ckeditor4.response;

import java.io.Serializable;

import lombok.Data;

@Data
public class FileUploadErrorResponse implements Serializable {

  private String message;
}
