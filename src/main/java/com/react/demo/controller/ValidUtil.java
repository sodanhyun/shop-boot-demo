package com.react.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

public class ValidUtil {

    static ResponseEntity<String> getStringResponseEntity(BindingResult bindingResult) {
        StringBuilder sb = new StringBuilder();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        for (FieldError fieldError : fieldErrors) {
            sb.append(fieldError.getDefaultMessage());
        }

        return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
    }
}
