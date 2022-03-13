package com.thoughtworks.midquiz.midquiz.Serveice;

import com.thoughtworks.midquiz.midquiz.client.FlightClient;
import com.thoughtworks.midquiz.midquiz.client.FlightSiteClient;
import com.thoughtworks.midquiz.midquiz.domain.feign.Flight;
import com.thoughtworks.midquiz.midquiz.domain.feign.FlightSite;
import com.thoughtworks.midquiz.midquiz.exception.BusinessException;
import com.thoughtworks.midquiz.midquiz.response.FlightInfoResponse;
import com.thoughtworks.midquiz.midquiz.response.FlightsResponse;
import com.thoughtworks.midquiz.midquiz.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightClient flightClient;

    private final FlightSiteClient flightSiteClient;

    private final RedisUtils redisUtils;

    private final String successMessage = "查询成功";

    private final String noFlightMessage = "对不起，当前航段无航班";

    public FlightsResponse getFlights(String takeOffDate, String startLocation, String destination) {
        List<Flight> flights = flightClient.searchFlight(takeOffDate, startLocation, destination);
        return FlightsResponse.builder().amount(flights.size()).flights(flights)
                .message(CollectionUtils.isEmpty(flights) ? noFlightMessage : successMessage).build();
    }

    public FlightInfoResponse getFlightInfo(String fId) {
        List<FlightSite> flightSite;
        try {
            flightSite = flightSiteClient.getFlightSite(fId);
            redisUtils.saveList(fId, flightSite);
        } catch (Exception exception) {
            if (redisUtils.hasKey(fId)) {
                flightSite = redisUtils.getList(fId);
            } else {
                throw new BusinessException("查询失败，请稍后再试", HttpStatus.NOT_FOUND);
            }
        }
        return FlightInfoResponse.builder().flightSites(flightSite).message(successMessage).build();
    }


}
