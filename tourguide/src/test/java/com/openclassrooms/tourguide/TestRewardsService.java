package com.openclassrooms.tourguide;

import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.model.beans.AttractionBean;
import com.openclassrooms.tourguide.model.beans.VisitedLocationBean;
import com.openclassrooms.tourguide.model.user.User;
import com.openclassrooms.tourguide.model.user.UserReward;
import com.openclassrooms.tourguide.proxies.GpsUtilProxy;
import com.openclassrooms.tourguide.proxies.RewardCentralProxy;
import com.openclassrooms.tourguide.proxies.TriPricerProxy;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestRewardsService {


	@Autowired
	private GpsUtilProxy gpsUtil;

	@Autowired
	private RewardCentralProxy rewardCentralProxy;

	@Autowired
	private TriPricerProxy triPricerProxy;

	@Test
	public void userGetRewards() throws ExecutionException, InterruptedException {

		RewardsService rewardsService = new RewardsService(gpsUtil, rewardCentralProxy);

		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, triPricerProxy);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		AttractionBean attraction = gpsUtil.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocationBean(user.getUserId(), attraction, new Date()));
		tourGuideService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();
//		tourGuideService.tracker.stopTracking();
		assertTrue(userRewards.size() == 1);
	}

	//	@Ignore // Needs fixed - can throw ConcurrentModificationException
	@Test
	public void isWithinAttractionProximity() {

		RewardsService rewardsService = new RewardsService(gpsUtil, rewardCentralProxy);
		AttractionBean attraction = gpsUtil.getAttractions().get(0);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}
	
//	@Ignore // Needs fixed - can throw ConcurrentModificationException
	@Test
	public void nearAllAttractions() throws ExecutionException, InterruptedException {

		RewardsService rewardsService = new RewardsService(gpsUtil, rewardCentralProxy);
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(1);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, triPricerProxy);
		
		rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
		List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));
//		tourGuideService.tracker.stopTracking();

		assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
	}
	
}
