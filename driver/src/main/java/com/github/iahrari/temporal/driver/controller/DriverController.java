package com.github.iahrari.temporal.driver.controller;

import com.github.iahrari.temporal.driver.model.Driver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.Set;

@RequestMapping("/api/v1/driver")
@RequiredArgsConstructor
@RestController
@Slf4j
public class DriverController {
    private final Set<Driver> driversDb;

    @GetMapping("/{lat}/{lng}")
    public String getDriverIdByLatLng(@PathVariable String lat, @PathVariable String lng){
        log.info("Driver for lat: {} and lng: {} requested.", lat, lng);
        return driversDb.toArray(new Driver[0])[new Random().nextInt(driversDb.size())].getDriverId();
    }

    @GetMapping
    public Set<Driver> getAll(){
        return driversDb;
    }
}
