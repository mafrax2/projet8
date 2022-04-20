package com.openclassrooms.rewardcentral.controller;

import com.openclassrooms.rewardcentral.service.RewardCentralService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class RewardCentralController {

    private RewardCentralService service;

    public RewardCentralController(RewardCentralService service) {
        this.service = service;
    }

    @RequestMapping("/rewardPoints")
    public int getAttractionRewardPoints(@RequestParam UUID attractionId, @RequestParam UUID userId){
        return service.getAttractionRewardPoints(attractionId, userId);
    }


}
