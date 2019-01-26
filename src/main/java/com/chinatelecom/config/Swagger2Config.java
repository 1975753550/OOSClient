/**
 * 文件名：Swagger2Config.java
 * 
 * 北京中油瑞飞信息技术有限责任公司(http://www.richfit.com) Copyright © 2017 Richfit Information Technology Co.,
 * LTD. All Right Reserved.
 */
package com.chinatelecom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Predicate;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * <p>
 * <li>Description:</li>
 * <li>$Author$</li>
 * <li>$Revision: 4547 $</li>
 * <li>$Date: 2017-05-12 16:10:40 +0800 (五, 2017-05-12) $</li>
 * 
 * @version 1.0
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

  @Bean
  public Docket createRestApi() {
    return new Docket(DocumentationType.SWAGGER_2).apiInfo(getApiInfo())
        .useDefaultResponseMessages(false).select().apis(getRredicate()).build();
  }

  private ApiInfo getApiInfo() {
    return new ApiInfoBuilder().title("oos API").version("1.0.1").build();
  }

  /**
   * 设置解析规则
   * 
   * @return
   * @author created by Zw
   */
  private Predicate<RequestHandler> getRredicate() {
    return new Predicate<RequestHandler>() {

      @Override
      public boolean apply(RequestHandler input) {
        Class<?> declaringClass = input.declaringClass();
        if (declaringClass.isAnnotationPresent(RestController.class)
            && input.isAnnotatedWith(ApiOperation.class)) {
          return true;
        } else {
          return false;
        }
      }
    };
  }
}
