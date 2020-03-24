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

import org.onap.aai.exceptions.AAIException;

public interface RestControllerInterface {

    public <T> void Get(T t, String sourceID, String transId, String path, RestObject<T> restObject, boolean oldserver)
            throws AAIException;

    public <T> void Get(T t, String sourceID, String transId, String path, RestObject<T> restObject, String apiVersion)
            throws AAIException;

    public <T> void Patch(T t, String sourceID, String transId, String path) throws AAIException;

    public <T> void Put(T t, String sourceID, String transId, String path) throws AAIException;

    public <T> void Put(T t, String sourceID, String transId, String path, boolean oldserver) throws AAIException;

    public void Delete(String sourceID, String transId, String path) throws AAIException;

    public <T> T getInstance(Class<T> clazz) throws IllegalAccessException, InstantiationException;

    public <T> T DoesResourceExist(String resourcePath, String resourceClassName, String fromAppId, String transId);
}
