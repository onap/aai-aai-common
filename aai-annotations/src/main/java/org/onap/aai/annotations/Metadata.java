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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
public @interface Metadata {

    boolean isKey() default false;

    String description() default "";

    String nameProps() default "";

    String indexedProps() default "";

    String dependentOn() default "";

    String container() default "";

    String namespace() default "";

    String defaultValue() default "";

    String searchable() default "";

    String uniqueProps() default "";

    String requiredProps() default "";

    String uriTemplate() default "";

    String extendsFrom() default "";

    String isAbstract() default "";

    String alternateKeys1() default "";

    String maximumDepth() default "";

    String crossEntityReference() default "";

    String requires() default "";

    String dbAlias() default "";

    String dataLocation() default "";

    String containsSuggestibleProps() default "";

    String suggestionAliases() default "";

    String sourceOfTruthType() default "";

    String dslStartNodeProps() default "";

}
