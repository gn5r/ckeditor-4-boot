package com.github.gn5r.boot.ckeditor4.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.gn5r.boot.ckeditor4.dto.Image;
import com.github.gn5r.boot.ckeditor4.dto.SelectBox;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ImageService {

  @Value("${application.file.image.path}")
  private String IMAGE_PATH;

  @Value("${application.file.image.root-path}")
  private String IMAGE_ROOT_PATH;

  @Value("${application.file.image.tmp-path}")
  private String IMAGE_TMP_PATH;

  @Value("${application.front.url}")
  private String FRONT_URL;

  @Value("#{new Boolean('${application.file.image.store.is-temp}')}")
  private Boolean isTemp;

  private static final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("uuuuMMdd");

  public String getNow() {
    return YMD.format(LocalDate.now());
  }

  private String createUrl(String ymd, String filename) {
    return FRONT_URL + IMAGE_PATH + ymd + "/" + filename;
  }

  public String uploadImage(byte[] image, String filename) throws IOException {
    String now = getNow();
    String outputPath = (isTemp ? IMAGE_TMP_PATH : IMAGE_ROOT_PATH) + now;
    log.debug("output path:" + outputPath);
    if (!new File(outputPath).exists()) {
      Files.createDirectories(Paths.get(outputPath));
    }

    String filepath = outputPath + "/" + filename;
    log.debug("file path:" + filepath);
    try (OutputStream os = Files.newOutputStream(Paths.get(filepath), StandardOpenOption.CREATE)) {
      os.write(image);
    } catch (IOException e) {
      throw e;
    }

    String url = createUrl(now, filename);
    return url;
  }

  public List<SelectBox> getSelects() {
    List<SelectBox> list = new ArrayList<>();
    list.add(new SelectBox());

    File dir = new File(isTemp ? IMAGE_TMP_PATH : IMAGE_ROOT_PATH);
    if (dir.exists() && dir.isDirectory()) {
      List<File> files = Arrays.asList(dir.listFiles());
      files.stream().filter(File::isDirectory).sorted(Comparator.comparing(File::lastModified)).forEach(f -> {
        SelectBox selectBox = new SelectBox();
        selectBox.setText(f.getName());
        selectBox.setValue(f.getName());
        list.add(selectBox);
      });
    }

    return list;
  }

  public List<Image> getImages(String ymd) {
    List<Image> list = new ArrayList<>();
    String now = StringUtils.defaultString(ymd, getNow());
    String path = (isTemp ? IMAGE_TMP_PATH : IMAGE_ROOT_PATH) + now;
    File dir = new File(path);
    if (dir.exists() && dir.isDirectory()) {
      List<File> files = Arrays.asList(dir.listFiles());
      files.stream().filter(File::isFile).sorted(Comparator.comparing(File::lastModified)).forEach(f -> {
        Image image = new Image();
        image.setFilename(f.getName());
        image.setFilepath(path + "/" + f.getName());
        image.setUrl(createUrl(now, f.getName()));
        list.add(image);
      });
    }
    return list;
  }

  public void deleteImages(List<String> targets) throws IOException {
    for (String path : targets) {
      Files.deleteIfExists(Paths.get(path));
    }
  }
}
