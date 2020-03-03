/**
* MIT License
* 
* Copyright (c) 2019 PRADYUMNA KAUSHIK
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/
package loadGenerator.driver;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

public class Driver {
	public static void main(String[] args) {
		CommandLineParser commandLineParser = new DefaultParser();
		CommandLine commandLine;
		// Parsing command line arguments.
		if (0 == args.length) {
			throw new UsageException();
		} else {
			try {
				commandLine = commandLineParser.parse(CLIBuilder.getOptions(), args);
			} catch (ParseException e) {
				e.printStackTrace();
				throw new UsageException(e.getMessage());
			}
		}
		// Printing possible command line options and exiting.
        if (commandLine.hasOption("help")) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("java -jar build/libs/system-load-generator-1.0-SNAPSHOT.jar [-h]"
                    + " --load-generator LOAD_GENERATOR_NAME --load-generator-config FILE ", CLIBuilder.getOptions());
            System.exit(0);
        }

		String loadGenConfigFilename = commandLine.getOptionValue("load-generator-config");
		String loadGen = commandLine.getOptionValue("load-generator");

		System.out.println(loadGen);
		System.out.println(loadGenConfigFilename);
	}
}
