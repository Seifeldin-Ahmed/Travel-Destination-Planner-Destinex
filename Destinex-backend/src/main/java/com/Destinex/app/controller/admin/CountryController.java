package com.Destinex.app.controller.admin;

import com.Destinex.app.dto.output.CountryResponse;
import com.Destinex.app.service.CountryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin/countries")
@RestController
public class CountryController {

    private final CountryService service;

    public CountryController(CountryService service) {
        this.service = service;
    }

    @GetMapping
    public List<CountryResponse> getCountries() {
        return service.getCountries();
    }

    @GetMapping("/{name}")
    public List<CountryResponse> getCountries(@PathVariable String name) {
        return service.getCountryByName(name);
    }

}