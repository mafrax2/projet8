package com.openclassrooms.trippricer.contoller;

import com.openclassrooms.trippricer.service.TrippricerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tripPricer.Provider;

import java.util.List;
import java.util.UUID;

@RestController
public class TrippricerController {

    private TrippricerService service;


    public TrippricerController(TrippricerService service) {
        this.service = service;
    }

    @RequestMapping("/getTripPrice")
    public List<Provider> getTripPrice(@RequestParam String apiKey, @RequestParam UUID attractionId, @RequestParam int adults, @RequestParam int children, @RequestParam int nightsStay, @RequestParam int rewardsPoints){
        return service.getPrice(apiKey, attractionId, adults, children, nightsStay, rewardsPoints);
    }

}
