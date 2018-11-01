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
package org.onap.aai.exceptions;

import org.junit.Test;
import org.onap.aai.AAISetup;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class AAIExceptionWithInfoTest extends AAISetup {

    
    private static final HashMap<String, Object> map = new HashMap<String, Object>();

    {
        map.put("itemInteger", 1);
        map.put("itemString", "Two");
        map.put("itemThree", Boolean.TRUE);
    }

    private static final String info = "An error has occurred.";
    private static final String code = "AAI_4004";
    private static final String details = "This is a detailed description of the exception.";
    private static final Throwable cause = new RuntimeException("This is a runtime exception.");

    /**
     * Test constructor with 2 params.
     *
     * @throws Exception the exception
     */
    @Test
    public void testConstructorWith2Params() throws Exception {
        AAIExceptionWithInfo exception = new AAIExceptionWithInfo(map, info);

        assertEquals(map, exception.getInfoHash());
        assertEquals(info, exception.getInfo());
    }

    /**
     * Test constructor with 3 params.
     *
     * @throws Exception the exception
     */
    @Test
    public void testConstructorWith3Params() throws Exception {
        AAIExceptionWithInfo exception = new AAIExceptionWithInfo(code, map, info);

        assertEquals("4004", exception.getErrorObject().getErrorCode());
        assertEquals(map, exception.getInfoHash());
        assertEquals(info, exception.getInfo());
    }

    /**
     * Test constructor with 4 params I.
     *
     * @throws Exception the exception
     */
    @Test
    public void testConstructorWith4ParamsI() throws Exception {
        AAIExceptionWithInfo exception = new AAIExceptionWithInfo(code, details, map, info);

        assertEquals("4004", exception.getErrorObject().getErrorCode());
        assertEquals(details, exception.getMessage());
        assertEquals(map, exception.getInfoHash());
        assertEquals(info, exception.getInfo());
    }

    /**
     * Test constructor with 4 params II.
     *
     * @throws Exception the exception
     */
    @Test
    public void testConstructorWith4ParamsII() throws Exception {
        AAIExceptionWithInfo exception = new AAIExceptionWithInfo(code, cause, map, info);

        assertEquals("4004", exception.getErrorObject().getErrorCode());
        assertEquals(cause.toString(), exception.getMessage());
        assertEquals(map, exception.getInfoHash());
        assertEquals(info, exception.getInfo());
    }

    /**
     * Test constructor with 5 params.
     *
     * @throws Exception the exception
     */
    @Test
    public void testConstructorWith5Params() throws Exception {
        AAIExceptionWithInfo exception = new AAIExceptionWithInfo(code, cause, details, map, info);

        assertEquals("4004", exception.getErrorObject().getErrorCode());
        assertEquals(details, exception.getMessage());
        assertEquals(map, exception.getInfoHash());
        assertEquals(info, exception.getInfo());
    }

    /**
     * Test set info hash.
     */
    @Test
    public void testSetInfoHash() {
        AAIExceptionWithInfo exception = new AAIExceptionWithInfo(map, info);

        HashMap<String, Object> newMap = new HashMap<String, Object>();
        newMap.put("itemInteger", 2);
        exception.setInfoHash(newMap);
        
        assertEquals(newMap, exception.getInfoHash());
        assertEquals(info, exception.getInfo());
    }
    
    /**
     * Test set info.
     */
    @Test
    public void testSetInfo() {
        AAIExceptionWithInfo exception = new AAIExceptionWithInfo(map, info);

        String newInfo = "This is updated info.";
        exception.setInfo(newInfo);
        
        assertEquals(map, exception.getInfoHash());
        assertEquals(newInfo, exception.getInfo());
    }

}
