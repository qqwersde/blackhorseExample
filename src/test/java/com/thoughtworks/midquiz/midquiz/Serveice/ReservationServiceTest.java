package com.thoughtworks.midquiz.midquiz.Serveice;

import com.thoughtworks.midquiz.midquiz.client.FlightSiteClient;
import com.thoughtworks.midquiz.midquiz.entity.Reservation;
import com.thoughtworks.midquiz.midquiz.exception.BusinessException;
import com.thoughtworks.midquiz.midquiz.repo.ReservationRepository;
import com.thoughtworks.midquiz.midquiz.request.ReservationRequest;
import com.thoughtworks.midquiz.midquiz.response.ReservationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
class ReservationServiceTest {
    @Mock
    private FlightSiteClient flightSiteClient;
    @Mock
    private ReservationRepository reservationRepository;
    @InjectMocks
    private ReservationService reservationService;

    @Test
    public void should_reserve_successfully_when_has_site_given_conditions() {

        String flightId = "CS2311";
        String economy = "ECONOMY";
        String userId = "27953";
        ReservationRequest reservationRequest = ReservationRequest.builder().siteType(economy).fId(flightId).price(500.23)
                .passengerId("36240119960522203X").passengerPhone("15179140628").passengerName("yin").build();
        Reservation reservation = Reservation.builder().fcId("fc001").creator(userId).flightId("CS2311").message("预定失败")
                .payStatus("代支付").price(500.23).id(1L).build();
        when(flightSiteClient.bookSite(flightId, economy)).thenReturn(true);
        when(reservationRepository.save(any())).thenReturn(reservation);

        ReservationResponse reservationResponse = reservationService.reserveFlight(reservationRequest, flightId, userId);

        assertEquals(reservationResponse.getMessage(), "预定成功");
        assertNull(reservationResponse.getOrderNum());
        assertEquals(reservationResponse.getRId(), 1L);
        assertEquals(reservationResponse.getPayStatus(), "代支付");
        verify(reservationRepository, times(1)).save(any());

    }

    @Test
    public void should_reserve_failed_when_has_no_site_given_conditions() {

        String flightId = "CS2311";
        String economy = "FIRST";
        String userId = "27953";
        ReservationRequest reservationRequest = ReservationRequest.builder().siteType(economy).fId(flightId).price(500.23)
                .passengerId("36240119960522203X").passengerPhone("15179140628").passengerName("yin").build();
        Reservation reservation = Reservation.builder().fcId("fc001").creator(userId).flightId("CS2311").message("预定失败")
                .payStatus("代支付").price(500.23).id(1L).build();
        when(flightSiteClient.bookSite(flightId, economy)).thenReturn(false);
        when(reservationRepository.save(any())).thenReturn(reservation);

        ReservationResponse reservationResponse = reservationService.reserveFlight(reservationRequest, flightId, userId);

        assertEquals(reservationResponse.getMessage(), "预定失败，当前航班已没有仓位，请选择其他航班");
        assertNull(reservationResponse.getOrderNum());
        assertEquals(reservationResponse.getRId(), 1L);
        assertEquals(reservationResponse.getPayStatus(), "不用支付");
        verify(reservationRepository, times(1)).save(any());
    }

    @Test
    public void should_throw_exception_when_flight_site_failed_given_conditions() {

        String flightId = "CS2311";
        String economy = "ECONOMY";
        String userId = "27953";
        ReservationRequest reservationRequest = ReservationRequest.builder().siteType(economy).fId(flightId).price(500.23)
                .passengerId("36240119960522203X").passengerPhone("15179140628").passengerName("yin").build();

        when(flightSiteClient.bookSite(flightId, economy)).thenThrow(new BusinessException("好像出错了，请稍后再试", HttpStatus.INTERNAL_SERVER_ERROR));

        Exception exception = assertThrows(
                BusinessException.class,
                () -> reservationService.reserveFlight(reservationRequest, flightId, userId));

        assertEquals("预定服务出错，请重试", exception.getMessage());

        verify(reservationRepository, never()).save(any());
    }

    @Test
    public void should_throw_exception_when_repo_failed_given_conditions() {

        String flightId = "CS2311";
        String economy = "FIRST";
        String userId = "27953";
        ReservationRequest reservationRequest = ReservationRequest.builder().siteType(economy).fId(flightId).price(500.23)
                .passengerId("36240119960522203X").passengerPhone("15179140628").passengerName("yin").build();
        Reservation reservation = Reservation.builder().fcId("fc001").creator(userId).flightId("CS2311").message("预定失败")
                .payStatus("代支付").price(500.23).id(1L).build();
        when(flightSiteClient.bookSite(flightId, economy)).thenReturn(true);
        when(reservationRepository.save(any())).thenThrow(new BusinessException("好像出错了，请稍后再试", HttpStatus.INTERNAL_SERVER_ERROR));
        Exception exception = assertThrows(
                BusinessException.class,
                () -> reservationService.reserveFlight(reservationRequest, flightId, userId));

        assertEquals("预定服务出错，请重试", exception.getMessage());

        verify(reservationRepository, times(1)).save(any());
    }

}