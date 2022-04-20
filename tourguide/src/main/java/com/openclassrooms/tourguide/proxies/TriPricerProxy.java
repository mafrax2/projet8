package com.openclassrooms.tourguide.proxies;

import com.openclassrooms.tourguide.model.beans.ProviderBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name="trippricer-reward", url = "${trippricerms.entry.port}")
public interface TriPricerProxy {

    @RequestMapping("/getTripPrice")
    public List<ProviderBean> getTripPrice(@RequestParam String apiKey, @RequestParam UUID attractionId, @RequestParam int adults, @RequestParam int children, @RequestParam int nightsStay, @RequestParam int rewardsPoints);

}
