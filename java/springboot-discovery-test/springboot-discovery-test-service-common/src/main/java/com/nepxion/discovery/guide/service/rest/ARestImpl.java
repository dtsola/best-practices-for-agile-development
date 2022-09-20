/*
 * Copyright 2022 Dtsola.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nepxion.discovery.guide.service.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.nepxion.discovery.common.constant.DiscoveryConstant;
import com.nepxion.discovery.guide.service.core.CoreImpl;

import java.util.Map;

@RestController
@ConditionalOnProperty(name = DiscoveryConstant.SPRING_APPLICATION_NAME, havingValue = "springboot-discovery-test-service-a")
public class ARestImpl extends CoreImpl {
    private static final Logger LOG = LoggerFactory.getLogger(ARestImpl.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Environment env;

    @GetMapping(path = "/rest/{value}")
    public String rest(@PathVariable(value = "value") String value) {
        value = getPluginInfo(value);
        value = restTemplate.getForEntity("http://springboot-discovery-test-service-b/rest/" + value, String.class).getBody();

        LOG.info("调用路径：{}", value);
        LOG.info("元数据：group:{}--version:{}--region:{}--env:{}--zone:{}\n",
                env.getProperty("spring.cloud.discovery.metadata.group"),
                env.getProperty("spring.cloud.discovery.metadata.version"),
                env.getProperty("spring.cloud.discovery.metadata.region"),
                env.getProperty("spring.cloud.discovery.metadata.env"),
                env.getProperty("spring.cloud.discovery.metadata.zone")
        );

        return value;
    }
}