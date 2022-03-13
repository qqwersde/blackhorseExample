package com.thoughtworks.midquiz.midquiz.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.thoughtworks.midquiz.midquiz.Serveice.ReservationService;
import com.thoughtworks.midquiz.midquiz.exception.BusinessException;
import com.thoughtworks.midquiz.midquiz.exception.ErrorResult;
import com.thoughtworks.midquiz.midquiz.request.ReservationRequest;
import com.thoughtworks.midquiz.midquiz.response.ReservationResponse;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReservationService reservationService;

    @Test
    public void should_reserve_successfully_when_has_site() throws Exception {
        String flightId = "CS2311";
        String economy = "ECONOMY";
        String userId = "27953";
        ReservationRequest reservationRequest = ReservationRequest.builder().siteType(economy).fId(flightId).price(500.23)
                .passengerId("36240119960522203X").passengerPhone("15179140628").passengerName("yin").build();

        ReservationResponse reservationResponse = ReservationResponse.builder().message("预定成功").payStatus("代支付")
                .orderNum(null).rId(1L).build();
        when(reservationService.reserveFlight(any(ReservationRequest.class), eq("fc001"), eq(userId))).thenReturn(reservationResponse);

        String requestJson = JSONObject.toJSONString(reservationRequest);

        MvcResult result = mockMvc.perform(
                post("/flight_contracts/fc001/reservations")
                        .content(requestJson)
                        .header("userId", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ReservationResponse response = JSON.parseObject(content, ReservationResponse.class);
        assertEquals(response.getRId(), 1L);
    }

    @Test
    public void should_reserve_successfully_when_has_no_site() throws Exception {
        String flightId = "CS2311";
        String economy = "FIRST";
        String userId = "27953";
        ReservationRequest reservationRequest = ReservationRequest.builder().siteType(economy).fId(flightId).price(500.23)
                .passengerId("36240119960522203X").passengerPhone("15179140628").passengerName("yin").build();

        ReservationResponse reservationResponse = ReservationResponse.builder().message("预定失败，当前航班已没有仓位，请选择其他航班").payStatus("不用支付")
                .orderNum(null).rId(1L).build();
        when(reservationService.reserveFlight(any(ReservationRequest.class), eq("fc001"), eq(userId))).thenReturn(reservationResponse);

        String requestJson = JSONObject.toJSONString(reservationRequest);

        MvcResult result = mockMvc.perform(
                post("/flight_contracts/fc001/reservations")
                        .content(requestJson)
                        .header("userId", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ReservationResponse response = JSON.parseObject(content, ReservationResponse.class);
        assertEquals(response.getRId(), 1L);
    }

    @Test
    public void should_throw_exception_when_request_has_wrong_param() throws Exception {
        String flightId = "CS2311";
        String economy = "ECONOMY";
        String userId = "27953";
        ReservationRequest reservationRequest = ReservationRequest.builder().siteType(economy).fId(flightId).price(500.23)
                .passengerId("36240119960522203X").passengerPhone("151791406281").passengerName("yin").build();


        String requestJson = JSONObject.toJSONString(reservationRequest);
        MvcResult result = mockMvc.perform(
                post("/flight_contracts/fc001/reservations")
                        .header("userId", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().is4xxClientError())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ErrorResult response = JSON.parseObject(content, ErrorResult.class);
        assertEquals(response.getErrorCode(), 400);
    }

    @Test
    public void should_throw_exception_when_sever_is_not_avaliable() throws Exception {
        String flightId = "CS2311";
        String economy = "ECONOMY";
        String userId = "27953";
        ReservationRequest reservationRequest = ReservationRequest.builder().siteType(economy).fId(flightId).price(500.23)
                .passengerId("36240119960522203X").passengerPhone("15179140628").passengerName("yin").build();

        when(reservationService.reserveFlight(any(ReservationRequest.class), eq("fc001"), eq(userId)))
                .thenThrow(new BusinessException("预定服务出错，请重试", HttpStatus.INTERNAL_SERVER_ERROR));

        String requestJson = JSONObject.toJSONString(reservationRequest);
        MvcResult result = mockMvc.perform(
                post("/flight_contracts/fc001/reservations")
                        .header("userId", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().is5xxServerError())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ErrorResult response = JSON.parseObject(content, ErrorResult.class);
        assertEquals(response.getErrorCode(), 500);
    }
}