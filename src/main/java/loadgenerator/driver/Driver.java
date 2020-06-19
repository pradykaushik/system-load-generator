/**
 * MIT License
 * <p>
 * Copyright (c) 2019 PRADYUMNA KAUSHIK
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package loadgenerator.driver;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

import loadgenerator.strategies.factory.SimpleStrategyFactoryI;
import loadgenerator.strategies.factory.SimpleStrategyFactory;
import loadgenerator.strategies.factory.UnsupportedLoadTypeException;
import loadgenerator.strategies.LoadGenerationStrategyI;

public class Driver {
    public static void main(final String[] args) {
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
            helpFormatter.printHelp("java -jar build/libs/system-load-generator-<version>.jar [-h]"
                    + " --load-type LOAD_TYPE", CLIBuilder
                    .getOptions());
            System.exit(0);
        }

        String loadType = commandLine.getOptionValue("load-type");
        SimpleStrategyFactoryI factory = new SimpleStrategyFactory();
        LoadGenerationStrategyI loadGenerationStrategy;
        try {
            loadGenerationStrategy = factory.getLoadGenerationStrategy(loadType);
            if (null != loadGenerationStrategy) {
                loadGenerationStrategy.execute();
            }
        } catch (UnsupportedLoadTypeException | IOException exception) {
            exception.printStackTrace();
        }

        System.out.println("DONE!");
    }
}
