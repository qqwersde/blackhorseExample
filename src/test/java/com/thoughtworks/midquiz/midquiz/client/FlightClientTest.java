package com.thoughtworks.midquiz.midquiz.client;

import com.thoughtworks.midquiz.midquiz.domain.feign.Flight;
import com.thoughtworks.midquiz.midquiz.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testng.annotations.Ignore;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Ignore
class FlightClientTest {
    @Autowired
    private FlightClient flightClient;

    @Test
    public void should_return_flight_list_when_has_supported_flights_given_conditions() {
        List<Flight> flights = flightClient.searchFlight("2022-03-10", "成都", "深圳");
        assertEquals(flights.size(), 2);
        assertEquals(flights.get(0).getFlightId(), "CS2311");
        assertEquals(flights.get(1).getFlightId(), "CS2463");
    }

    @Test
    public void should_return_non_flight_list_when_has_no_supported_flights_given_conditions() {
        List<Flight> flights = flightClient.searchFlight("2022-03-11", "成都", "深圳");
        assertEquals(flights.size(), 0);
    }

    @Test
    public void should_throw_exception_when_get_flights_failed_return_not_200_http_status() {
        Exception exception = assertThrows(
                BusinessException.class,
                () -> flightClient.searchFlight("2022-03-10", "成都", "伦敦"));

        assertEquals("好像出错了，请稍后再试", exception.getMessage());
    }

}