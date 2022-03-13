package com.thoughtworks.midquiz.midquiz.response;

import com.thoughtworks.midquiz.midquiz.domain.feign.Flight;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightsResponse {

    private List<Flight> flights;
    private String message;
    private int amount;
}
