package com.openclassrooms.trippricer.contoller;

import com.jsoniter.output.JsonStream;
import com.openclassrooms.trippricer.service.TrippricerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class TrippricerController {

    private TrippricerService service;


    public TrippricerController(TrippricerService service) {
        this.service = service;
    }

    @RequestMapping("/getTripPrice")
    public String getTripPrice(@RequestParam String apiKey,@RequestParam UUID attractionId,@RequestParam int adults,@RequestParam int children,@RequestParam int nightsStay,@RequestParam int rewardsPoints){
        return JsonStream.serialize(service.getPrice(apiKey, attractionId, adults, children, nightsStay, rewardsPoints));
    }

}
