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

package org.daobs.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by juanl on 06/04/2016.
 */
@Api(value = "authentication",
    tags = "authentication",
    description = "Authentication operations")
@Controller
public class LoginController {


  @ApiOperation(value = "Get login form",
      nickname = "loginFrom")
  @RequestMapping(
      value = "/signin-form",
      method = RequestMethod.GET)
  public String loginForm() {
    return "signin-form";
  }

  /**
   * Get user details.
   */
  @ApiOperation(value = "Get user details",
      nickname = "getUserDetails")
  @RequestMapping(
      value = "/me",
      method = RequestMethod.GET,
      produces = {
        MediaType.APPLICATION_JSON_VALUE
      })
  public @ResponseBody Map<String, Object> currentUserDetails(Principal activeUser) {
    Map<String, Object> map = new LinkedHashMap<>();
    if (activeUser != null && ((Authentication) activeUser).isAuthenticated()) {
      map.put("authenticated", Boolean.TRUE);
      map.put("username", activeUser.getName());
      map.put("roles", AuthorityUtils.authorityListToSet(((Authentication) activeUser)
          .getAuthorities()));
    } else {
      map.put("authenticated", Boolean.FALSE);
    }
    return map;

  }
}
