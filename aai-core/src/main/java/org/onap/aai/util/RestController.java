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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import javax.ws.rs.client.Entity;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.onap.aai.exceptions.AAIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestController implements RestControllerInterface {

    private static final String TARGET_NAME = "AAI";
    private static final Logger LOGGER = LoggerFactory.getLogger(RestController.class);

    private static Client client = null;

    private String restSrvrBaseURL;

    private String overrideLocalHost = null;

    // To do - Come up with helper function that will automatically
    // generate the REST API path based on path parameter(s) and query parameter(s)!
    public static final String REST_APIPATH_COMPLEXES = "cloud-infrastructure/complexes";
    public static final String REST_APIPATH_COMPLEX = "cloud-infrastructure/complexes/complex/";
    public static final String REST_APIPATH_PSERVERS = "cloud-infrastructure/pservers";
    public static final String REST_APIPATH_PSERVER = "cloud-infrastructure/pservers/pserver/";
    public static final String REST_APIPATH_PHYSICALLINKS = "network/physical-links/";
    public static final String REST_APIPATH_PHYSICALLINK = "network/physical-links/physical-link/";
    public static final String REST_APIPATH_PINTERFACES = "network/p-interfaces/";
    public static final String REST_APIPATH_PINTERFACE = "network/p-interfaces/p-interface/";
    public static final String REST_APIPATH_VPLSPES = "network/vpls-pes/";
    public static final String REST_APIPATH_VPLSPE = "network/vpls-pes/vpls-pe/";
    public static final String REST_APIPATH_UPDATE = "actions/update/";
    public static final String REST_APIPATH_SEARCH = "search/nodes-query?search-node-type=";

    public static final String REST_APIPATH_CLOUDREGION = "cloud-infrastructure/cloud-regions/cloud-region/";
    public static final String REST_APIPATH_TENANT = "cloud-infrastructure/tenants/tenant/";
    public static final String REST_APIPATH_VIRTUAL_DATA_CENTER = "cloud-infrastructure/virtual-data-centers/virtual-data-center/";
    public static final String REST_APIPATH_VIRTUAL_DATA_CENTERS = "cloud-infrastructure/virtual-data-centers/";
    public static final String REST_APIPATH_GENERIC_VNF = "network/generic-vnfs/generic-vnf/";
    public static final String REST_APIPATH_GENERIC_VNFS = "network/generic-vnfs";
    public static final String REST_APIPATH_L3_NETWORK = "network/l3-networks/l3-network/";
    public static final String REST_APIPATH_L3_NETWORKS = "network/l3-networks";
    public static final String REST_APIPATH_INSTANCE_GROUP = "network/instance-groups/instance-group";
    public static final String REST_APIPATH_INSTANCE_GROUPS = "network/instance-groups";
    public static final String REST_APIPATH_VFMODULE = "nodes/vf-modules/vf-module/";

    public static final String REST_APIPATH_VCE = "network/vces/vce/";

    public static final String REST_APIPATH_SERVICE = "service-design-and-creation/services/service/";
    public static final String REST_APIPATH_LOGICALLINKS = "network/logical-links/";
    public static final String REST_APIPATH_LOGICALLINK = "network/logical-links/logical-link/";

    public RestController(String truststorePath, String truststorePassword, String keystorePath,
            String keystorePassword) throws AAIException {
        this.initRestClient(truststorePath, truststorePassword, keystorePath, keystorePassword);
    }

    /**
     * Inits the rest client.
     *
     * @throws AAIException the AAI exception
     */
    public void initRestClient(String truststorePath, String truststorePassword, String keystorePath,
            String keystorePassword) throws AAIException {
        if (client == null) {
            try {
                client = getHttpsAuthClient(truststorePath, truststorePassword, keystorePath, keystorePassword);
            } catch (KeyManagementException e) {
                throw new AAIException("AAI_7117", "KeyManagementException in REST call to DB: " + e.toString());
            } catch (Exception e) {
                throw new AAIException("AAI_7117", " Exception in REST call to DB: " + e.toString());
            }
        }
    }

    public Client getHttpsAuthClient(String truststorePath, String truststorePassword, String keystorePath,
            String keystorePassword) throws KeyManagementException, UnrecoverableKeyException, CertificateException,
            NoSuchAlgorithmException, KeyStoreException, IOException {
        return HttpsAuthClient.getClient(truststorePath, truststorePassword, keystorePath, keystorePassword);
    }

    public Client getHttpsAuthClient() throws KeyManagementException, UnrecoverableKeyException, CertificateException,
            NoSuchAlgorithmException, KeyStoreException, IOException, AAIException {
        return HttpsAuthClient.getClient();
    }

    /**
     * Sets the rest srvr base URL.
     *
     * @param baseURL the base URL
     * @throws AAIException the AAI exception
     */
    public void SetRestSrvrBaseURL(String baseURL) throws AAIException {
        if (baseURL == null)
            throw new AAIException("AAI_7117", "REST Server base URL cannot be null.");
        restSrvrBaseURL = baseURL;
    }

    /**
     * Gets the rest srvr base URL.
     *
     * @return the rest srvr base URL
     */
    public String getRestSrvrBaseURL() {
        return restSrvrBaseURL;
    }

    public <T> void Get(T t, String sourceID, String transId, String path, RestObject<T> restObject, boolean oldserver)
            throws AAIException {
        Get(t, sourceID, transId, path, restObject, oldserver, AAIConstants.AAI_RESOURCES_PORT);
    }

    /**
     * To do - optimization and automation. Also make it as generic as possible.
     *
     * @param <T> the generic type
     * @param t the t
     * @param sourceID the source ID
     * @param transId the trans id
     * @param path the path
     * @param restObject the rest object
     * @param oldserver the oldserver
     * @throws AAIException the AAI exception
     */
    @SuppressWarnings("unchecked")
    public <T> void Get(T t, String sourceID, String transId, String path, RestObject<T> restObject, boolean oldserver,
            int port) throws AAIException {
        String methodName = "Get";
        String url = "";
        transId += ":" + UUID.randomUUID().toString();

        LOGGER.debug(methodName + " start");

        restObject.set(t);

        if (oldserver) {
            url = AAIConfig.get(AAIConstants.AAI_OLDSERVER_URL) + path;
        } else {
            if (overrideLocalHost == null) {
                overrideLocalHost = AAIConfig.get(AAIConstants.AAI_LOCAL_OVERRIDE,
                        AAIConstants.AAI_LOCAL_OVERRIDE_DEFAULT);
            }
            if (AAIConstants.AAI_LOCAL_OVERRIDE_DEFAULT.equals(overrideLocalHost)) {
                url = String.format(AAIConstants.AAI_LOCAL_REST, port,
                        AAIConfig.get(AAIConstants.AAI_DEFAULT_API_VERSION_PROP)) + path;
            } else {
                url = String.format(AAIConstants.AAI_LOCAL_REST_OVERRIDE, overrideLocalHost,
                        AAIConfig.get(AAIConstants.AAI_DEFAULT_API_VERSION_PROP)) + path;
            }
        }
        LOGGER.debug(url + " for the get REST API");
        Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .header("X-TransactionId", transId)
                .header("X-FromAppId", sourceID)
                .header("Real-Time", "true")
                .get();

        // System.out.println("response.EntityInputSream()="+response.getEntityInputStream().toString());
        // System.out.println("response.tostring()="+response.toString());

        if (response.getStatus() == 200) {
            // System.out.println(methodName + ": url=" + url);
            T entity = response.readEntity((Class<T>) t.getClass());
            restObject.set(entity);
            restObject.set(t);
            LOGGER.debug(methodName + "REST api GET was successfull!");
        } else {
            // System.out.println(methodName + ": url=" + url + " failed with status=" +
            // response.getStatus());
            throw new AAIException("AAI_7116", methodName + " with status=" + response.getStatus() + ", url=" + url);
        }
    }

    /**
     * To do - optimization and automation. Also make it as generic as possible.
     *
     * @param <T> the generic type
     * @param t the t
     * @param sourceID the source ID
     * @param transId the trans id
     * @param path the path
     * @param restObject the rest object
     * @param oldserver the oldserver
     * @throws AAIException the AAI exception
     */
    @SuppressWarnings("unchecked")
    public <T> void Get(T t, String sourceID, String transId, String path, RestObject<T> restObject, String apiVersion)
            throws AAIException {
        String methodName = "Get";
        String url = "";
        transId += ":" + UUID.randomUUID().toString();

        LOGGER.debug(methodName + " start");

        restObject.set(t);

        url = AAIConfig.get(AAIConstants.AAI_SERVER_URL_BASE) + apiVersion + "/" + path;

        LOGGER.debug(url + " for the get REST API");
        Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .header("X-TransactionId", transId)
                .header("X-FromAppId", sourceID)
                .header("Real-Time", "true")
                .get();

        // System.out.println("response.EntityInputSream()="+response.getEntityInputStream().toString());
        // System.out.println("response.tostring()="+response.toString());

        if (response.getStatus() == 200) {
            // System.out.println(methodName + ": url=" + url);
            T entity = response.readEntity((Class<T>) t.getClass());
            restObject.set(entity);
            LOGGER.debug(methodName + "REST api GET was successfull!");
        } else {
            // System.out.println(methodName + ": url=" + url + " failed with status=" +
            // response.getStatus());
            throw new AAIException("AAI_7116", methodName + " with status=" + response.getStatus() + ", url=" + url);
        }
    }

    /**
     * Map json to object list.
     *
     * @param <T> the generic type
     * @param typeDef the type def
     * @param json the json
     * @param clazz the clazz
     * @return the list
     * @throws Exception the exception
     */
    private <T> List<T> mapJsonToObjectList(T typeDef, String json, Class<?> clazz) throws Exception {
        List<T> list;
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(json);
        TypeFactory t = TypeFactory.defaultInstance();
        list = mapper.readValue(json, t.constructCollectionType(ArrayList.class, clazz));

        return list;
    }

    /**
     * Put.
     *
     * @param <T> the generic type
     * @param t the t
     * @param sourceID the source ID
     * @param transId the trans id
     * @param path the path
     * @throws AAIException the AAI exception
     */
    public <T> void Put(T t, String sourceID, String transId, String path) throws AAIException {
        Put(t, sourceID, transId, path, false, AAIConstants.AAI_RESOURCES_PORT);
    }

    /**
     * Put.
     *
     * @param <T> the generic type
     * @param t the t
     * @param sourceID the source ID
     * @param transId the trans id
     * @param path the path
     * @throws AAIException the AAI exception
     */
    public <T> void Put(T t, String sourceID, String transId, String path, boolean oldserver) throws AAIException {
        Put(t, sourceID, transId, path, oldserver, AAIConstants.AAI_RESOURCES_PORT);
    }

    /**
     * Put.
     *
     * @param <T> the generic type
     * @param t the t
     * @param sourceID the source ID
     * @param transId the trans id
     * @param path the path
     * @param oldserver the oldserver
     * @throws AAIException the AAI exception
     */
    public <T> void Put(T t, String sourceID, String transId, String path, boolean oldserver, int port)
            throws AAIException {
        String methodName = "Put";
        String url = "";
        transId += ":" + UUID.randomUUID().toString();

        LOGGER.debug(methodName + " start");

        if (oldserver) {
            url = AAIConfig.get(AAIConstants.AAI_OLDSERVER_URL) + path;
        } else {
            if (overrideLocalHost == null) {
                overrideLocalHost = AAIConfig.get(AAIConstants.AAI_LOCAL_OVERRIDE,
                        AAIConstants.AAI_LOCAL_OVERRIDE_DEFAULT);
            }
            if (AAIConstants.AAI_LOCAL_OVERRIDE_DEFAULT.equals(overrideLocalHost)) {
                url = String.format(AAIConstants.AAI_LOCAL_REST, port,
                        AAIConfig.get(AAIConstants.AAI_DEFAULT_API_VERSION_PROP)) + path;
            } else {
                url = String.format(AAIConstants.AAI_LOCAL_REST_OVERRIDE, overrideLocalHost,
                        AAIConfig.get(AAIConstants.AAI_DEFAULT_API_VERSION_PROP)) + path;
            }
        }

        Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .header("X-TransactionId", transId)
                .header("X-FromAppId", sourceID)
                .header("Real-Time", "true")
                .put(Entity.entity(t, MediaType.APPLICATION_JSON));

        // System.out.println("response.tostring()="+response.toString());

        int statuscode = response.getStatus();
        if (statuscode >= 200 && statuscode <= 299) {
            LOGGER.debug(methodName + ": url=" + url + ", request=" + path);
        } else {
            throw new AAIException("AAI_7116", methodName + " with status=" + statuscode + ", url=" + url + ", msg="
                    + response.readEntity(String.class));
        }
    }

    /**
     * Put.
     *
     * @param <T> the generic type
     * @param t the t
     * @param sourceID the source ID
     * @param transId the trans id
     * @param path the path
     * @param apiVersion version number
     * @throws AAIException the AAI exception
     */
    public <T> void Put(T t, String sourceID, String transId, String path, String apiVersion) throws AAIException {
        String methodName = "Put";
        String url = "";
        transId += ":" + UUID.randomUUID().toString();

        LOGGER.debug(methodName + " start");

        url = AAIConfig.get(AAIConstants.AAI_SERVER_URL_BASE) + apiVersion + "/" + path;

        Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .header("X-TransactionId", transId)
                .header("X-FromAppId", sourceID)
                .header("Real-Time", "true")
                .put(Entity.entity(t, MediaType.APPLICATION_JSON));

        // System.out.println("response.tostring()="+response.toString());

        int statuscode = response.getStatus();
        if (statuscode >= 200 && statuscode <= 299) {
            LOGGER.debug(methodName + ": url=" + url + ", request=" + path);
        } else {
            throw new AAIException("AAI_7116", methodName + " with status=" + statuscode + ", url=" + url + ", msg="
                    + response.getEntity());
        }
    }

    public void Delete(String sourceID, String transId, String path) throws AAIException {
        Delete(sourceID, transId, path, AAIConstants.AAI_RESOURCES_PORT);
    }

    /**
     * Delete.
     *
     * @param sourceID the source ID
     * @param transId the trans id
     * @param path the path
     * @throws AAIException the AAI exception
     */
    public void Delete(String sourceID, String transId, String path, int port) throws AAIException {
        String methodName = "Delete";
        String url = "";
        transId += ":" + UUID.randomUUID().toString();

        LOGGER.debug(methodName + " start");

        String request = "{}";
        if (overrideLocalHost == null) {
            overrideLocalHost = AAIConfig.get(AAIConstants.AAI_LOCAL_OVERRIDE, AAIConstants.AAI_LOCAL_OVERRIDE_DEFAULT);
        }
        if (AAIConstants.AAI_LOCAL_OVERRIDE_DEFAULT.equals(overrideLocalHost)) {
            url = String.format(AAIConstants.AAI_LOCAL_REST, port,
                    AAIConfig.get(AAIConstants.AAI_DEFAULT_API_VERSION_PROP)) + path;
        } else {
            url = String.format(AAIConstants.AAI_LOCAL_REST_OVERRIDE, overrideLocalHost,
                    AAIConfig.get(AAIConstants.AAI_DEFAULT_API_VERSION_PROP)) + path;
        }
        Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .header("X-TransactionId", transId)
                .header("X-FromAppId", sourceID)
                .header("Real-Time", "true")
                .delete();

        if (response.getStatus() == 404) { // resource not found
            LOGGER.info(
                    "Resource does not exist...: " + response.getStatus() + ":" + response.readEntity(String.class));
        } else if (response.getStatus() == 200 || response.getStatus() == 204) {
            LOGGER.info("Resource " + url + " deleted");
        } else {
            LOGGER.error("Deleting Resource failed: " + response.getStatus() + ":" + response.readEntity(String.class));
            throw new AAIException("AAI_7116", "Error during DELETE");
        }
    }

    public <T> String Post(T t, String sourceID, String transId, String path) throws Exception {
        return Post(t, sourceID, transId, path, AAIConfig.get(AAIConstants.AAI_DEFAULT_API_VERSION_PROP));
    }

    /**
     * Post.
     *
     * @param <T> the generic type
     * @param t the t
     * @param sourceID the source ID
     * @param transId the trans id
     * @param path the path
     * @param apiVersion the apiVersion
     * @return the string
     * @throws Exception the exception
     */
    public <T> String Post(T t, String sourceID, String transId, String path, String apiVersion) throws Exception {
        String methodName = "Post";
        String url = "";
        transId += ":" + UUID.randomUUID().toString();

        LOGGER.debug(methodName + " start");

        try {

            url = AAIConfig.get(AAIConstants.AAI_SERVER_URL_BASE) + apiVersion + "/" + path;

            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header("X-TransactionId", transId)
                    .header("X-FromAppId", sourceID)
                    .header("Real-Time", "true")
                    .post(Entity.entity(t, MediaType.APPLICATION_JSON));

            int statuscode = response.getStatus();
            if (statuscode >= 200 && statuscode <= 299) {
                LOGGER.debug(methodName + "REST api POST was successful!");
                return response.readEntity(String.class);
            } else {
                throw new AAIException("AAI_7116", methodName + " with status=" + statuscode + ", url=" + url + ", msg="
                        + response.readEntity(String.class));
            }

        } catch (AAIException e) {
            throw new AAIException("AAI_7116", methodName + " with url=" + url + ", Exception: " + e.toString());
        } catch (Exception e) {
            throw new AAIException("AAI_7116", methodName + " with url=" + url + ", Exception: " + e.toString());

        } finally {
        }
    }

    /**
     * Gets the single instance of RestController.
     *
     * @param <T> the generic type
     * @param clazz the clazz
     * @return single instance of RestController
     * @throws IllegalAccessException the illegal access exception
     * @throws InstantiationException the instantiation exception
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     */
    public <T> T getInstance(Class<T> clazz) throws IllegalAccessException, InstantiationException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        return clazz.getDeclaredConstructor().newInstance();
    }

    /**
     * Does resource exist.
     *
     * @param <T> the generic type
     * @param resourcePath the resource path
     * @param resourceClassName the resource class name
     * @param fromAppId the from app id
     * @param transId the trans id
     * @return the t
     */
    /*
     * DoesResourceExist
     *
     * To check whether a resource exist or get a copy of the existing version of the resource
     *
     * Resourcepath: should contain the qualified resource path (including encoded unique key identifier value),
     * resourceClassName: is the canonical name of the resource class name,
     * fromAppId:
     * transId:
     *
     * Will return null (if the resource doesn’t exist) (or)
     * Will return the specified resource from the Graph.
     *
     * Example:
     * LogicalLink llink = new LogicalLink();
     * String resourceClassName = llink.getClass().getCanonicalName();
     * llink = RestController.DoesResourceExist("network/logical-links/logical-link/" + <encoded-link-name>,
     * resourceClassName, fromAppId, transId);
     */
    public <T> T DoesResourceExist(String resourcePath, String resourceClassName, String fromAppId, String transId) {

        try {

            RestObject<T> restObj = new RestObject<T>();
            @SuppressWarnings("unchecked")
            T resourceObj = (T) getInstance(Class.forName(resourceClassName));
            restObj.set(resourceObj);
            Get(resourceObj, fromAppId, transId, resourcePath, restObj, false, AAIConstants.AAI_RESOURCES_PORT);

            resourceObj = restObj.get();
            if (resourceObj != null)
                return resourceObj;

        } catch (AAIException e) {

        } catch (ClientErrorException che) {

        } catch (Exception e) {

        }

        return null;
    }

    /**
     * Patch.
     *
     * @param <T> the generic type
     * @param sourceID the source ID
     * @param transId the trans id
     * @param path the path
     * @throws AAIException the AAI exception
     */
    public <T> void Patch(T t, String sourceID, String transId, String path) throws AAIException {
        String methodName = "Patch";
        String url = "";
        transId += ":" + UUID.randomUUID().toString();

        int numRetries = 5;
        Response response = null;
        int statusCode = -1;

        try {
            if (overrideLocalHost == null) {
                overrideLocalHost = AAIConfig.get(AAIConstants.AAI_LOCAL_OVERRIDE,
                        AAIConstants.AAI_LOCAL_OVERRIDE_DEFAULT);
            }
            if (AAIConstants.AAI_LOCAL_OVERRIDE_DEFAULT.equals(overrideLocalHost)) {
                url = String.format(AAIConstants.AAI_LOCAL_REST, AAIConstants.AAI_RESOURCES_PORT,
                        AAIConfig.get(AAIConstants.AAI_DEFAULT_API_VERSION_PROP)) + path;
            } else {
                url = String.format(AAIConstants.AAI_LOCAL_REST_OVERRIDE, overrideLocalHost,
                        AAIConfig.get(AAIConstants.AAI_DEFAULT_API_VERSION_PROP)) + path;
            }

            do {

                response = client.target(url)
                        .request(MediaType.APPLICATION_JSON)
                        .header("X-TransactionId", transId)
                        .header("X-FromAppId", sourceID)
                        .header("X-HTTP-Method-Override", "PATCH")
                        .post(Entity.entity(t, "application/merge-patch+json"));

                statusCode = response.getStatus();

                if (statusCode >= 200 && statusCode <= 299) {
                    LOGGER.debug(methodName + "REST api PATCH was successful!");
                    return;
                } else {
                    LOGGER.debug(methodName + "Unable to make the patch request to url " + url + " so retrying");
                }

                numRetries--;

            } while (numRetries >= 0);

            LOGGER.debug(methodName + "Unable to make the patch request to url " + url + " even after trying = "
                    + numRetries + " times.");
            throw new AAIException("AAI_7116", methodName + " with status=" + statusCode + ", url=" + url + ", msg="
                    + response.readEntity(String.class));

        } catch (AAIException e) {
            throw new AAIException("AAI_7116", methodName + " with url=" + url + ", Exception: " + e.toString());
        } catch (Exception e) {
            throw new AAIException("AAI_7116", methodName + " with url=" + url + ", Exception: " + e.toString());

        } finally {
        }

    }
}
