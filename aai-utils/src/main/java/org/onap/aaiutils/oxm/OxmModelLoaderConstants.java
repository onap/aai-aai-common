/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property.
 * Copyright © 2017-2018 Amdocs
 * All rights reserved.
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

package org.onap.aaiutils.oxm;

public class OxmModelLoaderConstants {

    public static final String AaiUtils_FILESEP =
        (System.getProperty("file.separator") == null) ? "/" : System.getProperty("file.separator");

    public static final String AaiUtils_SPECIFIC_CONFIG =
        System.getProperty("CONFIG_HOME") + AaiUtils_FILESEP;

    public static final String AaiUtils_HOME_MODEL =
        AaiUtils_SPECIFIC_CONFIG + "model" + AaiUtils_FILESEP;
}
