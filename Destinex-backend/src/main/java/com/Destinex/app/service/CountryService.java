package com.Destinex.app.service;

import com.Destinex.app.dto.output.CountryResponse;

import java.util.List;

public interface CountryService {

    List<CountryResponse> getCountries();

    List<CountryResponse> getCountryByName(String countryName);
}
