/**
 * Copyright 2014-2016 European Environment Agency <p> Licensed under the EUPL, Version 1.1 or – as
 * soon they will be approved by the European Commission - subsequent versions of the EUPL (the
 * "Licence"); You may not use this work except in compliance with the Licence. You may obtain a
 * copy of the Licence at: <p> https://joinup.ec.europa.eu/community/eupl/og_page/eupl <p> Unless
 * required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the Licence for the specific language governing permissions and limitations under
 * the Licence.
 */

package org.daobs.controller.exception;

import org.daobs.controller.binding.ErrorResource;
import org.daobs.controller.binding.FieldErrorResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class DaobsExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler({InvalidHarvesterException.class})
  protected ResponseEntity<Object> handleInvalidRequest(RuntimeException exception,
                                                        WebRequest request) {
    InvalidHarvesterException ire = (InvalidHarvesterException) exception;
    List<FieldErrorResource> fieldErrorResources = new ArrayList<>();

    List<String> fieldErrors = ire.getErrors();
    for (String fieldError : fieldErrors) {
      FieldErrorResource fieldErrorResource = new FieldErrorResource();
      // fieldErrorResource.setResource(fieldError.getObjectName());
      // fieldErrorResource.setField(fieldError.getField());
      // fieldErrorResource.setCode(fieldError.getCode());
      fieldErrorResource.setMessage(fieldError);
      fieldErrorResources.add(fieldErrorResource);
    }

    ErrorResource error = new ErrorResource("InvalidRequest", ire.getMessage());
    error.setFieldErrors(fieldErrorResources);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    return handleExceptionInternal(exception, error, headers,
      HttpStatus.BAD_REQUEST, request);
  }
}
