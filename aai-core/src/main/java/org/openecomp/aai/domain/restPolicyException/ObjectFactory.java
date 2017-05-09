/*-
 * ============LICENSE_START=======================================================
 * org.openecomp.aai
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * ============LICENSE_END=========================================================
 */

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.02.11 at 04:54:39 PM EST 
//


package org.openecomp.aai.domain.restPolicyException;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openecomp.aai.domain.restPolicyException package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openecomp.aai.domain.restPolicyException
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Fault }.
     *
     * @return the fault
     */
    public Fault createFault() {
        return new Fault();
    }

    /**
     * Create an instance of {@link Fault.RequestError }
     *
     * @return the request error
     */
    public Fault.RequestError createFaultRequestError() {
        return new Fault.RequestError();
    }

    /**
     * Create an instance of {@link Fault.RequestError.PolicyException }
     *
     * @return the policy exception
     */
    public Fault.RequestError.PolicyException createFaultRequestErrorPolicyException() {
        return new Fault.RequestError.PolicyException();
    }

    /**
     * Create an instance of {@link Fault.RequestError.PolicyException.Variables }
     *
     * @return the variables
     */
    public Fault.RequestError.PolicyException.Variables createFaultRequestErrorPolicyExceptionVariables() {
        return new Fault.RequestError.PolicyException.Variables();
    }

}
