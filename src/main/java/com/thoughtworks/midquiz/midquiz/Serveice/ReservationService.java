package com.thoughtworks.midquiz.midquiz.Serveice;

import com.thoughtworks.midquiz.midquiz.client.FlightSiteClient;
import com.thoughtworks.midquiz.midquiz.entity.Reservation;
import com.thoughtworks.midquiz.midquiz.exception.BusinessException;
import com.thoughtworks.midquiz.midquiz.repo.ReservationRepository;
import com.thoughtworks.midquiz.midquiz.request.ReservationRequest;
import com.thoughtworks.midquiz.midquiz.response.ReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {


    private final FlightSiteClient flightSiteClient;

    private final ReservationRepository reservationRepository;


    private final String payStatusSuccess = "代支付";
    private final String payStatusNoNeed = "不用支付";

    private final String reservationSuccess = "预定成功";
    private final String reservationFailed = "预定失败，当前航班已没有仓位，请选择其他航班";
    private final String serverErrorMessage = "预定服务出错，请重试";

    public ReservationResponse reserveFlight(ReservationRequest request, String fcId, String userId) {
        try {
            boolean bookSite = flightSiteClient.bookSite(request.getFId(), request.getSiteType());
            if (bookSite) {
                return getReservationResponse(request, fcId, userId, payStatusSuccess, reservationSuccess);
            } else {
                return getReservationResponse(request, fcId, userId, payStatusNoNeed, reservationFailed);
            }
        } catch (Exception exception) {
            throw new BusinessException(serverErrorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private ReservationResponse getReservationResponse(ReservationRequest request, String fcId, String userId, String payStatusNoNeed, String reservationFailed) {
        Reservation reservation;
        reservation = Reservation.builder().flightId(request.getFId()).payStatus(payStatusNoNeed).message(reservationFailed)
                .creator(userId).price(request.getPrice()).fcId(fcId).build();
        Reservation save = reservationRepository.save(reservation);
        return ReservationResponse.builder().message(reservationFailed).orderNum(save.getOrderNum())
                .rId(save.getId()).payStatus(payStatusNoNeed).build();
    }


}
