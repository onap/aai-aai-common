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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.aai.domain.notificationEvent.NotificationEvent;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class PojoUtilsTest {

	private PojoUtils pojoUtils;
	
	@Before
	public void init() {
		pojoUtils = new PojoUtils();
	}

	@Test
	public void testGetKeyValueList() throws Exception {
		Entity entity = getEntityObject();
		Person person = getPojoObject();

		List<KeyValueList> keyValueLists = pojoUtils.getKeyValueList(entity, person);

		for (KeyValueList keyValueList : keyValueLists) {

			if(keyValueList.getKey().equals("key")) {
				assertEquals("value", keyValueList.getValue());
			} else if (keyValueList.getKey().equals("name")) {
				assertEquals("Andrew", keyValueList.getValue());
			} else if(keyValueList.getKey().equals("nickname")) {
				assertEquals("Andy", keyValueList.getValue());
			} else if(keyValueList.getKey().equals("age")) {
				assertEquals("30", keyValueList.getValue());
			} else if(keyValueList.getKey().equals("weightlb")) {
				assertEquals("185", keyValueList.getValue());
			} else if(keyValueList.getKey().equals("heightcm")) {
				assertEquals("190", keyValueList.getValue());
			} else if(keyValueList.getKey().equals("pet")) {
				assertEquals("", keyValueList.getValue());
			}
		}
	}

	@Test
	public void testGetJsonFromObjectClassMockTest() throws Exception {
		PojoUtils pojoUtils = Mockito.mock(PojoUtils.class);
		String obj = "helloWorld";
		Mockito.when(pojoUtils.getJsonFromObject(Mockito.anyString())).thenCallRealMethod();

		pojoUtils.getJsonFromObject(obj);

		Mockito.verify(pojoUtils, Mockito.times(1)).getJsonFromObject(Mockito.anyString(), Mockito.eq(false), Mockito.eq(true));
	}

	@Test
	public void testGetJsonFromObjectClass() throws Exception {
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
	public void testGetJsonFromObjectClassNull() throws Exception {
		String res = pojoUtils.getJsonFromObject(null);

		assertNotNull(res);
		assertEquals("null", res);
	}

	@Test
	public void testGetJsonFromObjectNull() throws Exception {
		String res = pojoUtils.getJsonFromObject(null, false, true);

		assertNotNull(res);
		assertEquals("null", res);
	}

	@Test
	public void testGetJsonFromObject() throws Exception {
		LocalDateTime date = LocalDateTime.of(2017, Month.SEPTEMBER, 18, 10, 55, 0, 300);

		String res = pojoUtils.getJsonFromObject(date, false, false);
		assertNotNull(res);

		res = pojoUtils.getJsonFromObject(date, true, false);
		assertNotNull(res);

		res = pojoUtils.getJsonFromObject(date, true, true);
		assertNotNull(res);
	}
	
	@Test
	public void testGetJsonFromDynamicObject() throws Exception {
		DynamicEntity dynamicEntity = Mockito.mock(DynamicEntity.class);
		JAXBContext jaxbContext = Mockito.mock(JAXBContext.class);
		JAXBMarshaller marshaller = Mockito.mock(JAXBMarshaller.class);
		
		Mockito.when(jaxbContext.createMarshaller()).thenReturn(marshaller);
		
		String output = pojoUtils.getJsonFromDynamicObject(dynamicEntity, jaxbContext, true);
		assertEquals("", output);
	}
	
	@Test(expected = NullPointerException.class)
	public void testGetXmlFromObjectNull() throws Exception {
		pojoUtils.getXmlFromObject(null);
	}
	
	@Test
	public void testGetXmlFromObject() throws JAXBException, IOException {
		NotificationEvent notificationEvent = new NotificationEvent();
		notificationEvent.setCambriaPartition("partition");
		
		String res = pojoUtils.getXmlFromObject(notificationEvent);
		
		assertNotNull(res);
		assertTrue(res.contains("<NotificationEvent>"));
		assertTrue(res.contains("<cambria.partition>partition</cambria.partition>"));
		assertTrue(res.contains("</NotificationEvent>"));
	}
	
	@Test
	public void testGetLookupKeyEmptyKey() {
		String baseKey = "";
		Collection<String> keyProps = new ArrayList<String>();
		keyProps.add("key");
				
		HashMap<String, Object> lookup = new HashMap<String, Object>();
		lookup.put("key", "val");
		String expectedLookupKey = "key=val";
		
		String res = pojoUtils.getLookupKey(baseKey, lookup, keyProps);
		assertEquals(expectedLookupKey, res);
	}
	
	@Test
	public void testGetLookupKey() {
		String baseKey = "baseKey";
		Collection<String> keyProps = new ArrayList<String>();
		keyProps.add("key");
				
		HashMap<String, Object> lookup = new HashMap<String, Object>();
		lookup.put("key", "val");
		String expectedLookupKey = "baseKey&key=val";
		
		String res = pojoUtils.getLookupKey(baseKey, lookup, keyProps);
		
		assertEquals(expectedLookupKey, res);
	}
	
	@Test
	public void testGetLookupKeys() {
		HashMap<String, Object> lookup = new HashMap<>();
		lookup.put("multimapkey", "val");
		LinkedHashMap<String, HashMap<String, Object>> lookupHashes = new LinkedHashMap<>();
		lookupHashes.put("objectType", lookup);
		
		Multimap<String, String> multimap = ImmutableListMultimap.of("objectType", "multimapkey");
		String res = pojoUtils.getLookupKeys(lookupHashes, multimap);
		
		String lookupKey = "val";
		assertNotNull(res);
		assertEquals(lookupKey, res);
	}
		
	@Test
	public void testGetExampleObject() throws Exception {
		Person p = getPojoObject();
		pojoUtils.getExampleObject(p, true);
		assertNotNull(p);
		assertTrue(p.getName().contains("example-name-val-"));
		assertTrue(p.getNickname().contains("example-nickname-val-"));
		assertTrue(p.getPet().contains("example-pet-val-"));
		assertNotNull(p.getAge());
		assertNotNull(p.getHeightcm());
		assertNotNull(p.getWeightlb());
		assertTrue(p.isMarried());
	}
	
	private Entity getEntityObject() {
		Entity entity = new Entity();
		KeyValueList list = new KeyValueList();
		list.setKey("key");
		list.setValue("value");

		entity.setAction("action");
		entity.setKeyValueList(Lists.newArrayList(list));
		entity.setEquipmentRole("equipmentRole");
		entity.setSelfLink("selfLink");
		
		return entity;
	}

	private Person getPojoObject() {
		Person p = new Person("Andrew");
		p.setAge(30);
		p.setHeightcm((short) 190);
		p.setWeightlb(185);
		p.setNickname("Andy");
		p.setPet(null);
		return p;
	}

	class Person {

		private int age;
		private long weightlb;
		private short heightcm;
		private String nickname;
		private String name;
		private String pet;
		private boolean isMarried;

		public Person(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public long getWeightlb() {
			return weightlb;
		}

		public void setWeightlb(long weightlb) {
			this.weightlb = weightlb;
		}

		public short getHeightcm() {
			return heightcm;
		}

		public void setHeightcm(short heightcm) {
			this.heightcm = heightcm;
		}

		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPet() {
			return pet;
		}

		public void setPet(String pet) {
			this.pet = pet;
		}

		public boolean isMarried() {
			return isMarried;
		}

		public void setMarried(boolean isMarried) {
			this.isMarried = isMarried;
		}
		
	}
}
