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
/**
 * 
 */
package org.onap.aai.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import com.opencsv.CSVWriter;

/**
 * had to overwrite the separate character to separate string
 * Based on the public - A very simple CSV writer released under a commercial-friendly license.
 *
 
 */
public class AAICSVWriter extends CSVWriter {

	private String separatorStr;
	private char overridequotechar;
	private String overridelineEnd;
	private Writer rawWriter;
	private PrintWriter pw;
	   
	/**
	 * Instantiates a new AAICSV writer.
	 *
	 * @param writer the writer
	 */
	public AAICSVWriter(Writer writer) {
		super(writer);
		// TODO Auto-generated constructor stub
	}

	 /**
 	 * Constructs AAICSVWriter with supplied separator string and quote char.
 	 *
 	 * @param writer    the writer to an underlying CSV source.
 	 * @param overrideseparator the overrideseparator
 	 * @param quotechar the character to use for quoted elements
 	 * @param lineEnd   the line feed terminator to use
 	 */
	   public AAICSVWriter(Writer writer, String overrideseparator, char quotechar, String lineEnd) {
	      super(writer, CSVWriter.DEFAULT_SEPARATOR, quotechar, DEFAULT_ESCAPE_CHARACTER, lineEnd);
	      separatorStr = overrideseparator;
	      overridequotechar = quotechar;
	      overridelineEnd = lineEnd;
	      this.rawWriter = writer;
	      this.pw = new PrintWriter(writer);
	   }
	   
	   /**
   	 * String contains special characters.
   	 *
   	 * @param line the line
   	 * @return true, if successful
   	 */
   	private boolean stringContainsSpecialCharacters(String line) {
		      return line.indexOf(overridequotechar) != -1 || line.indexOf(DEFAULT_ESCAPE_CHARACTER) != -1 || line.indexOf(separatorStr) != -1 || line.contains("\n") || line.contains("\r");
		   }
	   
	   /**
	    * Close the underlying stream writer flushing any buffered content.
	    *
	    * @throws IOException if bad things happen
	    */
	   public void close() throws IOException {
	      flush();
	      pw.close();
	      rawWriter.close();
	   }
	   
	/**
	    * Writes the next line to the file.
	    *
	    * @param nextLine         a string array with each comma-separated element as a separate
	    *                         entry.
	    * @param applyQuotesToAll true if all values are to be quoted.  false applies quotes only
	    *                         to values which contain the separator, escape, quote or new line characters.
	    */
	   public void writeNext(String[] nextLine, boolean applyQuotesToAll) {

	      if (nextLine == null)
	         return;

	      StringBuilder sb = new StringBuilder(INITIAL_STRING_SIZE);
	      for (int i = 0; i < nextLine.length; i++) {

	         if (i != 0) {
	            sb.append(separatorStr);
	         }

	         String nextElement = nextLine[i];

	         if (nextElement == null)
	            continue;

	         Boolean stringContainsSpecialCharacters = stringContainsSpecialCharacters(nextElement);

	         if ((applyQuotesToAll || stringContainsSpecialCharacters) && overridequotechar != NO_QUOTE_CHARACTER)
	            sb.append(overridequotechar);

	         if (stringContainsSpecialCharacters) {
	            sb.append(processLine(nextElement));
	         } else {
	            sb.append(nextElement);
	         }

	         if ((applyQuotesToAll || stringContainsSpecialCharacters) && overridequotechar != NO_QUOTE_CHARACTER)
	            sb.append(overridequotechar);
	      }

	      sb.append(overridelineEnd);
	      pw.write(sb.toString());
	   }

	   
	   /**
   	 * Writes the next line to the file ignoring all exceptions.
   	 *
   	 * @param nextLine         a string array with each comma-separated element as a separate
   	 *                         entry.
   	 */
	   public void writeColumn(String[] nextLine) {

	      if (nextLine == null)
	         return;

	      StringBuilder sb = new StringBuilder(INITIAL_STRING_SIZE);
	      for (int i = 0; i < nextLine.length; i++) {


	         String nextElement = nextLine[i];

	         if (nextElement == null)
	            continue;

             sb.append(nextElement);
	         

	      }

	      sb.append(overridelineEnd);
	      pw.write(sb.toString());
	   }
}
