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
import java.util.concurrent.*;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
	private final GpsUtilProxy gpsUtil;
	private final RewardCentralProxy rewardsCentral;

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;

	private List<AttractionBean> attractions;

	
	public RewardsService(GpsUtilProxy gpsUtil, RewardCentralProxy rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
		this.attractions = gpsUtil.getAttractions();
	}
	
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	public void calculateRewards(User user) {
		List<VisitedLocationBean> userLocations = user.getVisitedLocations();

//		CopyOnWriteArrayList<UserReward> userRewards = new CopyOnWriteArrayList<>();
//		userRewards.addAll(user.getUserRewards());

//		for(VisitedLocationBean visitedLocation : userLocations) {
//			}
		userLocations.parallelStream().forEach( visitedLocation ->{
			AttractionBean attractionBean = nearAttraction(visitedLocation);
			if(attractionBean!=null) {
				user.addUserReward(new UserReward(visitedLocation, attractionBean, getRewardPoints(attractionBean, user)));
				System.out.println("adding reward to thread: " + Thread.currentThread().getName());
			}
				}
		);


//		return userRewards;
	}

	public void calculateAllRewards(List<User> users) throws ExecutionException, InterruptedException {

		List<CompletableFuture> rewardFutures = new CopyOnWriteArrayList<>();

		ExecutorService pool = Executors.newFixedThreadPool(10);

		users.stream().forEach(u -> {
			rewardFutures.add(CompletableFuture.runAsync(() -> calculateRewards(u)));});

		CompletableFuture<Void> allFutures = CompletableFuture
				.allOf(rewardFutures.toArray(new CompletableFuture[rewardFutures.size()]));

		allFutures.get();

		System.out.println("lol");
	}

	private void filterRewards(List<VisitedLocationBean> userLocations, List<AttractionBean> attractions, User user) {



}
	
	public boolean isWithinAttractionProximity(AttractionBean attraction, LocationBean location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}
	
	private boolean nearAttraction(VisitedLocationBean visitedLocation, AttractionBean attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}

	private AttractionBean nearAttraction(VisitedLocationBean visitedLocation) {
		AttractionBean attractionBean = attractions.parallelStream().filter(a -> getDistance(a, visitedLocation.location) > proximityBuffer ? false : true).findAny().orElse(null);
		return attractionBean;
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
