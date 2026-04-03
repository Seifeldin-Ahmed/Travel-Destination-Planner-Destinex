package com.Destinex.app.dto.input;


import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;


public class DestinationRequest {

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String name;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String capital;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String region;

    @NotNull(message = "is required")
    @Min(value = 1, message = "must be greater than or equal to 1")
    private long population;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String currency;

    @Pattern(regexp = "^(https?|ftp)://.*\\.(png|jpg|jpeg|gif|bmp)$",
            message = "Invalid image URL")
    private String flag;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public long getPopulation() {
        return population;
    }

    public void setPopulation(long population) {
        this.population = population;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
