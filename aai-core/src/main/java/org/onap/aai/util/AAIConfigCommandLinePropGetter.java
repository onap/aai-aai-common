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

/*
 * The script deobfuscatePW.sh needs to retrieve pws from the AAIConfig file.
 * As AAIConfig has no main to be callable on the command line, this class helps
 * by providing one for accessing AAIConfig that way.
 * (AAIConfig deobfuscates pws itself, so we just need to call its .get() on the desired pw.)
 * 
 * This could be used to get any property from AAIConfig via the command line,
 * not just the pws, even though it was made for pw-related needs.
 */
public class AAIConfigCommandLinePropGetter {

    /**
     * The main method.
     *
     * @param args the arguments
     */
    /*
     * usage:
     * AAIConfigCommandLinePropGetter propertyname
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            // System.out.println("only one property may be requested at a time");
            // System.out.println("usage: AAIConfigCommandLinePropGetter propertyname");
        }
        try {
            AAIConfig.init();
            String value = AAIConfig.get(args[0]);
            if (value != null) {
                System.out.println(value); // bc this utility used by a shell script so it needs the result sent to
                                           // stdout
            } else {
                System.out.println("requested property could not be found");
            }
        } catch (AAIException e) {
            // System.out.println("exception:" + e.toString()); //TODO is this reasonable?
        } finally {
            System.exit(0);
        }

    }

}
