package com.thoughtworks.midquiz.midquiz.Serveice;

import com.thoughtworks.midquiz.midquiz.client.FlightClient;
import com.thoughtworks.midquiz.midquiz.client.FlightSiteClient;
import com.thoughtworks.midquiz.midquiz.domain.feign.Flight;
import com.thoughtworks.midquiz.midquiz.domain.feign.FlightSite;
import com.thoughtworks.midquiz.midquiz.domain.feign.SiteType;
import com.thoughtworks.midquiz.midquiz.exception.BusinessException;
import com.thoughtworks.midquiz.midquiz.response.FlightInfoResponse;
import com.thoughtworks.midquiz.midquiz.response.FlightsResponse;
import com.thoughtworks.midquiz.midquiz.util.RedisUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class FlightServiceTest {

    @Mock
    private FlightClient flightClient;

    @Mock
    private FlightSiteClient flightSiteClient;

    @Mock
    private RedisUtils<Object> redisUtils;

    @InjectMocks
    private FlightService flightService;

    @Test
    public void should_return_flight_list_when_search_flight_give_time_start_location_and_destination() {
        String time = "2022-03-10";
        String startLocation = "成都";
        String destination = "深圳";
        LocalDateTime takeoffTime = LocalDateTime.of(2022, 3, 10, 9, 30);
        LocalDateTime takeoffTime2 = LocalDateTime.of(2022, 3, 10, 10, 30);

        Flight cs2311 = Flight.builder().flightId("CS2311").time(takeoffTime).startLocation(startLocation).destination(destination).build();
        Flight cs2463 = Flight.builder().flightId("CS2463").time(takeoffTime2).startLocation(startLocation).destination(destination).build();

        when(flightClient.searchFlight(time, startLocation, destination)).thenReturn(Arrays.asList(cs2311, cs2463));
        FlightsResponse flights = flightService.getFlights(time, startLocation, destination);
        assertEquals(flights.getMessage(), "查询成功");
        assertEquals(flights.getAmount(), 2);
    }

    @Test
    public void should_return_no_flight_list_when_search_flight_give_time_start_location_and_destination() {
        String time = "2022-03-11";
        String startLocation = "成都";
        String destination = "深圳";


        when(flightClient.searchFlight(time, startLocation, destination)).thenReturn(Collections.emptyList());
        FlightsResponse flights = flightService.getFlights(time, startLocation, destination);
        assertEquals(flights.getMessage(), "对不起，当前航段无航班");
        assertEquals(flights.getAmount(), 0);
    }

    @Test
    public void should_throw_exception_when_flight_client_return_response_status_is_not_200() {
        String time = "2022-03-10";
        String startLocation = "成都";
        String destination = "伦敦";


        when(flightClient.searchFlight(time, startLocation, destination)).thenThrow(new BusinessException("好像出错了，请稍后再试", HttpStatus.INTERNAL_SERVER_ERROR));
        Exception exception = assertThrows(
                BusinessException.class,
                () -> flightClient.searchFlight(time, startLocation, destination));

        assertEquals("好像出错了，请稍后再试", exception.getMessage());
    }

    @Test
    public void should_return_flight_site_when_choose_one_flight_given_fid() {
        String fId = "CS2311";
        FlightSite flightSite = FlightSite.builder().amount(10).siteType(SiteType.ECONOMY).price(500.23).build();
        FlightSite flightSite1 = FlightSite.builder().amount(5).siteType(SiteType.BUSINESS).price(1000.23).build();
        FlightSite flightSite2 = FlightSite.builder().amount(0).siteType(SiteType.ECONOMY).price(1500.23).build();
        List<FlightSite> flightSites = Arrays.asList(flightSite, flightSite1, flightSite2);

        when(flightSiteClient.getFlightSite(fId)).thenReturn(flightSites);
        FlightInfoResponse flightInfo = flightService.getFlightInfo(fId);

        assertEquals(flightInfo.getFlightSites().size(), 3);
        assertEquals(flightInfo.getFlightSites().get(0).getSiteType(), SiteType.ECONOMY);
        assertEquals(flightInfo.getFlightSites().get(0).getPrice(), 500.23);
        assertEquals(flightInfo.getMessage(), "查询成功");
        verify(redisUtils, times(1)).saveList(eq(fId), anyList());
    }

    @Test
    public void should_return_flight_site_when_flight_site_client_throw_exception_redis_has_value_given_fid() {
        String fId = "CS2311";
        FlightSite flightSite = FlightSite.builder().amount(10).siteType(SiteType.ECONOMY).price(500.23).build();
        FlightSite flightSite1 = FlightSite.builder().amount(5).siteType(SiteType.BUSINESS).price(1000.23).build();
        FlightSite flightSite2 = FlightSite.builder().amount(0).siteType(SiteType.ECONOMY).price(1500.23).build();

        when(flightSiteClient.getFlightSite(fId)).thenThrow(new BusinessException("好像出错了，请稍后再试", HttpStatus.INTERNAL_SERVER_ERROR));
        when(redisUtils.hasKey(fId)).thenReturn(true);
        when(redisUtils.getList(fId)).thenReturn(Arrays.asList(flightSite, flightSite1, flightSite2));
        FlightInfoResponse flightInfo = flightService.getFlightInfo(fId);

        assertEquals(flightInfo.getFlightSites().size(), 3);
        assertEquals(flightInfo.getFlightSites().get(0).getSiteType(), SiteType.ECONOMY);
        assertEquals(flightInfo.getFlightSites().get(0).getPrice(), 500.23);
        assertEquals(flightInfo.getMessage(), "查询成功");
        verify(redisUtils, never()).saveList(eq(fId), anyList());
        verify(redisUtils, times(1)).hasKey(fId);
        verify(redisUtils, times(1)).getList(fId);
    }

    @Test
    public void should_throw_exception_when_flight_site_client_throw_exception_redis_has_no_value_given_fid() {
        String fId = "CS2311";

        when(flightSiteClient.getFlightSite(fId)).thenThrow(new BusinessException("好像出错了，请稍后再试", HttpStatus.INTERNAL_SERVER_ERROR));
        when(redisUtils.hasKey(fId)).thenReturn(false);
        Exception exception = assertThrows(
                BusinessException.class, () ->
                        flightService.getFlightInfo(fId));

        assertEquals("查询失败，请稍后再试", exception.getMessage());
        verify(redisUtils, never()).saveList(eq(fId), anyList());
        verify(redisUtils, times(1)).hasKey(fId);
        verify(redisUtils, never()).getList(fId);
    }

}