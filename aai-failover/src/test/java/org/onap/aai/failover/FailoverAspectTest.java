/*
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright (C) 2026
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Test;

public class FailoverAspectTest {

    @Test
    public void proceedsWithScheduledTaskWhenMonitorAllowsExecution() throws Throwable {
        FailoverMonitor monitor = mock(FailoverMonitor.class);
        ProceedingJoinPoint joinPoint = joinPoint();
        when(monitor.shouldRun()).thenReturn(true);

        new FailoverAspect(monitor).preSchedule(joinPoint);

        verify(joinPoint).proceed();
    }

    @Test
    public void suppressesScheduledTaskWhenMonitorDisallowsExecution() throws Throwable {
        FailoverMonitor monitor = mock(FailoverMonitor.class);
        ProceedingJoinPoint joinPoint = joinPoint();
        when(monitor.shouldRun()).thenReturn(false);

        new FailoverAspect(monitor).preSchedule(joinPoint);

        verify(joinPoint, never()).proceed();
    }

    private ProceedingJoinPoint joinPoint() throws Exception {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getTarget()).thenReturn(this);
        when(signature.getMethod()).thenReturn(getClass().getDeclaredMethod("scheduledTask"));
        return joinPoint;
    }

    private void scheduledTask() {
    }
}