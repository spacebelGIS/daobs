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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * Created by francois on 01/04/16.
 */
@RequestMapping(value = {
    "/api",
    "/api/" + org.daobs.api.Api.VERSION_0_1
    })
@Api(value = "test",
     tags = "test",
     description = "Test operations")
@Controller("records")
@EnableWebMvc
@Service
public class TestApi {
  @ApiOperation(value = "Test",
      nickname = "test")
  @RequestMapping(value = "/test",
      method = RequestMethod.GET)
  public ResponseEntity<String> test() {
    return new ResponseEntity<>("ok", HttpStatus.OK);
  }
}
