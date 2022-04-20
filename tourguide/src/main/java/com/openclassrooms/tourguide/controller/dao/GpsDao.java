package com.openclassrooms.tourguide.controller.dao;

import com.openclassrooms.tourguide.model.beans.AttractionBean;
import com.openclassrooms.tourguide.model.beans.VisitedLocationBean;
import com.openclassrooms.tourguide.proxies.GpsUtilProxy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class GpsDao {

    private final GpsUtilProxy gpsUtilProxy;

    public GpsDao(GpsUtilProxy gpsUtilProxy) {
        this.gpsUtilProxy = gpsUtilProxy;
    }

    @RequestMapping("/getAttractions")
    public List<AttractionBean> getAllAttractions(){
        List<AttractionBean> allAttractions = gpsUtilProxy.getAttractions();
        return allAttractions;
    }

    @RequestMapping("/getUserLocation")
    public VisitedLocationBean getLocation(@RequestParam UUID userId){

        VisitedLocationBean location = gpsUtilProxy.getUserLocation(userId);
        return location;
    }





}
