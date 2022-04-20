package com.openclassrooms.gpsUtilMs.controller;

import com.openclassrooms.gpsUtilMs.service.GpsUtilService;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class GpsUtilController {

    private GpsUtilService service;


    public GpsUtilController(GpsUtilService service) {
        this.service = service;
    }

    @RequestMapping("/getUserLocation")
    public VisitedLocation getUserLocation(@RequestParam UUID userId) throws Exception {
        VisitedLocation userLocation = service.getUserLocation(userId);
        return userLocation;
    }

    @RequestMapping("/getAttractions")
    public List<Attraction> getAttractions(){
        return service.getAttractions();
    }
}
