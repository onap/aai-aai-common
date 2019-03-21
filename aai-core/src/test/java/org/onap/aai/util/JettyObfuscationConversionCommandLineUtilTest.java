/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright © 2018 IBM.
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

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class JettyObfuscationConversionCommandLineUtilTest {

    private final ByteArrayOutputStream testOut = new ByteArrayOutputStream();

    /**
     * Test.
     */
    @Test
    public void test() {
        // setup, this will catch main's print statements for evaluation
        PrintStream oldOutputStream = System.out;
        System.setOut(new PrintStream(testOut));

        /* ------ TEST OBFUSCATION ---- */
        JettyObfuscationConversionCommandLineUtil.main(new String[] {"-e", "hello world"});
        /*
         * testOut was also catching any logging statements which interfered with result checking.
         * This regex business was the workaround - it tries to find the expected value in
         * the results and asserts against that.
         */
        String obfResult = testOut.toString();
        String obfExpected = "OBF:1thf1ugo1x151wfw1ylz11tr1ymf1wg21x1h1uh21th7";
        Pattern obfExpectPat = Pattern.compile(obfExpected);
        Matcher obfMatch = obfExpectPat.matcher(obfResult);
        assertTrue(obfMatch.find());

        testOut.reset(); // clear out previous result

        /* ------ TEST DEOBFUSCATION ----- */
        JettyObfuscationConversionCommandLineUtil.main(new String[] {"-d", obfExpected});
        String deobfResult = testOut.toString();
        String deobfExpected = "hello world";
        Pattern deobfExpectPat = Pattern.compile(deobfExpected);
        Matcher deobfMatch = deobfExpectPat.matcher(deobfResult);
        assertTrue(deobfMatch.find());

        // clean up, resets to stdout
        System.setOut(oldOutputStream);
    }

    /**
     * Test.
     */
    @Test
    public void testUsage() {
        System.setOut(new PrintStream(testOut));

        /* ------ TEST OBFUSCATION ---- */
        JettyObfuscationConversionCommandLineUtil.main(new String[] {"-f", "hello world"});
        /*
         * testOut was also catching any logging statements which interfered with result checking.
         * This regex business was the workaround - it tries to find the expected value in
         * the results and asserts against that.
         */
        String obfResult = testOut.toString();
        assertTrue(obfResult.startsWith("failed to parse input"));

        testOut.reset(); // clear out previous result

    }

}
