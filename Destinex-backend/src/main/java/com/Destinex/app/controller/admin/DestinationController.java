package com.Destinex.app.controller.admin;

import com.Destinex.app.dto.input.DestinationRequest;
import com.Destinex.app.dto.output.ApiResponse;
import com.Destinex.app.service.DestinationService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin/destination")
@RestController("adminDestinationController")
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @PostMapping("/countries")
    public ResponseEntity<ApiResponse> createDestinations(@Valid @RequestBody List<DestinationRequest> theDestinations, BindingResult theBindingResult)  {
        if(theBindingResult.hasErrors()){
            System.out.println(theBindingResult.getAllErrors());
            throw new ValidationException("data is not valid");
        }
        destinationService.save(theDestinations);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("success", "destination created"));
    }

    @PostMapping("/country")
    public ResponseEntity<ApiResponse> createDestination(@Valid @RequestBody DestinationRequest theDestination, BindingResult theBindingResult){
        if(theBindingResult.hasErrors()){
            throw new ValidationException("data is not valid");
        }
        destinationService.save(theDestination);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("success", "destination created"));
    }

    @PutMapping("/{destinationId}")
    public ResponseEntity<ApiResponse> updateDestination(@PathVariable int destinationId, @Valid @RequestBody DestinationRequest theDestination, BindingResult theBindingResult) {
        if(theBindingResult.hasErrors() || destinationId <= 0){
            throw new ValidationException("data is not valid");
        }
        destinationService.update(theDestination, destinationId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("success", "destination updated"));
    }

    @DeleteMapping("/{destinationId}")
    public ResponseEntity<ApiResponse> deleteDestinationId(@PathVariable int destinationId) {
        if (destinationId <= 0) {
            throw new ValidationException("data is not valid");
        }
        destinationService.deleteById(destinationId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("success", "destination deleted"));
    }

}
