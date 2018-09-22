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
package org.onap.aai;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.onap.aai.setup.*;
import org.onap.aai.util.AAIConstants;

/**
 * Quick and dirty access to test schema files
 *
 */
public abstract class AbstractConfigTranslator extends ConfigTranslator {

    public AbstractConfigTranslator(SchemaLocationsBean bean, SchemaVersions schemaVersions) {
        super(bean, schemaVersions);
    }

    

    /* (non-Javadoc)
     * @see org.onap.aai.setup.ConfigTranslator#getNodeFiles()
     */
    @Override
    public Map<SchemaVersion, List<String>> getNodeFiles() {
        String prefix = bean.getNodeDirectory() + AAIConstants.AAI_FILESEP ;
        
        String suffix = ".xml";
        
        Map<SchemaVersion, List<String>> files = new TreeMap<>();
        for (SchemaVersion v : schemaVersions.getVersions()) {
            
            List<String> container = getVersionNodeFiles(v);
            
            
            files.put(v, container);
        }
        
        return files;
    }
    

    public List<String> getVersionNodeFiles(SchemaVersion v) {
        Pattern p = Pattern.compile("aai(.*)"+"_oxm_(.*).xml" );
        
        List<String> container = new ArrayList<>();
        String directoryName = bean.getNodeDirectory() + AAIConstants.AAI_FILESEP  + v.toString() + AAIConstants.AAI_FILESEP ;
        
        File[] files = new File(directoryName).listFiles();
        for (File f : files) {
            String fileName = f.getName();
            Matcher m = p.matcher(fileName);
            if (m.find()) {
                String file = directoryName + m.group();
                container.add(file.toString());
            }
            
        }
        return container;
        
    }

}
