/**
 * Copyright 2014-2016 European Environment Agency
 *
 * Licensed under the EUPL, Version 1.1 or – as soon
 * they will be approved by the European Commission -
 * subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package org.daobs.api;

import static com.google.common.base.Predicates.or;
import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.builders.PathSelectors.regex;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

import com.google.common.base.Predicate;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;



@EnableWebMvc
@Configuration
@Service
@ComponentScan(basePackages = {"org.daobs.api"})
@EnableSwagger2 //Loads the spring beans required by the framework
public class SwaggerConfig {
  @Autowired
  private TypeResolver typeResolver;

  private Predicate<String> paths() {
    return or(
      regex(".*")
    );
  }

  /**
   * Load API documentation.
   */
  public Docket loadApi() {
    Docket doc = new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(new ApiInfo(
          "DAOBS Api Documentation (beta)",
          "Learn how to use the DAOBS REST API.",
          Api.VERSION_0_1,
          "urn:tos",
          new Contact(Api.CONTACT_EMAIL, "https://github.com/INSPIRE-MIF/daobs", Api.CONTACT_EMAIL),
          "EUPL",
          "https://joinup.ec.europa.eu/community/eupl/og_page/eupl"))
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(paths())
        .build()
        .pathMapping("/")
        .genericModelSubstitutes(ResponseEntity.class)
        // .directModelSubstitute(XMLGregorianCalendar.class, Date.class)
        .alternateTypeRules(
          newRule(typeResolver.resolve(DeferredResult.class,
            typeResolver.resolve(ResponseEntity.class, WildcardType.class)
            ),
            typeResolver.resolve(WildcardType.class)
          ))
        .useDefaultResponseMessages(false)
        .securitySchemes(newArrayList(new BasicAuth("ba")));
    return doc;
  }

  @Bean
  public Docket daobsApi() {
    return loadApi();
  }
}
