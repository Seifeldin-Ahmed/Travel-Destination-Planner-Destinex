package com.Destinex.app.service;

import com.Destinex.app.dao.DestinationDao;
import com.Destinex.app.dao.DestinationJdbcRepository;
import com.Destinex.app.dto.input.DestinationRequest;
import com.Destinex.app.dto.output.PageResponse;
import com.Destinex.app.entity.Destination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class DestinationServiceImpl implements DestinationService {
    private final DestinationDao destinationDao;
    private final DestinationJdbcRepository destinationJdbcRepository;

    public DestinationServiceImpl(DestinationDao destinationDao, DestinationJdbcRepository destinationJdbcRepository) {
        this.destinationDao = destinationDao;
        this.destinationJdbcRepository = destinationJdbcRepository;
    }


    @Override
    public List<Destination> findWishList(int userId) {
        return destinationDao.findWishList(userId);
    }

    @Override
    public PageResponse findDestinationsPerPage(int page) {
        return destinationDao.findDestinationsPerPage(page);
    }

    @Override
    public Destination findDestination(int id) {
        return destinationDao.findDestination(id);
    }

    @Override
    public List<Destination> findDestinationByCountryName(String countryName) {
        return destinationDao.findDestinationByCountryName(countryName);
    }

    @Override
    @Transactional
    public void addToWishList(int userId, int destinationId) {
        destinationDao.addToWishList(userId,destinationId);
    }

    @Override
    @Transactional
    public void deleteFromWishList(int userId, int destinationId) {
        destinationDao.deleteFromWishList(userId,destinationId);

    }

    @Override
    @Transactional
    public void deleteAllFromWishList(int userId) {
        destinationDao.deleteAllFromWishList(userId);

    }


    @Override
    @Transactional
    public void save(DestinationRequest destinationRequest) {

        Destination theNewDestination = new Destination( destinationRequest.getName(),
                                                         destinationRequest.getCapital(),
                                                         destinationRequest.getRegion(),
                                                         destinationRequest.getPopulation(),
                                                         destinationRequest.getCurrency(),
                                                         destinationRequest.getFlag());
        destinationDao.save(theNewDestination);
    }

    @Override
    @Transactional
    public void save(List<DestinationRequest> destinationsRequest) {
        List<Destination> theNewDestinations = new ArrayList<>();

        for (DestinationRequest destinationRequest : destinationsRequest){
            Destination theNewDestination = new Destination( destinationRequest.getName(),
                    destinationRequest.getCapital(),
                    destinationRequest.getRegion(),
                    destinationRequest.getPopulation(),
                    destinationRequest.getCurrency(),
                    destinationRequest.getFlag());
            theNewDestinations.add(theNewDestination);
        }
        destinationJdbcRepository.batchInsert(theNewDestinations);
    }

    @Override
    @Transactional
    public void update(DestinationRequest destinationRequest, int destinationId) {
        Destination exisitngDestination = destinationDao.findDestination(destinationId);

        exisitngDestination.setCountry(destinationRequest.getName());
        exisitngDestination.setCapital(destinationRequest.getCapital());
        exisitngDestination.setRegion(destinationRequest.getRegion());
        exisitngDestination.setPopulation(destinationRequest.getPopulation());
        exisitngDestination.setCurrency(destinationRequest.getCurrency());
        exisitngDestination.setImageUrl(destinationRequest.getFlag());
        destinationDao.save(exisitngDestination);
    }

    @Override
    @Transactional
    public void deleteById(int id)  {
        destinationDao.deleteById(id);
    }

}
