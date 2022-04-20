package com.openclassrooms.tourguide.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name="microservice-reward", url = "${rewardms.entry.port}")
public interface RewardCentralProxy {


    @RequestMapping("/rewardPoints")
    public int getAttractionRewardPoints(@RequestParam UUID attractionId,@RequestParam UUID userId);

}
