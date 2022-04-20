package com.openclassrooms.gpsUtilMs.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GpsUtilService {

    private static GpsUtil gpsUtil;


    public GpsUtilService() {
        this.gpsUtil = new GpsUtil();

    }

    public GpsUtilService(GpsUtil gpsUtil) {
        this.gpsUtil = gpsUtil;
    }

    public VisitedLocation getUserLocation(UUID userId) throws Exception {
        try {
        VisitedLocation userLocation = gpsUtil.getUserLocation(userId);
        return userLocation;
        } catch(Exception e) {
            throw new Exception(e);
        }
    }

    public List<Attraction> getAttractions(){
        List<Attraction> attractions = gpsUtil.getAttractions();
        return attractions;
    }

}
