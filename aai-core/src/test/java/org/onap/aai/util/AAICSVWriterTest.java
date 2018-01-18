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
package org.onap.aai.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class AAICSVWriterTest {

	private  final String TEMP_DIR=System.getProperty("java.io.tmpdir")+"/test.csv";
	
	@Test
	public void writeFile() throws IOException
	{
		
		
		FileWriter fileWriter = new FileWriter(TEMP_DIR);
		AAICSVWriter aaicsvWriter = new AAICSVWriter(fileWriter, ",", '\'', null);
		aaicsvWriter.writeColumn(new String[]{"id", "name", null});
		aaicsvWriter.writeColumn(null);
		aaicsvWriter.writeNext(new String[]{"1", "Test1"}, true);
		aaicsvWriter.writeNext(new String[]{"1", "Test1"});
		aaicsvWriter.writeNext(new String[]{"1", "Test1"});
		aaicsvWriter.writeNext(new String[]{"1", "Test@"}, false);
		aaicsvWriter.writeNext(new String[]{"1", "Test1"});
		aaicsvWriter.writeNext(new String[]{"1", "Test1"});
		aaicsvWriter.writeNext(new String[]{"1", "Test1"});
		aaicsvWriter.writeNext(new String[]{"1", "Test1"});
		aaicsvWriter.writeNext(new String[]{"1", "Test1"});
		aaicsvWriter.writeNext(new String[]{"1", null});
		aaicsvWriter.writeNext(null);
		aaicsvWriter.close();
		File file = new File(TEMP_DIR);
		Assert.assertTrue("File shoud be exists", file.exists());
	}
	
	@Test
	public void writeFile1() throws IOException
	{
		FileWriter fileWriter = new FileWriter(TEMP_DIR);
		AAICSVWriter aaicsvWriter = new AAICSVWriter(fileWriter, ",",  '\u0000', null);
		aaicsvWriter.writeNext(new String[]{"1", "Test1"}, true);
		aaicsvWriter.writeNext(new String[]{"1", "Tes\"t@"}, false);
		aaicsvWriter.writeNext(new String[]{"1", "Tes\t@"}, false);
		aaicsvWriter.writeNext(new String[]{"1", "Test,@"}, false);
		aaicsvWriter.writeNext(new String[]{"1", "Tes\n"}, false);
		aaicsvWriter.writeNext(new String[]{"1", "Tes\r"}, false);
		aaicsvWriter.writeNext(new String[]{"1", "Tes\u0000"}, false);
		aaicsvWriter.close();
		File file = new File(TEMP_DIR);
		Assert.assertTrue("File shoud be exists", file.exists());
		
	}
}
