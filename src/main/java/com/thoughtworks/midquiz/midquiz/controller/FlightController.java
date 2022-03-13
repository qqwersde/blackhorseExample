package com.thoughtworks.midquiz.midquiz.controller;


import com.thoughtworks.midquiz.midquiz.Serveice.FlightService;
import com.thoughtworks.midquiz.midquiz.response.FlightInfoResponse;
import com.thoughtworks.midquiz.midquiz.response.FlightsResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flights")
public class FlightController {


    private final FlightService flightService;


    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("")
    public FlightsResponse getFlightList(@RequestParam String takeOffDate, @RequestParam String startLocation,
                                         @RequestParam String destination) {
        return flightService.getFlights(takeOffDate, startLocation, destination);
    }

    @GetMapping("/{fId}")
    public FlightInfoResponse getFlightSite(@PathVariable("fId") String fId) {
        return flightService.getFlightInfo(fId);
    }

}
