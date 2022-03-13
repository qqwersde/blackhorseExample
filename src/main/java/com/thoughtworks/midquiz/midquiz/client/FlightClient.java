package com.thoughtworks.midquiz.midquiz.client;

import com.thoughtworks.midquiz.midquiz.client.config.FlightConfig;
import com.thoughtworks.midquiz.midquiz.domain.feign.Flight;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "flight-client", url = "${application.flight.url}", configuration = FlightConfig.class)
public interface FlightClient {

    @GetMapping("/flights")
    List<Flight> searchFlight(@RequestParam("takeOffDate") String takeOffDate, @RequestParam("startLocation") String startLocation,
                              @RequestParam("destination") String destination);
}
