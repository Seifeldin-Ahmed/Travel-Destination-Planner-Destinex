package com.Destinex.app.dao;

import com.Destinex.app.dto.output.PageResponse;
import com.Destinex.app.entity.Destination;

import java.util.List;


public interface DestinationDao {

    List<Destination> findWishList(int userId);

    PageResponse findDestinationsPerPage(int page);

    Destination findDestination(int id);

    List<Destination> findDestinationByCountryName(String countryName);

    void addToWishList(int userId, int destinationId);

    void deleteFromWishList(int userId, int destinationId);

    void deleteAllFromWishList(int userId);

    void save(Destination theDestination);

    void deleteById(int id);
}
