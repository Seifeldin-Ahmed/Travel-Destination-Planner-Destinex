package com.Destinex.app.service;

import com.Destinex.app.dto.input.SignupRequest;
import com.Destinex.app.entity.Client;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface ClientService extends UserDetailsService {

    Client findClientByEmail(String email);

    Client findClientById(Integer id);

    void save(SignupRequest signedUser);
}
