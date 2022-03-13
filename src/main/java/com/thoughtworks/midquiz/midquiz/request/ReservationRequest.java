package com.thoughtworks.midquiz.midquiz.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ReservationRequest {

    private String passengerName;
    private String passengerId;
    private String fId;
    @Size(min = 11, max = 11, message = "信息填写有误请修改")
    private String passengerPhone;
    private String siteType;
    private double price;
}
