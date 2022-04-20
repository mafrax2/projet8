package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.model.beans.AttractionBean;
import com.openclassrooms.tourguide.model.beans.LocationBean;
import com.openclassrooms.tourguide.model.beans.VisitedLocationBean;
import com.openclassrooms.tourguide.model.user.User;
import com.openclassrooms.tourguide.model.user.UserReward;
import com.openclassrooms.tourguide.proxies.GpsUtilProxy;
import com.openclassrooms.tourguide.proxies.RewardCentralProxy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
	private final GpsUtilProxy gpsUtil;
	private final RewardCentralProxy rewardsCentral;

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;

	private AtomicReference<List<UserReward>> userRewards;

	
	public RewardsService(GpsUtilProxy gpsUtil, RewardCentralProxy rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}
	
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	public void calculateRewards(User user) {
		List<VisitedLocationBean> userLocations = user.getVisitedLocations();
		List<AttractionBean> attractions = gpsUtil.getAttractions();

		for(VisitedLocationBean visitedLocation : userLocations) {
			for(AttractionBean attraction : attractions) {
				if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
					if(nearAttraction(visitedLocation, attraction)) {
						user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
					}
				}
			}
		}

	}

	private void filterRewards(List<VisitedLocationBean> userLocations, List<AttractionBean> attractions, User user) {

		for(VisitedLocationBean visitedLocation : userLocations) {
			attractions.stream().parallel().filter(a -> nearAttraction(visitedLocation,a)).forEach(a-> user.addUserReward(new UserReward(visitedLocation, a, getRewardPoints(a, user))));

		}

		Predicate<AttractionBean> notIn1 = s -> user.getUserRewards().stream().noneMatch(mc -> mc.attraction.attractionName.equals(s.attractionName));
		List<AttractionBean> attractionsNotinUserReWards = attractions.stream().filter(notIn1).collect(Collectors.toList());

		Predicate<AttractionBean> near = s -> userLocations.stream().allMatch(mc -> nearAttraction(mc, s));
		List<AttractionBean> attractionsNearbyNotInUserRewards = attractionsNotinUserReWards.stream().filter(near).collect(Collectors.toList());



}
	
	public boolean isWithinAttractionProximity(AttractionBean attraction, LocationBean location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}
	
	private boolean nearAttraction(VisitedLocationBean visitedLocation, AttractionBean attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}
	
	private int getRewardPoints(AttractionBean attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}
	
	public double getDistance(LocationBean loc1, LocationBean loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}

}
