package com.thoughtworks.midquiz.midquiz.client;

import com.thoughtworks.midquiz.midquiz.domain.feign.FlightSite;
import com.thoughtworks.midquiz.midquiz.domain.feign.SiteType;
import com.thoughtworks.midquiz.midquiz.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testng.annotations.Ignore;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Ignore
class FlightSiteClientTest {
    @Autowired
    private FlightSiteClient flightSiteClient;

    @Test
    public void should_return_flight_sites_when_search_flight_site_info_given_correct_fId() {
        List<FlightSite> flightSite = flightSiteClient.getFlightSite("CS2311");
        assertEquals(flightSite.size(), 3);
        assertEquals(flightSite.get(0).getPrice(), 500.23);
        assertEquals(flightSite.get(0).getSiteType(), SiteType.ECONOMY);
    }

    @Test
    public void should_throw_exception_when_search_flight_site_info_given_wrong_fId() {

        Exception exception = assertThrows(
                BusinessException.class,
                () -> flightSiteClient.getFlightSite("CS2345"));

        assertEquals("好像出错了，请稍后再试", exception.getMessage());
    }

    @Test
    public void should_book_site_successfully_when_has_sites_given_fId_and_site_type() {
        boolean bookSite = flightSiteClient.bookSite("CS2311", "ECONOMY");
        assertTrue(bookSite);
    }

    @Test
    public void should_book_site_failed_when_has_no_sites_given_fId_and_site_type() {
        boolean bookSite = flightSiteClient.bookSite("CS2311", "FIRST");
        assertFalse(bookSite);
    }

    @Test
    public void should_throw_exception_when_book_site_exception() {

        Exception exception = assertThrows(
                BusinessException.class,
                () -> flightSiteClient.bookSite("CS2345", "FIRST"));

        assertEquals("好像出错了，请稍后再试", exception.getMessage());
    }


}