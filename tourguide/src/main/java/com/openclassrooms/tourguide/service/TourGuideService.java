package com.openclassrooms.tourguide.service;


import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.model.RewardTuple;
import com.openclassrooms.tourguide.model.beans.AttractionBean;
import com.openclassrooms.tourguide.model.beans.LocationBean;
import com.openclassrooms.tourguide.model.beans.ProviderBean;
import com.openclassrooms.tourguide.model.beans.VisitedLocationBean;
import com.openclassrooms.tourguide.model.user.User;
import com.openclassrooms.tourguide.model.user.UserReward;
import com.openclassrooms.tourguide.proxies.GpsUtilProxy;
import com.openclassrooms.tourguide.proxies.TriPricerProxy;
import feign.RetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class TourGuideService {
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);

	private final RewardsService rewardsService;
	private final GpsUtilProxy gpsUtil;

	private final TriPricerProxy tripPricer;

	boolean testMode = true;

	private ExecutorService executor = Executors.newFixedThreadPool(2000);
	
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

	public Map<UUID, LocationBean> getAllUsersLastVisitedLocation(){
		List<User> allUsers = getAllUsers();
		HashMap<UUID, LocationBean> map = new HashMap<>();
		allUsers.forEach(u-> map.put(u.getUserId(), u.getLastVisitedLocation().location));
		return map;
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

	public void trackAllUserLocation(List<User> users) throws ExecutionException, InterruptedException {

		List<CompletableFuture> rewardFutures = new ArrayList<>();

		for (User user: users
			 ) {
			CompletableFuture future = CompletableFuture.supplyAsync(() ->
						gpsUtil.getUserLocation(user.getUserId())
					)
					.thenAccept(v -> {
						user.addToVisitedLocations((v));
					})
					.thenRun(() -> {
						try {
							List<RewardTuple> rewardTuples = rewardsService.calculateRewards(user);
							rewardTuples.stream().forEach(t -> {
								try {
									user.addUserReward(new UserReward(t.getVisitedLocationBean(), t.getAttractionBean(), rewardsService.getRewardPoints(t.getAttractionBean(), user)));
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							});
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
						}
					});

			rewardFutures.add(future);
		}

		CompletableFuture<Void> allFutures = CompletableFuture
				.allOf(rewardFutures.toArray(new CompletableFuture[rewardFutures.size()]));


		allFutures.get();
		System.out.println("lol");

	}

	public VisitedLocationBean getUserLocation(UUID userId){
		VisitedLocationBean userLocation= new VisitedLocationBean();
		try {
			userLocation = gpsUtil.getUserLocation(userId);
		} catch (RetryableException e){
			e.retryAfter();
		}
		return userLocation;
	}

	public VisitedLocationBean trackUserLocation(User user) throws ExecutionException, InterruptedException {

		VisitedLocationBean visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		List<RewardTuple> rewardTuples = rewardsService.calculateRewards(user);
		rewardTuples.stream().forEach(t -> {
			try {
				user.addUserReward(new UserReward(t.getVisitedLocationBean(), t.getAttractionBean(), rewardsService.getRewardPoints(t.getAttractionBean(), user)));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});


		return visitedLocation;
	}


	public List<AttractionBean> getNearByAttractions(VisitedLocationBean visitedLocation) {

		List<AttractionBean> nearbyAttractions = gpsUtil.getAttractions().stream().sorted((a1, a2) -> rewardsService.getDistance(a1, visitedLocation.location).compareTo(rewardsService.getDistance(a2, visitedLocation.location))).collect(Collectors.toList()).subList(0, 5);
		return nearbyAttractions;
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
		logger.info("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
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
