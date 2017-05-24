/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
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
 * ============LICENSE_END=========================================================
 */

package org.openecomp.aai.exceptions;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openecomp.aai.serialization.queryformats.QueryFormatTestHelper;
import org.openecomp.aai.util.AAIConstants;

public class AAIExceptionTest {
  private static final String code = "4004";
  private static final String details = "This is a detailed description of the exception.";
  private static final Throwable cause = new RuntimeException("This is a runtime exception.");
  private static final Throwable noMessage = new RuntimeException();
  
  @BeforeClass
	public static void configure() throws NoSuchFieldException, SecurityException, Exception {
		System.setProperty("AJSC_HOME", ".");
		System.setProperty("BUNDLECONFIG_DIR", "src/test/resources/bundleconfig-local");
		QueryFormatTestHelper.setFinalStatic(AAIConstants.class.getField("AAI_HOME_ETC_OXM"), "src/test/resources/org/openecomp/aai/introspection/");
	}

  /**
   * Test constructor with 0 params.
   *
   * @throws Exception the exception
   */
  @Test
  public void testConstructorWith0Params() throws Exception {
    AAIException exception = new AAIException();
    assertEquals(exception, exception);
  }
  
  /**
   * Test constructor with 1 params.
   *
   * @throws Exception the exception
   */
  @Test
  public void testConstructorWith1Params() throws Exception {
    AAIException exception = new AAIException(code);
    assertEquals(exception, exception);
  }
  
  /**
   * Test constructor with 2 params details.
   *
   * @throws Exception the exception
   */
  @Test
  public void testConstructorWith2ParamsDetails() throws Exception {
    AAIException exception = new AAIException(code, details);
    assertEquals(details, exception.getMessage());
  }
  
  /**
   * Test constructor with 2 params cause.
   *
   * @throws Exception the exception
   */
  @Test
  public void testConstructorWith2ParamsCause() throws Exception {
    AAIException exception = new AAIException(code, cause);
    assertEquals("java.lang.RuntimeException: This is a runtime exception.", exception.getMessage());
  }
  
  /**
   * Test constructor with 2 params null message.
   *
   * @throws Exception the exception
   */
  @Test
  public void testConstructorWith2ParamsNullMessage() throws Exception {
    AAIException exception = new AAIException(code, noMessage);
    assertEquals(noMessage.toString(), exception.getMessage());
  }
  
  /**
   * Test constructor with 3 params.
   *
   * @throws Exception the exception
   */
  @Test
  public void testConstructorWith3Params() throws Exception {
    AAIException exception = new AAIException(code, cause, details);
    String details = "This is a detailed description of the exception.";
    assertEquals(details, exception.getMessage());
  }
  
  /**
   * Test constructor with 3 params null message.
   *
   * @throws Exception the exception
   */
  @Test
  public void testConstructorWith3ParamsNullMessage() throws Exception {
    AAIException exception = new AAIException(code, noMessage, details);
    String detailString = new String(details);
    assertEquals(detailString, exception.getMessage());
  }
}
