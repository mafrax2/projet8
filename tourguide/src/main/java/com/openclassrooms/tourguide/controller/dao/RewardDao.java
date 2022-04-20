package com.openclassrooms.tourguide.controller.dao;

import com.openclassrooms.tourguide.proxies.RewardCentralProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class RewardDao {

    @Autowired
    private RewardCentralProxy rewardCentralProxy;


    @RequestMapping("/rewardPoints")
    public int getAttractionRewardPoints(@RequestParam UUID attractionId, @RequestParam UUID userId){
        int attractionRewardPoints = rewardCentralProxy.getAttractionRewardPoints(attractionId, userId);
        return attractionRewardPoints;
    }

}
