package com.thoughtworks.midquiz.midquiz.client.config;

import com.thoughtworks.midquiz.midquiz.client.decoder.FlightSiteErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class FlightSiteConfig {

    @Bean
    public ErrorDecoder decoder() {
        return new FlightSiteErrorDecoder();
    }
}
