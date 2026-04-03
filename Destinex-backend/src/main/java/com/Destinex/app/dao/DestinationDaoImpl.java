package com.Destinex.app.dao;

import com.Destinex.app.config.exceptionhandling.ResourceNotFoundException;
import com.Destinex.app.dto.output.PageResponse;
import com.Destinex.app.entity.Destination;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public class DestinationDaoImpl implements DestinationDao {

    private final EntityManager entityManager;
    private final int ELEMENTS_PER_PAGE = 10;

    public DestinationDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Override
    public List<Destination> findWishList(int userId) {

        TypedQuery<Destination> theQuery = entityManager.createQuery("FROM Destination d Join d.users u where u.id = :userId ", Destination.class); // ?????????????????
        theQuery.setParameter("userId", userId);
        List<Destination> destinations = null;
        try {
            destinations = theQuery.getResultList();
        } catch (Exception e) {
            throw new ResourceNotFoundException("destination not found");
        }
        return destinations;
    }

    @Override
    public PageResponse findDestinationsPerPage(int page) {
        TypedQuery<Destination> theQuery = entityManager.createQuery("FROM Destination", Destination.class);

        theQuery.setFirstResult(page * ELEMENTS_PER_PAGE); // offset
        theQuery.setMaxResults(ELEMENTS_PER_PAGE);          // limit

        List<Destination> destinations =  theQuery.getResultList();
        long count = entityManager.createQuery("SELECT COUNT(d) FROM Destination d", Long.class).getSingleResult();
        return new PageResponse(destinations,count, ELEMENTS_PER_PAGE);
    }

    @Override
    public List<Destination> findDestinationByCountryName(String countryName) {
        TypedQuery<Destination> theQuery  = entityManager.createQuery("FROM Destination d where d.country LIKE :countryName ", Destination.class);
        theQuery.setParameter("countryName", countryName + "%");
        List<Destination> destinations = null;
        try {
            destinations = theQuery.getResultList();
        } catch (Exception e) {
            throw new ResourceNotFoundException("destination not found");
        }
        return destinations;
    }


    @Override
    public Destination findDestination(int id) {
        Destination destination = entityManager.find(Destination.class,id);
        if (destination == null){
            throw new ResourceNotFoundException("destination not found");
        }
        return destination;
    }

    @Override
    public void addToWishList(int userId, int destinationId) {
        this.findDestination(destinationId); // to check if the destination exist
        int rows = entityManager.createNativeQuery("INSERT INTO wishlist(destination_id, user_id) VALUES (:destId, :userId)")
                     .setParameter("destId", destinationId)
                     .setParameter("userId", userId)
                     .executeUpdate();
        if (rows == 0) {
            throw new RuntimeException("server not responding");
        }
    }

    @Override
    public void deleteFromWishList(int userId, int destinationId) {
        this.findDestination(destinationId); // to check if the destination exist
        int rows = entityManager.createNativeQuery("DELETE From wishlist where  wishlist.destination_id =  :destId")
                .setParameter("destId", destinationId)
                .executeUpdate();
        if (rows == 0) {
            throw new RuntimeException("server not responding");
        }
    }

    @Override
    public void deleteAllFromWishList(int userId) {
        int rows = entityManager.createNativeQuery("DELETE From wishlist where  wishlist.user_id =  :userId")
                .setParameter("userId", userId)
                .executeUpdate();
        if (rows == 0) {
            throw new RuntimeException("server not responding");
        }
    }

    @Override
    public void save(Destination theDestination) {
        if (theDestination.getId() == 0) {
            entityManager.persist(theDestination);
        } else {
            entityManager.merge(theDestination);
        }
    }



    @Override
    public void deleteById(int id) {
      Destination destination = this.findDestination(id);
      entityManager.remove(destination);
    }

}