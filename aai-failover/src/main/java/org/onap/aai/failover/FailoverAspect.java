/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
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
 */
package org.onap.aai.failover;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@Aspect
@Component
public class FailoverAspect {

    private final Logger LOGGER = LoggerFactory.getLogger(FailoverAspect.class);

    private final FailoverMonitor failoverMonitor;
    private final AtomicLong atomicLong;

    public FailoverAspect(FailoverMonitor failoverMonitor){
        this.failoverMonitor = failoverMonitor;
        this.atomicLong = new AtomicLong(1l);
    }

    /*
     * By default, check for the existence of the following file: /opt/app/failover/failover.properties
     *
     * If the file exists, open the file as properties
     * and find the following property: is_primary
     * Check if the following value is set to true
     * If it is set to true, then proceed with running the scheduled task
     * and store the current value into an thread safe variable
     *
     * If the file doesn't exist, then proceed with the execution of scheduled task
     * as if it is the primary site since there is nothing helping identify if its primary
     *
     * If the application is not in an kubernetes environment, in order to emulate the behavior
     * search for the file in the classpath of application
     * If the file can be found then it will behavior similar to above in kubernetes env
     *
     * Since some tasks such as ones in history is constantly getting data
     * with little time in between each runs of the task to get latest data
     * we don't want to log too much when the failover properties isn't being changed
     * So it will check the last time this got executed and see if its more than two minutes have passed
     * then if it did, then it will log current status
     */
    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void preSchedule(ProceedingJoinPoint pointcut) throws Throwable {

        Object target             = pointcut.getTarget();
        MethodSignature signature = (MethodSignature) pointcut.getSignature();
        String method             = signature.getMethod().getName();

        if(failoverMonitor.shouldRun()){
            atomicLong.set(1l);
            pointcut.proceed();
        } else {
            long currentTime = new Date().getTime();
            long lastMessageTime = atomicLong.get();

            if((currentTime - lastMessageTime) > 120000){
                atomicLong.compareAndSet(lastMessageTime, new Date().getTime());
                LOGGER.debug("Not proceeding the task {}#{} due to is_primary set to false in failover.properties",
                    target.getClass(),
                    method
                );
            }
        }
    }
}

