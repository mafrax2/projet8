package com.openclassrooms.tourguide.controller.dao;

import com.openclassrooms.tourguide.model.beans.ProviderBean;
import com.openclassrooms.tourguide.proxies.TriPricerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class TripPricerDao {

    @Autowired
    private TriPricerProxy triPricerProxy;

    @RequestMapping("/getTripPrice")
    public List<ProviderBean> getTripPrice(@RequestParam String apiKey, @RequestParam UUID attractionId, @RequestParam int adults, @RequestParam int children, @RequestParam int nightsStay, @RequestParam int rewardsPoints){
        List<ProviderBean> tripPrice = triPricerProxy.getTripPrice(apiKey, attractionId, adults, children, nightsStay, rewardsPoints);
        return tripPrice;
    }

}
