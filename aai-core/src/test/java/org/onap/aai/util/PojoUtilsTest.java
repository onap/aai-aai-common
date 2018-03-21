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
package org.onap.aai.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.Test;
import org.mockito.Mockito;

public class PojoUtilsTest {

	@Test
	public void testGetJsonFromObject_Clazz_MockTest() throws Exception {

		String obj = "helloWorld";
		PojoUtils pojoUtils = Mockito.mock(PojoUtils.class);
		Mockito.doCallRealMethod().when(pojoUtils).getJsonFromObject(Mockito.anyString());

		pojoUtils.getJsonFromObject(obj);

		Mockito.verify(pojoUtils, Mockito.times(1)).getJsonFromObject(Mockito.anyString(), Mockito.eq(false), Mockito.eq(true));
	}

	@Test
	public void testGetJsonFromObject_Clazz() throws Exception {
		
		PojoUtils pojoUtils = PojoUtilsTest.getInstance();
		LocalDateTime date = LocalDateTime.of(2017, Month.SEPTEMBER, 18, 10, 55, 0, 300);

		String res = pojoUtils.getJsonFromObject(date);

		assertNotNull(res);
		assertTrue(res.contains("\"dayOfMonth\" : 18"));
		assertTrue(res.contains("\"dayOfWeek\" : \"MONDAY\""));
		assertTrue(res.contains("\"dayOfYear\" : 261"));
		assertTrue(res.contains("\"hour\" : 10"));
		assertTrue(res.contains("\"minute\" : 55"));
		assertTrue(res.contains("\"month\" : \"SEPTEMBER\""));
		assertTrue(res.contains("\"monthValue\" : 9"));
		assertTrue(res.contains("\"nano\" : 300"));
		assertTrue(res.contains("\"second\" : 0"));
		assertTrue(res.contains("\"year\" : 2017"));
	}
	
	@Test
	public void testGetJsonFromObject_Clazz_null() throws Exception {
		PojoUtils pojoUtils = PojoUtilsTest.getInstance();
		
		String res = pojoUtils.getJsonFromObject(null);

		assertNotNull(res);
		assertEquals("null", res);
	}
	
	@Test
	public void testGetJsonFromObject_null() throws Exception {
		PojoUtils pojoUtils = PojoUtilsTest.getInstance();
		
		String res = pojoUtils.getJsonFromObject(null, false, true);

		assertNotNull(res);
		assertEquals("null", res);
	}

	@Test
	public void testGetJsonFromObject() throws Exception {
		PojoUtils pojoUtils = PojoUtilsTest.getInstance();
		LocalDateTime date = LocalDateTime.of(2017, Month.SEPTEMBER, 18, 10, 55, 0, 300);
		
		String res = pojoUtils.getJsonFromObject(date, false, false);
		assertNotNull(res);
		
		res = pojoUtils.getJsonFromObject(date, true, false);
		assertNotNull(res);
		
		res = pojoUtils.getJsonFromObject(date, true, true);
		assertNotNull(res);
	}

	static PojoUtils getInstance() {
		return new PojoUtils();	
	}
}
