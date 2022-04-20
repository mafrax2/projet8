package com.openclassrooms.tourguide.model.beans;

import java.util.UUID;

public class TripPricerTaskBean {

    private final UUID attractionId;
    private final String apiKey;
    private final int adults;
    private final int children;
    private final int nightsStay;

    public TripPricerTaskBean(String apiKey, UUID attractionId, int adults, int children, int nightsStay) {
        this.apiKey = apiKey;
        this.attractionId = attractionId;
        this.adults = adults;
        this.children = children;
        this.nightsStay = nightsStay;
    }
}
