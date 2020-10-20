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
package loadgenerator.strategies;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

import loadgenerator.util.LoadAverage;
import loadgenerator.util.ProcessorArch;
import loadgenerator.entities.ProcessorArchInfo;

import java.util.concurrent.ExecutionException;

public class ConstIncreaseLoadAverage implements LoadGenerationStrategyI {
    // Default value for 1min load average on each core.
    // This means that we would start with (START_LOAD_AVERAGE_CORE * numCores) number of threads to be always running.
    private static final double START_LOAD_AVERAGE_CORE;

    // Setting default step size for load average to be 20%.
    // This means that every minute, the number of running threads would be increased by (0.2 * numCores).
    // For example, on a 10 core machine,
    // 	There would be 0 thread running in the first minute.
    // 	There would be 2 threads running in parallel in the second minute.
    // 	There would be 4 threads running in parallel in the third minut and so on.
    private static final double STEP_SIZE = 0.2;

    // Setting default final value for 1min load average for each core to be 10 (total 1min load_avg = 10 * numCores).
    // This means that load would be increased until there are 10 processes either running or waiting to be run on
    // each core for the minute.
    private static final double LOAD_AVERAGE_LIMIT_CORE = 10.0;

    // Information regarding the number of cores and the number of virtual CPUs available.
    private static ProcessorArchInfo procArchInfo = null;

    // Non-Defaults.
    private final double startLoadAvgCore;
    private final double stepSize;

    static {
        // Retrieving the processor architecture information.
        try {
            procArchInfo = ProcessorArch.getProcessorArchInformation();
        } catch (Exception e) {
            System.err.println("failed to obtain processor architecture information.");
            e.printStackTrace();
        }
        // Printing the processor architecture information.
        System.err.println(procArchInfo);

        // Initializing default value for START_LOAD_AVERAGE_CORE.
        START_LOAD_AVERAGE_CORE = 1.0 / procArchInfo.getNumCores();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder {
        private double startLoadAvgCore = START_LOAD_AVERAGE_CORE;
        private double stepSize = STEP_SIZE;

        public Builder withConfig(String configFilePath) throws IOException {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Builder builder;
            try {
                builder = mapper.readValue(new File(configFilePath), Builder.class);
            } catch (IOException exception) {
                throw new IOException("failed to open load average generator config file", exception);
            }

            return builder;
        }

        public Builder() {
        }

        public void setStepSize(double stepSize) { this.stepSize = stepSize; }
        public void setStartLoadAvgCore(double startLoadAvgCore) { this.startLoadAvgCore = startLoadAvgCore; }

        public ConstIncreaseLoadAverage build() {
            return new ConstIncreaseLoadAverage(startLoadAvgCore, stepSize);
        }
    }

    private ConstIncreaseLoadAverage(double startLoadAvgCore, double stepSize) {
        this.startLoadAvgCore = startLoadAvgCore;
        this.stepSize = stepSize;
    }

    @Override
    public void execute() {
        long duration = 60000; // 1min.
        long currentTime = System.currentTimeMillis();
        double currentLoadAvgCore = startLoadAvgCore;
        while (currentLoadAvgCore <= LOAD_AVERAGE_LIMIT_CORE) {
            int totalNumCores = procArchInfo.getNumCores();
            int numThreadsToCreate = (int) Math.floor((currentLoadAvgCore * totalNumCores) + 0.5);
            System.out.println("Creating load...");
            System.out.println("load average for next minute = " + numThreadsToCreate);
            System.out.println();

            try {
                // Only if positive load.
                if (currentLoadAvgCore != 0) {
                    LoadAverage.createLoad(numThreadsToCreate, duration);
                }
                // Thread.sleep(duration);
            } catch (InterruptedException | ExecutionException ie) {
                System.err.println((duration / 1000) + "second wait bas been interrupted!"
                        + "Next load change will happen sooner than you think.");
            }
            currentLoadAvgCore += stepSize;
        }
    }
}
