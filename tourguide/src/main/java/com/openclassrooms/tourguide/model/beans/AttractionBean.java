package com.openclassrooms.tourguide.model.beans;

import java.util.UUID;

public class AttractionBean extends LocationBean {

    public String attractionName;
    public String city;
    public String state;
    public UUID attractionId;

    public AttractionBean(String attractionName, String city, String state, double latitude, double longitude) {
        super(latitude, longitude);
        this.attractionName = attractionName;
        this.city = city;
        this.state = state;
        this.attractionId = UUID.randomUUID();
    }

}
