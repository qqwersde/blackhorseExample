package com.thoughtworks.midquiz.midquiz.controller;

import com.alibaba.fastjson.JSON;
import com.thoughtworks.midquiz.midquiz.Serveice.FlightService;
import com.thoughtworks.midquiz.midquiz.domain.feign.Flight;
import com.thoughtworks.midquiz.midquiz.domain.feign.FlightSite;
import com.thoughtworks.midquiz.midquiz.domain.feign.SiteType;
import com.thoughtworks.midquiz.midquiz.exception.BusinessException;
import com.thoughtworks.midquiz.midquiz.exception.ErrorResult;
import com.thoughtworks.midquiz.midquiz.response.FlightInfoResponse;
import com.thoughtworks.midquiz.midquiz.response.FlightsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FlightService flightService;

    @Test
    public void should_return_flight_list_when_search_flight_give_time_start_location_and_destination() throws Exception {

        String time = "2022-03-10";
        String startLocation = "成都";
        String destination = "深圳";
        LocalDateTime takeoffTime = LocalDateTime.of(2022, 3, 10, 9, 30);
        LocalDateTime takeoffTime2 = LocalDateTime.of(2022, 3, 10, 10, 30);
        Flight cs2311 = Flight.builder().flightId("CS2311").time(takeoffTime).startLocation(startLocation).destination(destination).build();
        Flight cs2463 = Flight.builder().flightId("CS2463").time(takeoffTime2).startLocation(startLocation).destination(destination).build();
        FlightsResponse flightsResponse = FlightsResponse.builder().flights(Arrays.asList(cs2311, cs2463)).message("查询成功").amount(2).build();

        when(flightService.getFlights(time, startLocation, destination)).thenReturn(flightsResponse);

        MvcResult result = mockMvc.perform(
                get("/flights")
                        .param("takeOffDate", time)
                        .param("startLocation", startLocation)
                        .param("destination", destination)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        FlightsResponse response = JSON.parseObject(content, FlightsResponse.class);
        assertEquals(response.getAmount(), 2);
    }

    @Test
    public void should_return_no_flight_list_when_search_flight_give_time_start_location_and_destination() throws Exception {

        String time = "2022-03-11";
        String startLocation = "成都";
        String destination = "深圳";
        FlightsResponse flightsResponse = FlightsResponse.builder().flights(Collections.emptyList()).message("对不起，当前航段无航班").amount(0).build();

        when(flightService.getFlights(time, startLocation, destination)).thenReturn(flightsResponse);

        MvcResult result = mockMvc.perform(
                get("/flights")
                        .param("takeOffDate", time)
                        .param("startLocation", startLocation)
                        .param("destination", destination)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        FlightsResponse response = JSON.parseObject(content, FlightsResponse.class);
        assertEquals(response.getAmount(), 0);
    }

    @Test
    public void should_throw_exception_when_search_flight_failed_give_time_start_location_and_destination() throws Exception {

        String time = "2022-03-10";
        String startLocation = "成都";
        String destination = "伦敦";

        when(flightService.getFlights(time, startLocation, destination)).thenThrow(new BusinessException("好像出错了，请稍后再试", HttpStatus.INTERNAL_SERVER_ERROR));

        MvcResult result = mockMvc.perform(
                get("/flights")
                        .param("takeOffDate", time)
                        .param("startLocation", startLocation)
                        .param("destination", destination)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ErrorResult response = JSON.parseObject(content, ErrorResult.class);
        assertEquals(response.getErrorCode(), 500);
    }

    @Test
    public void should_return_flight_site_when_choose_one_flight_given_fid() throws Exception {
        String fId = "CS2311";
        FlightSite flightSite = FlightSite.builder().amount(10).siteType(SiteType.ECONOMY).price(500.23).build();
        FlightSite flightSite1 = FlightSite.builder().amount(5).siteType(SiteType.BUSINESS).price(1000.23).build();
        FlightSite flightSite2 = FlightSite.builder().amount(0).siteType(SiteType.ECONOMY).price(1500.23).build();
        List<FlightSite> flightSites = Arrays.asList(flightSite, flightSite1, flightSite2);
        FlightInfoResponse flightInfoResponse = FlightInfoResponse.builder().flightSites(flightSites).message("查询成功").build();

        when(flightService.getFlightInfo(fId)).thenReturn(flightInfoResponse);

        MvcResult result = mockMvc.perform(
                get("/flights/CS2311")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        FlightInfoResponse response = JSON.parseObject(content, FlightInfoResponse.class);
        assertEquals(response.getFlightSites().size(), 3);
    }

    @Test
    public void should_throw_exception_when_flight_site_client_throw_exception_redis_has_no_value_given_fid() throws Exception {
        String fId = "CS2311";


        when(flightService.getFlightInfo(fId)).thenThrow(new BusinessException("查询失败，请稍后再试", HttpStatus.NOT_FOUND));

        MvcResult result = mockMvc.perform(
                get("/flights/CS2311")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ErrorResult response = JSON.parseObject(content, ErrorResult.class);
        assertEquals(response.getErrorCode(), 404);
    }

}