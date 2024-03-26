package com.github.gn5r.boot.ckeditor4.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Value("${application.file.image.path}")
  private String IMAGE_PATH;

  @Value("${application.file.image.root-path}")
  private String IMAGE_ROOT_PATH;

  @Value("${application.file.image.tmp-path}")
  private String IMAGE_TMP_PATH;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler(IMAGE_PATH + "**").addResourceLocations("file:" + IMAGE_TMP_PATH);
  }
}
