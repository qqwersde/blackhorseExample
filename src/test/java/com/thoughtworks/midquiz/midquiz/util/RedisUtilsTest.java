package com.thoughtworks.midquiz.midquiz.util;

import com.thoughtworks.midquiz.midquiz.config.TestRedisConfiguration;
import com.thoughtworks.midquiz.midquiz.domain.feign.FlightSite;
import com.thoughtworks.midquiz.midquiz.domain.feign.SiteType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = TestRedisConfiguration.class)
class RedisUtilsTest {

    @Autowired
    private RedisUtils redisUtils;

    @Spy
    private final RedisTemplate redisTemplate = new RedisTemplate();

    @Test
    public void should_save_list_and_get_list_when_be_called() {
        FlightSite flightSite = FlightSite.builder().amount(10).siteType(SiteType.ECONOMY).price(500.23).build();
        FlightSite flightSite1 = FlightSite.builder().amount(5).siteType(SiteType.BUSINESS).price(1000.23).build();
        FlightSite flightSite2 = FlightSite.builder().amount(0).siteType(SiteType.ECONOMY).price(1500.23).build();
        List<FlightSite> flightSites = Arrays.asList(flightSite, flightSite1, flightSite2);
        String key = "CS2311";
        redisUtils.saveList(key, flightSites);
        List list = redisUtils.getList(key);
        assertEquals(list.get(0), flightSite);
    }

    @Test
    public void should_check_if_redis_has_key() {
        FlightSite flightSite = FlightSite.builder().amount(10).siteType(SiteType.ECONOMY).price(500.23).build();
        FlightSite flightSite1 = FlightSite.builder().amount(5).siteType(SiteType.BUSINESS).price(1000.23).build();
        FlightSite flightSite2 = FlightSite.builder().amount(0).siteType(SiteType.ECONOMY).price(1500.23).build();
        List<FlightSite> flightSites = Arrays.asList(flightSite, flightSite1, flightSite2);
        String key = "CS2311";
        redisUtils.saveList(key, flightSites);
        boolean hasKey = redisUtils.hasKey(key);
        assertEquals(hasKey, true);
    }
}