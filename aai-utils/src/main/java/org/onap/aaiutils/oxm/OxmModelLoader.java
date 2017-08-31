/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property.
 * Copyright © 2017 Amdocs
 * All rights reserved.
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
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aaiutils.oxm;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.openecomp.cl.eelf.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBException;


public class OxmModelLoader {

    final static Pattern p = Pattern.compile("aai_oxm_(.*).xml");

  private static Map<String, DynamicJAXBContext> versionContextMap  = new ConcurrentHashMap();

  private static org.openecomp.cl.api.Logger logger = LoggerFactory.getInstance()
      .getLogger(OxmModelLoader.class.getName());

  public synchronized static void loadModels() throws Exception {
    ClassLoader cl = OxmModelLoader.class.getClassLoader();
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
    Resource[] resources;
    try {
      resources = resolver.getResources("classpath*:/oxm/aai_oxm*.xml");
    } catch (IOException ex) {
      logger.error( OxmModelLoaderMsgs.OXM_LOAD_ERROR, ex.getMessage());
      throw new FileNotFoundException("OXM files not found on classpath.");
    }

    if (resources.length == 0) {
      logger.error(OxmModelLoaderMsgs.OXM_LOAD_ERROR, "No OXM schema files found on classpath");
      throw new Exception("Failed to load schema");
    }

    for (Resource resource : resources) {
      Matcher matcher = p.matcher(resource.getFilename());

      if (matcher.matches()) {
        try {
          OxmModelLoader.loadModel(matcher.group(1), resource);
        } catch (Exception e) {
          logger.error(OxmModelLoaderMsgs.OXM_LOAD_ERROR, "Failed to load " + resource.getFilename()
              + ": " + e.getMessage());
          throw new Exception("Failed to load schema");
        }
      }
    }
  }

  public static DynamicJAXBContext getContextForVersion(String version) throws Exception {
    if (versionContextMap == null || versionContextMap.isEmpty()) {
      loadModels();
    } else if (!versionContextMap.containsKey(version)) {
      String filename = OxmModelLoaderConstants.AaiUtils_HOME_MODEL + "aai_oxm_" + version + ".xml";
      try {
        loadModel(version, new File(filename));
      } catch (Exception e) {
        throw new FileNotFoundException(filename);
      }
    }

    return versionContextMap.get(version);
  }

  public static Map<String, DynamicJAXBContext> getVersionContextMap() {
    return versionContextMap;
  }

  public static void setVersionContextMap(Map<String, DynamicJAXBContext> versionContextMap) {
    OxmModelLoader.versionContextMap = versionContextMap;
  }

  private synchronized static void loadModel ( String version, File file )
          throws JAXBException, IOException {
      InputStream inputStream = new FileInputStream ( file );
      loadModel ( version, file.getName (), inputStream );
  }

  private synchronized static void loadModel ( String version, Resource resource )
          throws JAXBException, IOException {
      InputStream inputStream = resource.getInputStream ();
      loadModel ( version, resource.getFilename (), inputStream );
  }

  private synchronized static void loadModel ( String version, String resourceName,
                                               InputStream inputStream )
          throws JAXBException, IOException {
      Map<String, Object> properties = new HashMap<String, Object> ();
      properties.put ( JAXBContextProperties.OXM_METADATA_SOURCE, inputStream );
      final DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory
              .createContextFromOXM ( Thread.currentThread ().getContextClassLoader (), properties );
      versionContextMap.put ( version, jaxbContext );
      logger.info ( OxmModelLoaderMsgs.LOADED_OXM_FILE, resourceName );
    }

}
