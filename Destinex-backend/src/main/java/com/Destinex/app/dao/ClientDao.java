package com.Destinex.app.dao;

import com.Destinex.app.entity.Client;

public interface ClientDao {

    Client findClientByEmail(String email);

    Client findClientById(Integer id);

    void save(Client theclient);

}
