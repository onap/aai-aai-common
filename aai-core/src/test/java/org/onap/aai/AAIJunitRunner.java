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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.runners.Parameterized;
import org.onap.aai.util.AAIConstants;

public class AAIJunitRunner extends Parameterized {

    public AAIJunitRunner(Class<?> klass) throws Throwable {
        super(klass);
        setProps();
        modifyOxmHome();
    }

    public void setProps() {
        System.setProperty("AJSC_HOME", ".");
        System.setProperty("BUNDLECONFIG_DIR", "src/test/resources/bundleconfig-local");
    }

    public void modifyOxmHome() {
        try {
            Field aaiConstantsField = AAIConstants.class.getField("AAI_HOME_ETC_OXM");
            setFinalStatic(aaiConstantsField, "../aai-schema/src/main/resources/oxm/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }
}
