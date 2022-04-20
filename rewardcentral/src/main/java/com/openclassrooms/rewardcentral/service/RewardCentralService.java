package com.openclassrooms.rewardcentral.service;

import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;

import java.util.UUID;

@Service
public class RewardCentralService {

    private RewardCentral rewardCentral;


    public RewardCentralService(RewardCentral rewardCentral) {
        this.rewardCentral = rewardCentral;
    }

    public RewardCentralService() {
        this.rewardCentral = new RewardCentral();
    }

    public int getAttractionRewardPoints(UUID attractionId, UUID userId) {
        return rewardCentral.getAttractionRewardPoints(attractionId, userId);
    }

}
