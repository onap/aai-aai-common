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

package org.openecomp.aai.restcore.util;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.*;

public class GenerateEdgeRules {

    private static final EELFLogger LOG = EELFManager.getInstance().getLogger(GenerateEdgeRules.class);

    public static void main(String[] args) throws IOException, TemplateException {

        String filename = "/AAI8032.csv";
        InputStream inputStream = GenerateEdgeRules.class.getResourceAsStream(filename);
        Map<String, Integer> headers = new HashMap<>();
        Map<String, Object> edgeRulesMap = new TreeMap<String, Object>();
        List<Map<String, String>> edgeRules = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line = null;

            int rowNum = 0;

            // Retrieve the header line to map the indexes to their column names

            while ((line = reader.readLine()) != null) {

                if (rowNum == 0) {
                    headers = retrieveHeaderMap(line);
                } else {
                    String[] columns = line.split(",");

                    String originalNode = columns[headers.get("Orig NodeA|NodeB")];
                    String finalNode = columns[headers.get("Final NodeA|NodeB")];
                    String originalEdge = columns[headers.get("Orig EdgeLabel")];
                    String finalEdge = columns[headers.get("Final EdgeLabel")];

                    String lineage = columns[headers.get("Final Lineage")];
                    String originalParent = columns[headers.get("Orig ParentOf")];
                    String usesResource = columns[headers.get("Revised UsesResource")];
                    String hasDelTarget = columns[headers.get("Revised hasDelTarget")];
                    String svcInfra = columns[headers.get("Final SVC-INFRA")];
                    String svcInfraRev = "";

                    if(usesResource.equals("T"))
                        usesResource = "true";
                    else if(usesResource.equals("F"))
                        usesResource = "false";

                    if (hasDelTarget.equals("T") || hasDelTarget.equals("AB")) {
                        hasDelTarget = "true";
                    } else if (hasDelTarget.equals("F")) {
                        hasDelTarget = "false";
                    }

                    if (svcInfra.equals("T")) {
                        svcInfra = "true";
                    } else if (svcInfra.equals("F")) {
                        svcInfra = "false";
                    } else if (svcInfra.equals("R")) {
                        svcInfra = "reverse";
                    }

                    if (originalParent.equals("T")) {
                        if (lineage.trim().equalsIgnoreCase("CHILD")) {
                            lineage = "true";
                        } else if (lineage.trim().equalsIgnoreCase("PARENT")) {
                            lineage = "reverse";
                        }
                    } else {
                        lineage = "false";
                    }

                    Map<String, String> edgeMap = new HashMap<String, String>();

                    edgeMap.put("lineage", lineage);
                    edgeMap.put("usesResource", usesResource);
                    edgeMap.put("hasDelTarget", hasDelTarget);
                    edgeMap.put("SVC-INFRA", svcInfra);
                    edgeMap.put("SVC-INFRA-REV", svcInfraRev);
                    edgeMap.put("nodes", finalNode);
                    edgeMap.put("edge", finalEdge);
                    edgeMap.put("direction", columns[headers.get("Orig Direction")]);
                    edgeMap.put("multiplicity", columns[headers.get("Revised Multiplicity")]);

                    edgeRules.add(edgeMap);

                }
                ++rowNum;
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }

        edgeRulesMap.put("edgeRules", edgeRules);

        Collections.sort(edgeRules, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                return o1.get("nodes").compareTo(o2.get("nodes"));
            }
        });

        Configuration configuration = new Configuration();
        Template template = configuration.getTemplate("ajsc-aai/src/main/resources/EdgeRules.ftl");
        Writer file = new FileWriter(new File("ajsc-aai/src/main/resources" + "/" + "EdgeRules.txt"));
        template.process(edgeRulesMap, file);
    }

    private static Map<String, Integer> retrieveHeaderMap(String line){

        if(line == null)
            throw new NullPointerException();

        String[] columnNames = line.split(",");

        Map<String, Integer> map = new HashMap<String, Integer>();

        int index = 0;

        for(String columnName : columnNames){
            map.put(columnName, index++);
        }

        return map;
    }

}
