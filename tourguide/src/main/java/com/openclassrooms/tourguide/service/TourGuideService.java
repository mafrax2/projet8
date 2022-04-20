package com.openclassrooms.tourguide.service;


import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.model.beans.AttractionBean;
import com.openclassrooms.tourguide.model.beans.LocationBean;
import com.openclassrooms.tourguide.model.beans.ProviderBean;
import com.openclassrooms.tourguide.model.beans.VisitedLocationBean;
import com.openclassrooms.tourguide.model.user.User;
import com.openclassrooms.tourguide.model.user.UserReward;
import com.openclassrooms.tourguide.proxies.GpsUtilProxy;
import com.openclassrooms.tourguide.proxies.TriPricerProxy;
import com.openclassrooms.tourguide.tracker.Tracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class TourGuideService {
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);

	private final RewardsService rewardsService;
	private final GpsUtilProxy gpsUtil;

	private final TriPricerProxy tripPricer;
	public final Tracker tracker;
	boolean testMode = true;
	
	public TourGuideService(GpsUtilProxy gpsUtil, RewardsService rewardsService, TriPricerProxy tripPricer) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
		this.tripPricer = tripPricer;

		if(testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}
	
	public VisitedLocationBean getUserLocation(User user) throws ExecutionException, InterruptedException {
		VisitedLocationBean visitedLocation = (user.getVisitedLocations().size() > 0) ?
			user.getLastVisitedLocation() :
			trackUserLocation(user);
		return visitedLocation;
	}
	
	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}
	
	public List<User> getAllUsers() {
		return internalUserMap.values().stream().collect(Collectors.toList());
	}
	
	public void addUser(User user) {
		if(!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}
	
	public List<ProviderBean> getTripDeals(User user) {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<ProviderBean> providers = tripPricer.getTripPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}
	
	public VisitedLocationBean trackUserLocation(User user) throws ExecutionException, InterruptedException {

			VisitedLocationBean visitedLocation = gpsUtil.getUserLocation(user.getUserId());
			user.addToVisitedLocations(visitedLocation);
//		rewardsService.calculateRewards(user);


		CompletableFuture.runAsync(() -> rewardsService.calculateRewards(user)).completeAsync(()-> null);

		return visitedLocation;
	}


	public List<AttractionBean> getNearByAttractions(VisitedLocationBean visitedLocation) {
		List<AttractionBean> nearbyAttractions = new ArrayList<>();
		for(AttractionBean attraction : gpsUtil.getAttractions()) {
			if(rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
				nearbyAttractions.add(attraction);
			}
		}
		
		return nearbyAttractions;
	}
	
	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() { 
		      public void run() {
		        tracker.stopTracking();
		      } 
		    }); 
	}
	
	/**********************************************************************************
	 * 
	 * Methods Below: For Internal Testing
	 * 
	 **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";
	// Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
	private final Map<String, User> internalUserMap = new HashMap<>();
	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);
			
			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}
	
	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i-> {
			user.addToVisitedLocations(new VisitedLocationBean(user.getUserId(), new LocationBean(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}
	
	private double generateRandomLongitude() {
		double leftLimit = -180;
	    double rightLimit = 180;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
	    double rightLimit = 85.05112878;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
	    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}
	
}
