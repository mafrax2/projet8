package com.openclassrooms.tourguide.proxies;

import com.openclassrooms.tourguide.model.beans.AttractionBean;
import com.openclassrooms.tourguide.model.beans.VisitedLocationBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name="microservice-gps", url = "${gpsms.entry.port}")
public interface GpsUtilProxy {

    @RequestMapping("/getUserLocation")
    public VisitedLocationBean getUserLocation(@RequestParam UUID userId);

    @RequestMapping("/getAttractions")
    public List<AttractionBean> getAttractions();

}
