package org.daobs.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid harvester")
public class InvalidHarvesterException extends RuntimeException {
    private List<String> errors;

    public InvalidHarvesterException(List<String> errors) {
        super();
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}