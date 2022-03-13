package com.thoughtworks.midquiz.midquiz.client.config;

import com.thoughtworks.midquiz.midquiz.client.decoder.FlightErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class FlightConfig {

    @Bean
    public ErrorDecoder decoder() {
        return new FlightErrorDecoder();
    }
}
