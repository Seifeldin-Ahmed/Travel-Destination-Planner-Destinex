package com.Destinex.app.dao;

import com.Destinex.app.config.exceptionhandling.ResourceNotFoundException;
import com.Destinex.app.entity.Client;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

@Repository
public class ClientDaoImpl implements ClientDao {

    private final EntityManager entityManager;

    public ClientDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Client findClientByEmail(String email) {

        //create the query
        TypedQuery<Client> theQuery = entityManager.createQuery("from Client where email=:uEmail and enabled=true", Client.class);
        theQuery.setParameter("uEmail",email);

        Client theClient = null;
        try {
            theClient = theQuery.getSingleResult();
        } catch (Exception e) {
            theClient = null;
        }
        return theClient;
    }

    @Override
    public Client findClientById(Integer id) {
        Client client = entityManager.find(Client.class,id);
        if ( client == null){
            throw new ResourceNotFoundException("client not found");
        }
        return client;
    }

    @Override
    public void save(Client theclient) {
        entityManager.persist(theclient);
    }


}
