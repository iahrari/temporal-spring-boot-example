package com.github.iahrari.temporal.order.activity;

import com.github.iahrari.temporal.api.activity.CalculateOrderPriceActivity;
import com.github.iahrari.temporal.api.annotations.TemporalActivity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@TemporalActivity
public class CalculateOrderPriceActivityImpl implements CalculateOrderPriceActivity {
    @Override
    public BigDecimal calculatePrice(Integer lat, Integer lng) {
        return BigDecimal.valueOf((lat + lng) * 1000L);
    }
}
