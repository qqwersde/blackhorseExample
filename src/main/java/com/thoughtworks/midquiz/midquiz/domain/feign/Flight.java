package com.thoughtworks.midquiz.midquiz.domain.feign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Flight {
    private LocalDateTime time;
    private String startLocation;
    private String destination;
    private String flightId;
}
