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

import com.att.eelf.i18n.EELFResourceManager;

import org.onap.aai.cl.eelf.LogMessageEnum;

public enum OxmModelLoaderMsgs implements LogMessageEnum {

    /**
     * Invalid Model File
     * Arguments:
     * {0} = File/Dir
     * {1} = error
     */

    INVALID_OXM_FILE, INVALID_OXM_DIR,

    /**
     * Unable to load OXM schema: {0}
     *
     * <p>
     * Arguments:
     * {0} = error
     */
    OXM_LOAD_ERROR,

    /**
     * Successfully loaded schema: {0}
     *
     * <p>
     * Arguments:
     * {0} = oxm filename
     */
    LOADED_OXM_FILE;

    /**
     * Static initializer to ensure the resource bundles for this class are loaded...
     */
    static {
        EELFResourceManager.loadMessageBundle("oxm/OxmModelLoaderMsgs");
    }
}
