package com.Destinex.app.config;

import com.Destinex.app.config.exceptionhandling.EmailAlreadyExistsException;
import com.Destinex.app.config.exceptionhandling.ResourceNotFoundException;
import com.Destinex.app.dto.output.ApiResponse;
import jakarta.validation.ValidationException;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PreAndPostProcessRequests {

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> resourceNotFoundExceptionHandler(ResourceNotFoundException exc){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", exc.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> EmailAlreadyExistsExceptionHandler(EmailAlreadyExistsException exc){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("error", exc.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse> validationExceptionHandler(ValidationException exc){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("error", exc.getMessage()));
    }

    // a global fallback handler.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> validationExceptionHandler(Exception exc){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("error", exc.getMessage()));
    }
}
