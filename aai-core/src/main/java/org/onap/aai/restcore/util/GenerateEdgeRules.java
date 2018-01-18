/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
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
package org.onap.aai.restcore.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.*;

import org.onap.aai.serialization.db.EdgeRules;
import org.onap.aai.introspection.Version;

public class GenerateEdgeRules {

    public static void main(String[] args) throws IOException, TemplateException {

        String filename = "/edgeLabelMigration.csv";
        InputStream inputStream = GenerateEdgeRules.class.getResourceAsStream(filename);
        Map<String, Integer> headers = new HashMap<>();

        List<EdgeRuleBean> rulesToWriteV12 = new ArrayList<>();
        List<EdgeRuleBean> rulesToWriteV7 = new ArrayList<>();
        List<EdgeRuleBean> rulesToWriteV8 = new ArrayList<>();
        List<EdgeRuleBean> rulesToWriteV9 = new ArrayList<>();
        List<EdgeRuleBean> rulesToWriteV10 = new ArrayList<>();
        List<EdgeRuleBean> rulesToWriteV11 = new ArrayList<>();

        ArrayList <String> rulesWeAlreadyHave = new ArrayList <String>();

        EdgeRules rulesV8 = EdgeRules.getInstance(Version.v8);
        EdgeRules rulesV9 = EdgeRules.getInstance(Version.v9);
        EdgeRules rulesV10 = EdgeRules.getInstance(Version.v10);
        EdgeRules rulesV11 = EdgeRules.getInstance(Version.v11);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line = null;
            int rowNum = 0;
            while ((line = reader.readLine()) != null) {
            	if (rowNum == 0) {
                    headers = retrieveHeaderMap(line);
                }
                else {
                	EdgeRuleBean data = new EdgeRuleBean();
                	String[] columns = line.split(",");
                	String oldNodeA = columns[headers.get("from")];
                	String oldNodeB = columns[headers.get("to")];
                	String oldEdgeLabel = columns[headers.get("label")];

                    String nodeA = columns[headers.get("new from")];
                    data.setFrom(nodeA);
                    String nodeB = columns[headers.get("new to")];
                    data.setTo(nodeB);

                    String edgeLabel = columns[headers.get("new label")];
                    data.setLabel( edgeLabel );


                    // Note: it is assumed that if we know the two NodeTypes and the edgeLabel, we can
                    //     uniquely identify an edgeRule -- so if that key is found twice, it is a
                    //     problem with our CSV file.  Note -we check with the nodeTypes in both directions.
                    String key1 = nodeA + "|" + nodeB + "|" + edgeLabel;
                    String key2 = nodeB + "|" + nodeA + "|" + edgeLabel;
                    if( rulesWeAlreadyHave.contains(key1) ){
                    	throw new Exception ("Duplicate rule found for [" + key1 + "] -- please fix the CSV file. ");
                    }
                    else if( rulesWeAlreadyHave.contains(key2) ){
                    	throw new Exception ("Duplicate rule found for [" + key2 + "] -- please fix the CSV file. ");
                    }
                    else {
                    	rulesWeAlreadyHave.add(key1);
                    	rulesWeAlreadyHave.add(key2);
                    }

                    String direction = columns[headers.get("new direction")];
                    data.setDirection(direction);

                    String multiplicity = columns[headers.get("new multiplicity")];
                    data.setMultiplicity(multiplicity);

                    String lineage = columns[headers.get("new contains-other-v")];
                    data.setLineage(lineage);

                    String deleteOtherV = columns[headers.get("new delete-other-v")];
                    data.setDeleteOtherV(deleteOtherV);

                    String svcInfra = columns[headers.get("new SVC-INFRA")];
                    data.setSvcInfra(svcInfra);

                    String prevDel = columns[headers.get("new prevent-delete")];
                    data.setPreventDelete(prevDel);

                    String defaultVal = columns[headers.get("new default")];
                    if( defaultVal.equals("T") ){
                    	data.setDefault("true");
                    }
                    else if( defaultVal.equals("F") ){
                    	data.setDefault("false");
                    }

                    rulesToWriteV12.add(data);

                    if( rulesV8.hasEdgeRule(oldNodeA, oldNodeB, oldEdgeLabel) ){
                    	rulesToWriteV8.add(data);
                    }

                    if( rulesV9.hasEdgeRule(oldNodeA, oldNodeB, oldEdgeLabel) ){
                    	rulesToWriteV9.add(data);
                    }

                    if( rulesV10.hasEdgeRule(oldNodeA, oldNodeB, oldEdgeLabel) ){
                    	rulesToWriteV10.add(data);
                    }

                    if( rulesV11.hasEdgeRule(oldNodeA, oldNodeB, oldEdgeLabel) ){
                    	rulesToWriteV11.add(data);
                    }
                }
                ++rowNum;
            }

            Configuration configuration = new Configuration();
            Template template = configuration.getTemplate("src/main/resources/edgerulesTemplate.ftlh");
            Writer file = new FileWriter(new File("src/main/resources/EdgeRulesWithNewLabels_v12.json"));
            Map<String, List<EdgeRuleBean>> wrappedRules = new HashMap<>();
    		wrappedRules.put("wrappedRules", rulesToWriteV12);
    		template.process(wrappedRules, file);
    		file.close();

    		file = new FileWriter(new File("src/main/resources/EdgeRulesWithNewLabels_v7.json"));
            wrappedRules = new HashMap<>();
       		wrappedRules.put("wrappedRules", rulesToWriteV7);
       		template.process(wrappedRules, file);
       		file.close();

    		file = new FileWriter(new File("src/main/resources/EdgeRulesWithNewLabels_v8.json"));
            wrappedRules = new HashMap<>();
       		wrappedRules.put("wrappedRules", rulesToWriteV8);
       		template.process(wrappedRules, file);
       		file.close();


    		file = new FileWriter(new File("src/main/resources/EdgeRulesWithNewLabels_v9.json"));
            wrappedRules = new HashMap<>();
       		wrappedRules.put("wrappedRules", rulesToWriteV9);
       		template.process(wrappedRules, file);
       		file.close();

    		file = new FileWriter(new File("src/main/resources/EdgeRulesWithNewLabels_v10.json"));
            wrappedRules = new HashMap<>();
       		wrappedRules.put("wrappedRules", rulesToWriteV10);
       		template.process(wrappedRules, file);
       		file.close();

    		file = new FileWriter(new File("src/main/resources/EdgeRulesWithNewLabels_v11.json"));
            wrappedRules = new HashMap<>();
       		wrappedRules.put("wrappedRules", rulesToWriteV11);
       		template.process(wrappedRules, file);
       		file.close();

        } catch(Exception ex){
            ex.printStackTrace();
        }


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
