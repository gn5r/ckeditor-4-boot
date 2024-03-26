package com.github.gn5r.boot.ckeditor4.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.github.gn5r.boot.ckeditor4.form.IndexForm;
import com.github.gn5r.boot.ckeditor4.response.FileUploadErrorResponse;
import com.github.gn5r.boot.ckeditor4.response.FileUploadReponse;
import com.github.gn5r.boot.ckeditor4.service.ImageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class IndexController {

  private final ImageService imageService;

  @GetMapping
  public String index(@ModelAttribute("form") IndexForm form) {
    return "index";
  }

  @PostMapping
  public String post(@ModelAttribute("form") IndexForm form) {
    return "index";
  }

  @GetMapping("browse")
  public String browse(Model model, @RequestParam(name = "ymd", required = false) String ymd) {
    model.addAttribute("now", StringUtils.defaultString(ymd, imageService.getNow()));
    model.addAttribute("images", imageService.getImages(ymd));
    model.addAttribute("selects", imageService.getSelects());
    return "browse";
  }

  @PostMapping("upload")
  @ResponseBody
  public ResponseEntity<?> uploadImage(@RequestParam("upload") MultipartFile image) {
    FileUploadReponse res = new FileUploadReponse();

    if (!image.isEmpty()) {
      res.setUploaded(1);
      try {
        String fileName = image.getOriginalFilename();
        String url = imageService.uploadImage(image.getBytes(), fileName);
        res.setFileName(fileName);
        res.setUrl(url);
      } catch (IOException e) {
        log.error(ExceptionUtils.getStackTrace(e));
        FileUploadErrorResponse error = new FileUploadErrorResponse();
        error.setMessage(e.getLocalizedMessage());
        res.setError(error);
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }

    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @PostMapping("delete")
  @ResponseBody
  public ResponseEntity<?> deleteImages(@RequestBody List<String> targets) {
    try {
      imageService.deleteImages(targets);
    } catch (IOException e) {
      log.error(ExceptionUtils.getStackTrace(e));
      Map<String, String> error = new HashMap<>();
      error.put("message", e.getLocalizedMessage());
      return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
