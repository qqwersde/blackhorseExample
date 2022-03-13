package com.thoughtworks.midquiz.midquiz.controller;


import com.thoughtworks.midquiz.midquiz.Serveice.ReservationService;
import com.thoughtworks.midquiz.midquiz.request.ReservationRequest;
import com.thoughtworks.midquiz.midquiz.response.ReservationResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/flight_contracts/{fcId}/reservations")
class ReservationController {


    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("")
    public ReservationResponse reserveFlight(@Valid @RequestBody ReservationRequest request, @PathVariable("fcId") String fcId
            , @RequestHeader("userId") String userId) {
        return reservationService.reserveFlight(request, fcId, userId);
    }

}
