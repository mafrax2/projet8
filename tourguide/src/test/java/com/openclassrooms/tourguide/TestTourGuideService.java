package com.openclassrooms.tourguide;

import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.model.beans.AttractionBean;
import com.openclassrooms.tourguide.model.beans.ProviderBean;
import com.openclassrooms.tourguide.model.beans.VisitedLocationBean;
import com.openclassrooms.tourguide.model.user.User;
import com.openclassrooms.tourguide.proxies.GpsUtilProxy;
import com.openclassrooms.tourguide.proxies.RewardCentralProxy;
import com.openclassrooms.tourguide.proxies.TriPricerProxy;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestTourGuideService {


    @Autowired
    private GpsUtilProxy gpsUtil;

    @Autowired
    private RewardCentralProxy rewardCentralProxy;

    @Autowired
    private TriPricerProxy triPricerProxy;

    @Test
    public void getUserLocation() throws ExecutionException, InterruptedException {

        RewardsService rewardsService = new RewardsService(gpsUtil, rewardCentralProxy);
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, triPricerProxy);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocationBean visitedLocation = tourGuideService.trackUserLocation(user);

        assertTrue(visitedLocation.userId.equals(user.getUserId()));
    }

    @Test
    public void addUser() {

        RewardsService rewardsService = new RewardsService(gpsUtil, rewardCentralProxy);
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, triPricerProxy);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        User retrivedUser = tourGuideService.getUser(user.getUserName());
        User retrivedUser2 = tourGuideService.getUser(user2.getUserName());


        assertEquals(user, retrivedUser);
        assertEquals(user2, retrivedUser2);
    }

    @Test
    public void getAllUsers() {

        RewardsService rewardsService = new RewardsService(gpsUtil, rewardCentralProxy);
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, triPricerProxy);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        List<User> allUsers = tourGuideService.getAllUsers();


        assertTrue(allUsers.contains(user));
        assertTrue(allUsers.contains(user2));
    }

    @Test
    public void trackUser() throws ExecutionException, InterruptedException {
        RewardsService rewardsService = new RewardsService(gpsUtil, rewardCentralProxy);
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, triPricerProxy);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocationBean visitedLocation = tourGuideService.trackUserLocation(user);


        assertEquals(user.getUserId(), visitedLocation.userId);
    }

    //@Ignore // Not yet implemented
    @Test
    public void getNearbyAttractions() throws ExecutionException, InterruptedException {
        RewardsService rewardsService = new RewardsService(gpsUtil, rewardCentralProxy);
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, triPricerProxy);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocationBean visitedLocation = tourGuideService.trackUserLocation(user);

        List<AttractionBean> attractions = tourGuideService.getNearByAttractions(visitedLocation);


        assertEquals(5, attractions.size());
    }

    @Test
    public void getTripDeals() {
        RewardsService rewardsService = new RewardsService(gpsUtil, rewardCentralProxy);
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, triPricerProxy);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        List<ProviderBean> providers = tourGuideService.getTripDeals(user);


        assertEquals(5, providers.size());
    }


}
