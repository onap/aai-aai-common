/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 *  Modifications Copyright © 2018 IBM.
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

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MapperUtilTest {

    private JSONObject expectedJson;
    private JSONObject sampleJson;

    @Before
    public void setup() {
        expectedJson = new JSONObject();
        sampleJson = new JSONObject();
    }

    @Test
    public void writeAsJSONStringTest() throws Exception {
        expectedJson.put("color", "black");
        expectedJson.put("shape", "box");
        SampleClass sample = new SampleClass("black", "box");
        Assert.assertEquals(expectedJson.toString(), MapperUtil.writeAsJSONString(sample));
    }

    @Test
    public void readAsObjectOfTest() throws Exception {
        sampleJson.put("color", "black");
        sampleJson.put("shape", "box");
        SampleClass expectedObject = new SampleClass("black", "box");
        SampleClass actualObject = MapperUtil.readAsObjectOf(SampleClass.class, sampleJson.toString());
        assertEquals(expectedObject.getColor(), actualObject.getColor());
        assertEquals(expectedObject.getShape(), actualObject.getShape());
    }
}


class SampleClass {
    private String color;
    private String shape;

    public SampleClass() {

    }

    public SampleClass(String c, String s) {
        color = c;
        shape = s;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }
}
