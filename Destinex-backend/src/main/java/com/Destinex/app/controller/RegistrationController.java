package com.Destinex.app.controller;


import com.Destinex.app.config.exceptionhandling.EmailAlreadyExistsException;
import com.Destinex.app.dto.output.ApiResponse;
import com.Destinex.app.dto.input.SignupRequest;
import com.Destinex.app.entity.Client;
import com.Destinex.app.service.ClientService;
import jakarta.validation.Valid;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
public class RegistrationController {

    private ClientService clientService;

    @Autowired
    public RegistrationController(ClientService userService) {
        this.clientService = userService;
    }


    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> SignUp(@Valid @RequestBody SignupRequest theSignedUser, BindingResult theBindingResult) {
        if(theBindingResult.hasErrors()){
            throw new ValidationException("data is not valid");
        }
        // check the database if user already exists
        String email = theSignedUser.getEmail();
        Client exisitngClient = clientService.findClientByEmail(email);
        if(exisitngClient != null){
            throw new EmailAlreadyExistsException("email already exists");
        }
        clientService.save(theSignedUser);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("success", "Client registered"));
    }

}

