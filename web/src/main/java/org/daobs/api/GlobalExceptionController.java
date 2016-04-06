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

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.MissingResourceException;

/**
 * Global exception controller.
 */
@ControllerAdvice
public class GlobalExceptionController {

  /**
   * Unauthorized handler.
   */
  @ResponseBody
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler({
      SecurityException.class
      })
  public Object unauthorizedHandler(final Exception exception) {
    return new LinkedHashMap<String, String>() {{
        put("code", "unauthorized");
        put("message", exception.getClass().getSimpleName());
        put("description", exception.getMessage());
        }
    };
  }

  /**
   * maxFileExceededHandler handler.
   */
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({
      MaxUploadSizeExceededException.class
      })
  public Object maxFileExceededHandler(final Exception exception) {
    return new LinkedHashMap<String, String>() {{
          put("code", "max_file_exceeded");
          put("message", exception.getClass().getSimpleName());
          put("description", exception.getMessage());
        }
    };
  }

  /**
   * fileNotFoundHandler handler.
   */
  @ResponseBody
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler({
       FileNotFoundException.class})
  public Object fileNotFoundHandler(final Exception exception) {
    return new LinkedHashMap<String, String>() {{
          put("code", "file_not_found");
          put("message", exception.getClass().getSimpleName());
          put("description", exception.getMessage());
        }
    };
  }

  /**
   * missingParameterHandler handler.
   */
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public Object missingParameterHandler(final Exception exception) {
    return new LinkedHashMap<String, String>() {{
          put("code", "required_parameter_missing");
          put("message", exception.getClass().getSimpleName());
          put("description", exception.getMessage());
        }
    };
  }

  /**
   * unsatisfiedParameterHandler handler.
   */
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({
      UnsatisfiedServletRequestParameterException.class,
      IllegalArgumentException.class,
      MultipartException.class
      })
  public Object unsatisfiedParameterHandler(final Exception exception) {
    return new LinkedHashMap<String, String>() {{
          put("code", "unsatisfied_request_parameter");
          put("message", exception.getClass().getSimpleName());
          put("description", exception.getMessage());
        }
    };
  }

  /**
   * missingResourceHandler handler.
   */
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({
      MissingResourceException.class
      })
  public Object missingResourceHandler(final Exception exception) {
    return new LinkedHashMap<String, String>() {{
          put("code", "missing_resource_parameter");
          put("message", exception.getClass().getSimpleName());
          put("description", exception.getMessage());
        }
    };
  }
}
