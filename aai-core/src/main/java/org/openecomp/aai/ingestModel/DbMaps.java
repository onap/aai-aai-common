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

package org.openecomp.aai.ingestModel;
import java.util.HashMap; 
import java.util.LinkedHashMap;
import java.util.Map; 

import com.google.common.collect.ArrayListMultimap; 
import com.google.common.collect.Multimap;

/**
 * The Class DbMaps.
 */
public class DbMaps {

	// from oxm file
    public  Multimap<String, String> NodeAltKey1Props      = ArrayListMultimap.create();
    
    public  Multimap<String, String> NodeDependencies      = ArrayListMultimap.create();
    
    public  Multimap<String, String> NodeNameProps         = ArrayListMultimap.create();
    
    public  Multimap<String, String> NodeMapIndexedProps   = ArrayListMultimap.create();
    
    public  Multimap<String, String> NodeMapUniqueProps    = ArrayListMultimap.create();
    
	public Map<Integer, String>      EdgeInfoMap            = new LinkedHashMap<Integer, String>();
	
	public Map<String, String>       ReservedPropNames      = new HashMap<String, String>();
     
    // from AAIResources
    public  Multimap<String, String> NodeProps              = ArrayListMultimap.create();   
    
    public  Multimap<String, String> NodeKeyProps           = ArrayListMultimap.create();
    
    public  Map<String, String>      NodePlural             = new HashMap<String, String>();
    
    public  Map<String, String>      NodeNamespace          = new HashMap<String, String>();
	
	public  Map<String, String>      PropertyVersionInfoMap = new HashMap<String, String>(); 
	
	public  Map<String, String>      NodeVersionInfoMap 	= new HashMap<String, String>(); 
    
    public  Map<String, String>      PropertyDataTypeMap    = new HashMap<String, String>();

	}
