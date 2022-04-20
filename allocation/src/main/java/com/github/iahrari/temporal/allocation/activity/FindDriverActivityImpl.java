package com.github.iahrari.temporal.allocation.activity;

import com.github.iahrari.temporal.api.activity.FindDriverActivity;
import com.github.iahrari.temporal.api.annotations.TemporalActivity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@TemporalActivity
@RequiredArgsConstructor
public class FindDriverActivityImpl implements FindDriverActivity {
    private final RestTemplate restTemplate;

    @Override
    public String findDriver(int lat, int lng) {
        return restTemplate.getForObject("http://localhost:8090/api/v1/driver/{lat}/{lng}", String.class, lat, lng);
    }
}
