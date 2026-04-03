package com.Destinex.app.service;

import com.Destinex.app.dto.externalData.CountryApiResponse;
import com.Destinex.app.dto.externalData.Currency;
import com.Destinex.app.dto.output.CountryResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CountryServiceImpl implements CountryService{
    private final WebClient webClient;

    public CountryServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<CountryResponse> getCountries() {

        CountryApiResponse[] response = webClient.get()
                .uri("/all?fields=name,capital,region,population,currencies,flags")
                .retrieve()
                .bodyToMono(CountryApiResponse[].class)
                .block();

        if (response == null || response.length == 0) {
            throw new RuntimeException("No countries returned from API");
        }

        List<CountryResponse> result = new ArrayList<>();
        populateTheCountryResponse(result,response);
        return result;
    }

    @Override
    public  List<CountryResponse> getCountryByName(String countryName) {

        CountryApiResponse[] response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/name/{name}")
                        .queryParam("fields", "name,capital,region,population,currencies,flags")
                        .build(countryName))
                .retrieve()
                .bodyToMono(CountryApiResponse[].class)
                .block();

        if (response == null || response.length == 0) {
            throw new RuntimeException("Country not found: " + countryName);
        }

        List<CountryResponse> result = new ArrayList<>();
        populateTheCountryResponse(result,response);

        return result;
    }


    private void populateTheCountryResponse( List<CountryResponse> result,  CountryApiResponse[] response){
        for (CountryApiResponse retrievedCountry : response) {

            CountryResponse theCountryResoponse = new CountryResponse();

            // Get name
            theCountryResoponse.setName(retrievedCountry.getName().getCommon());

            // Get capital
            theCountryResoponse.setCapital(retrievedCountry.getCapital() != null && !retrievedCountry.getCapital().isEmpty() ? retrievedCountry.getCapital().getFirst() : "N/A");

            // Get region
            theCountryResoponse.setRegion(retrievedCountry.getRegion());

            // Get population
            theCountryResoponse.setPopulation(retrievedCountry.getPopulation());

            // Get currencies
            if (retrievedCountry.getCurrencies() != null && !retrievedCountry.getCurrencies().isEmpty()) {
                Map.Entry<String, Currency> entry = retrievedCountry.getCurrencies().entrySet().iterator().next();
                theCountryResoponse.setCurrency(entry.getKey() + " -- " + entry.getValue().getName());
            } else {
                theCountryResoponse.setCurrency("N/A");
            }

            // Get flags
            theCountryResoponse.setFlag(retrievedCountry.getFlags() != null && retrievedCountry.getFlags().getPng() != null ? retrievedCountry.getFlags().getPng() : "N/A");

            result.add(theCountryResoponse);
        }
    }
}