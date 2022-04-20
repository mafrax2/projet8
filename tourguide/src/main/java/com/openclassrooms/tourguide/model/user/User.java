package com.openclassrooms.tourguide.model.user;

import com.openclassrooms.tourguide.model.beans.ProviderBean;
import com.openclassrooms.tourguide.model.beans.VisitedLocationBean;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class User {
	private final UUID userId;
	private final String userName;
	private String phoneNumber;
	private String emailAddress;
	private Date latestLocationTimestamp;
	private List<VisitedLocationBean> visitedLocations = new ArrayList<>();
	private List<UserReward> userRewards = new ArrayList<>();
	private UserPreferences userPreferences = new UserPreferences();
	private List<ProviderBean> tripDeals = new ArrayList<>();
	public User(UUID userId, String userName, String phoneNumber, String emailAddress) {
		this.userId = userId;
		this.userName = userName;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
	}
	
	public UUID getUserId() {
		return userId;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}
	
	public void setLatestLocationTimestamp(Date latestLocationTimestamp) {
		this.latestLocationTimestamp = latestLocationTimestamp;
	}
	
	public Date getLatestLocationTimestamp() {
		return latestLocationTimestamp;
	}
	
	public void addToVisitedLocations(VisitedLocationBean visitedLocation) {
		visitedLocations.add(visitedLocation);
	}
	
	public List<VisitedLocationBean> getVisitedLocations() {
		return visitedLocations;
	}
	
	public void clearVisitedLocations() {
		visitedLocations.clear();
	}
	
	public List<UserReward> addUserReward(UserReward userReward) {
		if(userRewards.parallelStream().filter(r -> !r.attraction.attractionName.equals(userReward.attraction)).count() == 0) {
			userRewards.add(userReward);
		}
		return userRewards;
	}
	
	public List<UserReward> getUserRewards() {
		return userRewards;
	}
	
	public UserPreferences getUserPreferences() {
		return userPreferences;
	}
	
	public void setUserPreferences(UserPreferences userPreferences) {
		this.userPreferences = userPreferences;
	}

	public VisitedLocationBean getLastVisitedLocation() {
		return visitedLocations.get(visitedLocations.size() - 1);
	}
	
	public void setTripDeals(List<ProviderBean> tripDeals) {
		this.tripDeals = tripDeals;
	}
	
	public List<ProviderBean> getTripDeals() {
		return tripDeals;
	}

}
