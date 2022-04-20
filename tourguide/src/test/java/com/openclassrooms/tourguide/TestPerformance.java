package com.openclassrooms.tourguide;

import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.model.beans.AttractionBean;
import com.openclassrooms.tourguide.model.beans.VisitedLocationBean;
import com.openclassrooms.tourguide.model.user.User;
import com.openclassrooms.tourguide.proxies.GpsUtilProxy;
import com.openclassrooms.tourguide.proxies.RewardCentralProxy;
import com.openclassrooms.tourguide.proxies.TriPricerProxy;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestPerformance {
	
	/*
	 * A note on performance improvements:
	 *     
	 *     The number of users generated for the high volume tests can be easily adjusted via this method:
	 *     
	 *     		InternalTestHelper.setInternalUserNumber(100000);
	 *     
	 *     
	 *     These tests can be modified to suit new solutions, just as long as the performance metrics
	 *     at the end of the tests remains consistent. 
	 * 
	 *     These are performance metrics that we are trying to hit:
	 *     
	 *     highVolumeTrackLocation: 100,000 users within 15 minutes:
	 *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     *     highVolumeGetRewards: 100,000 users within 20 minutes:
	 *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */

	@Autowired
	private GpsUtilProxy gpsUtil;

	@Autowired
	private RewardCentralProxy rewardCentralProxy;

	@Autowired
	private TriPricerProxy triPricerProxy;


	//	@Ignore
	@Test
	public void highVolumeTrackLocation() throws ExecutionException, InterruptedException, Exception {
//		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, rewardCentralProxy);
		// Users should be incremented up to 100,000, and test finishes within 15 minutes
		InternalTestHelper.setInternalUserNumber(1000);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, triPricerProxy);

		List<User> allUsers = new ArrayList<>();
		allUsers = tourGuideService.getAllUsers();
		
	    StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		for(User user : allUsers) {
			tourGuideService.trackUserLocation(user);
		}


		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."); 
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}
	
//	@Ignore
	@Test
	public void highVolumeGetRewards() {

		RewardsService rewardsService = new RewardsService(gpsUtil, rewardCentralProxy);

		// Users should be incremented up to 100,000, and test finishes within 20 minutes
		InternalTestHelper.setInternalUserNumber(100);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, triPricerProxy);
		
	    AttractionBean attraction = gpsUtil.getAttractions().get(0);
		List<User> allUsers = new ArrayList<>();
		allUsers = tourGuideService.getAllUsers();
		allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocationBean(u.getUserId(), attraction, new Date())));
	     
	    allUsers.forEach(u -> rewardsService.calculateRewards(u));
	    
		for(User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}
		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."); 
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}
	
}
