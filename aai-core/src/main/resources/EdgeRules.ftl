<#--

    ============LICENSE_START=======================================================
    org.onap.aai
    ================================================================================
    Copyright © 2017 AT&T Intellectual Property. All rights reserved.
    ================================================================================
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    ============LICENSE_END=========================================================

    ECOMP is a trademark and service mark of AT&T Intellectual Property.

-->
public static final Multimap<String, String> EdgeRules = new ImmutableSetMultimap.Builder<String, String>()
<#list edgeRules as edgeRule>
        .putAll("${edgeRule["nodes"]}",
                "${edgeRule["edge"]},${edgeRule["direction"]},${edgeRule["multiplicity"]},${edgeRule["lineage"]},${edgeRule["uses-resource"]},${edgeRule["has-del-target"]},${edgeRule["SVC-INFRA"]}")
</#list>
.build();
