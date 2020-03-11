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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Utility class to read/delete contents of log file
 */
public class LogFile {
	
    public static String getContents(String fileName) throws IOException {

        FileInputStream fileInputStream = null;
        String contents = null;
        try {
            fileInputStream = new FileInputStream("logs/" + fileName);
            contents = IOUtils.toString(fileInputStream, "UTF-8");
        }
        finally {
            if (fileInputStream != null) fileInputStream.close();
        }
        return contents;
    }
    public static void deleteContents(String fileName) throws IOException {
        FileUtils.write(new File("logs/" + fileName), "", Charset.defaultCharset());
    }
}
