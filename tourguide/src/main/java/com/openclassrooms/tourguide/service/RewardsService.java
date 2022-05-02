package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.model.RewardTuple;
import com.openclassrooms.tourguide.model.beans.AttractionBean;
import com.openclassrooms.tourguide.model.beans.LocationBean;
import com.openclassrooms.tourguide.model.beans.VisitedLocationBean;
import com.openclassrooms.tourguide.model.user.User;
import com.openclassrooms.tourguide.model.user.UserReward;
import com.openclassrooms.tourguide.proxies.GpsUtilProxy;
import com.openclassrooms.tourguide.proxies.RewardCentralProxy;
import feign.RetryableException;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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

    private ExecutorService executor = Executors.newFixedThreadPool(2000);


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

    public List<RewardTuple> calculateRewards(User user) throws InterruptedException, ExecutionException {

        List<RewardTuple> tuple = new ArrayList<>();

        List<VisitedLocationBean> visitedLocationList = user.getVisitedLocations();
        for (VisitedLocationBean visitedLocation : visitedLocationList) {
            for (AttractionBean attraction : attractions) {
                if (nearAttraction(visitedLocation, attraction)) {
                    tuple.add(new RewardTuple(visitedLocation, attraction));
                }
            }
        }

        return tuple;

    }


    public void calculateAllRewards(List<User> users) throws ExecutionException, InterruptedException {


        List<CompletableFuture> rewardFutures = new ArrayList<>();

        ConcurrentHashMap<User, List<RewardTuple>> map = new ConcurrentHashMap<>();

        users.forEach(u -> {
            List<RewardTuple> rewardTuples = null;
            try {
                rewardTuples = calculateRewards(u);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            map.put(u, rewardTuples);
        });

        map.forEach((k, v) -> {
            rewardFutures.add(
                    CompletableFuture.runAsync(() -> {
                        v.stream().forEach(t -> k.addUserReward(new UserReward(t.getVisitedLocationBean(), t.getAttractionBean(), getRewardPoints(t.getAttractionBean(), k))));
                    }, executor));
        });


        AtomicInteger a = new AtomicInteger();
        rewardFutures.forEach(t -> t.thenRun(() -> {
            a.getAndIncrement();
            System.out.println("percent finished : " + a.get() * 100 / rewardFutures.size() + "%");
        }));

        CompletableFuture<Void> allFutures = CompletableFuture
                .allOf(rewardFutures.toArray(new CompletableFuture[rewardFutures.size()]));

        allFutures.get();

    }


    public boolean isWithinAttractionProximity(AttractionBean attraction, LocationBean location) {
        return getDistance(attraction, location) > attractionProximityRange ? false : true;
    }

    private boolean nearAttraction(VisitedLocationBean visitedLocation, AttractionBean attraction) {
        return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
    }


    public int getRewardPoints(AttractionBean attraction, User user) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int attractionRewardPoints = 0;
        try {
            attractionRewardPoints = rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
        } catch (RetryableException exception) {
            exception.retryAfter();
        }
        stopWatch.stop();

        System.out.println("getRewards: Time Elapsed: " + stopWatch.getTime() + " " + user.getUserName() + " " + attraction.attractionName + " " + Thread.currentThread());
        return attractionRewardPoints;
    }

    public Double getDistance(LocationBean loc1, LocationBean loc2) {
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
