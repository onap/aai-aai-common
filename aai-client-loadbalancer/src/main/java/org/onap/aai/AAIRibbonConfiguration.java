/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.LoadBalancerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * AAIRibbonConfiguration is responsible for configuring the dmaap
 * and it reads the users the application properties class
 * and is mostly configurable via properties
 */
public class AAIRibbonConfiguration {

    @Autowired
    IClientConfig ribbonClientConfig;

    @Bean
    public ILoadBalancer ribbonLoadBalancer() {
        return LoadBalancerBuilder.newBuilder()
                .withClientConfig(ribbonClientConfig)
                .buildLoadBalancerFromConfigWithReflection();
    }

    @LoadBalanced
    @Bean
    public RestTemplate loadBalancedRestTemplate(){
        return new RestTemplate();
    }
}
