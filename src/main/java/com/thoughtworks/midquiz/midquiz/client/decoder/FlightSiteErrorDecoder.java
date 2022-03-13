package com.thoughtworks.midquiz.midquiz.client.decoder;

import com.thoughtworks.midquiz.midquiz.exception.BusinessException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class FlightSiteErrorDecoder implements ErrorDecoder {


    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() != 200) {
            String reason = response.reason();
            return new BusinessException("好像出错了，请稍后再试", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return decode(methodKey, response);
    }


}
