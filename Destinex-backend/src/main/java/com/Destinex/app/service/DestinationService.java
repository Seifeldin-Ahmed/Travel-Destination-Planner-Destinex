package com.Destinex.app.service;

import com.Destinex.app.dto.input.DestinationRequest;
import com.Destinex.app.dto.output.PageResponse;
import com.Destinex.app.entity.Destination;

import java.util.List;


public interface DestinationService {

    List<Destination> findWishList(int userId);

    PageResponse findDestinationsPerPage(int page);

    Destination findDestination(int id);

    List<Destination> findDestinationByCountryName(String countryName);

    void addToWishList(int userId, int destinationId);

    void deleteFromWishList(int userId, int destinationId);

    void deleteAllFromWishList(int userId);

    void save(DestinationRequest destinationRequest);

    void save(List<DestinationRequest> destinationsRequest);

    void update(DestinationRequest destinationRequest, int destinationId);

    void deleteById(int id);

}


