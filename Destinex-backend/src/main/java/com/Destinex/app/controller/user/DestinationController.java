package com.Destinex.app.controller.user;

import com.Destinex.app.dto.output.ApiResponse;
import com.Destinex.app.dto.output.PageResponse;
import com.Destinex.app.entity.Destination;
import com.Destinex.app.service.DestinationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/destination")
@RestController("userDestinationController")
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @GetMapping("/wishlist")
    public List<Destination> findWishList(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        return  destinationService.findWishList(userId);
    }


    @GetMapping
    public PageResponse findDestinationsPerPage(@RequestParam(defaultValue = "0") int page) {
        return  destinationService.findDestinationsPerPage(page);
    }

    @GetMapping("/countryName/{countryName}")
    public List<Destination> findDestinationByCountryName(@PathVariable String countryName) {
        if (countryName == null || countryName.isEmpty()) {
            throw new ValidationException("data is not valid");
        }
        return  destinationService.findDestinationByCountryName(countryName);
    }

    @GetMapping("/{destinationId}")
    public Destination findDestination(@PathVariable int destinationId) {
        if (destinationId <= 0) {
            throw new ValidationException("data is not valid");
        }
        return  destinationService.findDestination(destinationId);
    }

    @PostMapping("/wishlist/{destinationId}")
    public  ResponseEntity<ApiResponse>  addToWishList(@PathVariable int destinationId, HttpServletRequest request) {
        if (destinationId <= 0) {
            throw new ValidationException("data is not valid");
        }
        Integer userId = (Integer) request.getAttribute("userId");
        destinationService.addToWishList(userId,destinationId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("success", "destination added to wishlist successfully"));
    }


    @DeleteMapping("/wishlist/{destinationId}")
    public  ResponseEntity<ApiResponse>  deleteFromWishList(@PathVariable int destinationId, HttpServletRequest request) {
        System.out.println("hello");
        if (destinationId <= 0) {
            throw new ValidationException("data is not valid");
        }
        Integer userId = (Integer) request.getAttribute("userId");
        destinationService.deleteFromWishList(userId,destinationId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("success", "destination deleted from wishlist successfully"));
    }


    @DeleteMapping("/wishlist")
    public  ResponseEntity<ApiResponse> deleteAllFromWishList(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        destinationService.deleteAllFromWishList(userId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("success", "all destinations deleted successfully"));
    }

}
