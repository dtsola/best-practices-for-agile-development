package com.nepxion.discovery.guide.service.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "springboot-discovery-test-service-b")
public interface BFeign {
    @GetMapping(path = "/invoke/{value}")
    String invoke(@PathVariable(value = "value") String value);
}