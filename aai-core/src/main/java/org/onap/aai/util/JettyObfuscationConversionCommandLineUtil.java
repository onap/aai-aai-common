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

package org.onap.aai.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.jetty.util.security.Password;

/*
 * The purpose of this class is to be a tool for
 * manually applying jetty obfuscation/deobfuscation
 * so that one can obfuscate the various passwords/secrets
 * in aaiIncomingAdapterConfig.properties.
 * 
 * Originally, they were being encrypted by a similar
 * command line utility, however the encryption key
 * was being hardcoded in the src package
 * which is a security violation.
 * Since this ultimately just moved the problem of how
 * to hide secrets to a different secret in a different file,
 * and since that encryption was really just being done to
 * obfuscate those values in case someone needed to look at
 * properties with others looking at their screen,
 * we decided that jetty obfuscation would be adequate
 * for that task as well as
 * removing the "turtles all the way down" secret-to-hide-
 * the-secret-to-hide-the-secret problem.
 */
public class JettyObfuscationConversionCommandLineUtil {

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("e", true, "obfuscate the given string");
        options.addOption("d", true, "deobfuscate the given string");

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);
            String toProcess = null;

            if (cmd.hasOption("e")) {
                toProcess = cmd.getOptionValue("e");
                String encoded = Password.obfuscate(toProcess);
                System.out.println(encoded);
            } else if (cmd.hasOption("d")) {
                toProcess = cmd.getOptionValue("d");
                String decoded_str = Password.deobfuscate(toProcess);
                System.out.println(decoded_str);
            } else {
                usage();
            }
        } catch (ParseException e) {
            System.out.println("failed to parse input");
            System.out.println(e.toString());
            usage();
        } catch (Exception e) {
            System.out.println("exception:" + e.toString());
        }
    }

    /**
     * Usage.
     */
    private static void usage() {
        System.out.println("usage:");;
        System.out.println("-e [string] to obfuscate");
        System.out.println("-d [string] to deobfuscate");
        System.out.println("-h help");
    }
}
