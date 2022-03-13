package com.thoughtworks.midquiz.midquiz.client;

import com.thoughtworks.midquiz.midquiz.client.config.FlightSiteConfig;
import com.thoughtworks.midquiz.midquiz.domain.feign.FlightSite;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "flightSite-client", url = "${application.flight-site.url}", configuration = FlightSiteConfig.class)
public interface FlightSiteClient {

    @GetMapping("/flights/{fId}")
    List<FlightSite> getFlightSite(@RequestParam("fId") String fId);

    @PostMapping("/flights/{fId}/bookSite")
    boolean bookSite(@PathVariable("fId") String fId, @RequestParam String siteType);
}
