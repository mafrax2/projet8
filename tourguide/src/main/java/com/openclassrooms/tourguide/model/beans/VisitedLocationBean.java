package com.openclassrooms.tourguide.model.beans;

import java.util.Date;
import java.util.UUID;

public class VisitedLocationBean {
    public  UUID userId;
    public  LocationBean location;
    public  Date timeVisited;

    public VisitedLocationBean(UUID userId, LocationBean location, Date timeVisited) {
        this.userId = userId;
        this.location = location;
        this.timeVisited = timeVisited;
    }

    public VisitedLocationBean() {
    }
}
