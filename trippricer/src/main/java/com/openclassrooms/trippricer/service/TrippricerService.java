package com.openclassrooms.trippricer.service;

import org.springframework.stereotype.Service;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.List;
import java.util.UUID;

@Service
public class TrippricerService {

    private TripPricer tripPricer;


    public TrippricerService(TripPricer tripPricer) {
        this.tripPricer = tripPricer;
    }

    public TrippricerService() {
        this.tripPricer = new TripPricer();
    }

    public List<Provider> getPrice(String apiKey, UUID attractionId, int adults, int children, int nightsStay, int rewardsPoints){
        return tripPricer.getPrice(apiKey, attractionId, adults, children, nightsStay, rewardsPoints);
    }


}
