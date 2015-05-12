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
    protected ResponseEntity<Object> handleInvalidRequest(RuntimeException e,
                                                          WebRequest request) {
        InvalidHarvesterException ire = (InvalidHarvesterException) e;
        List<FieldErrorResource> fieldErrorResources = new ArrayList<>();

        List<String> fieldErrors = ire.getErrors();
        for (String fieldError : fieldErrors) {
            FieldErrorResource fieldErrorResource = new FieldErrorResource();
//            fieldErrorResource.setResource(fieldError.getObjectName());
//            fieldErrorResource.setField(fieldError.getField());
//            fieldErrorResource.setCode(fieldError.getCode());
            fieldErrorResource.setMessage(fieldError);
            fieldErrorResources.add(fieldErrorResource);
        }

        ErrorResource error = new ErrorResource("InvalidRequest", ire.getMessage());
        error.setFieldErrors(fieldErrorResources);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return handleExceptionInternal(e, error, headers,
                HttpStatus.BAD_REQUEST, request);
    }
}