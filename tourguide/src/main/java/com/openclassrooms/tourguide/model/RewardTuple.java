package com.openclassrooms.tourguide.model;

import com.openclassrooms.tourguide.model.beans.AttractionBean;
import com.openclassrooms.tourguide.model.beans.VisitedLocationBean;


public class RewardTuple {

    private VisitedLocationBean visitedLocationBean;
    private AttractionBean attractionBean;

    public RewardTuple(VisitedLocationBean visitedLocationBean, AttractionBean attractionBean) {
        this.visitedLocationBean = visitedLocationBean;
        this.attractionBean = attractionBean;
    }

    public VisitedLocationBean getVisitedLocationBean() {
        return visitedLocationBean;
    }

    public void setVisitedLocationBean(VisitedLocationBean visitedLocationBean) {
        this.visitedLocationBean = visitedLocationBean;
    }

    public AttractionBean getAttractionBean() {
        return attractionBean;
    }

    public void setAttractionBean(AttractionBean attractionBean) {
        this.attractionBean = attractionBean;
    }
}
