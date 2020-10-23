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
import java.util.Locale;
import java.util.Random;

import loadgenerator.util.ProcessorArch;
import loadgenerator.entities.ProcessorArchInfo;

public class CPULoadGeneratorWithMemoryPressure implements LoadGenerationStrategyI {

    private final int minCpuLoadPercentage;
    private final int maxCpuLoadPercentage;
    private final int ramUsageBytes;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder {
        private int minCpuLoadPercentage;
        private int maxCpuLoadPercentage;
        private int ramUsageBytes;

        public Builder withConfig(String configFilePath) throws IOException {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Builder builder;
            try {
                builder = mapper.readValue(new File(configFilePath), Builder.class);
            } catch (IOException exception) {
                throw new IOException("failed to open cpu load with memory pressure generator config file", exception);
            }

            return builder;
        }

        public Builder() {
        }

        public void setMinCpuLoadPercentage(int minCpuLoadPercentage) {
            this.minCpuLoadPercentage = minCpuLoadPercentage;
        }
        public void setMaxCpuLoadPercentage(int maxCpuLoadPercentage) {
            this.maxCpuLoadPercentage = maxCpuLoadPercentage;
        }
        public void setRamUsageBytes(int ramUsageBytes) {
            this.ramUsageBytes = ramUsageBytes;
        }

        public CPULoadGeneratorWithMemoryPressure build() {
            return new CPULoadGeneratorWithMemoryPressure(minCpuLoadPercentage, maxCpuLoadPercentage, ramUsageBytes);
        }
    }

    private CPULoadGeneratorWithMemoryPressure(int minCpuLoadPercentage, int maxCpuLoadPercentage, int ramUsageBytes) {
        this.minCpuLoadPercentage = minCpuLoadPercentage;
        this.maxCpuLoadPercentage = maxCpuLoadPercentage;
        this.ramUsageBytes = ramUsageBytes;

    }

    @Override
    public void execute() {
        ProcessorArchInfo procArchInfo = null;
        try {
            procArchInfo = ProcessorArch.getProcessorArchInformation();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        int numCores = procArchInfo.getNumThreadsPerCore();
        int numThreadsPerCore = procArchInfo.getNumThreadsPerCore();
        int threadsToCreate = numCores * numThreadsPerCore;

        byte[] memory = new byte[ramUsageBytes / threadsToCreate];
        for (int thread = 0; thread < threadsToCreate; thread++) {
            new BusyThread("Thread-" + thread, minCpuLoadPercentage, maxCpuLoadPercentage, memory).start();
        }
    }

    /**
     * Thread that actually generates the given load
     * @author Sriram
     */
    private static class BusyThread extends Thread {
        private final Random random;
        private final int minCpuLoadPercentage;
        private final int maxCpuLoadPercentage;
        private final byte[] memory;

        public BusyThread(String name, int minCpuLoadPercentage, int maxCpuLoadPercentage, byte[] memory) {
            super(name);

            this.random = new Random(name.hashCode() + System.currentTimeMillis());
            this.minCpuLoadPercentage = minCpuLoadPercentage;
            this.maxCpuLoadPercentage = maxCpuLoadPercentage;
            this.memory = memory;
        }

        /**
         * Generates the load when run
         */
        @Override
        public void run() {

            final int cpuPressureRange = maxCpuLoadPercentage - minCpuLoadPercentage;

            try {
                System.out.println(String.format(Locale.US, "Thread %s starting with CPU pressure [%d-%d] and %d bytes",
                        getName(), minCpuLoadPercentage, maxCpuLoadPercentage, memory.length));
                Thread.sleep(cpuPressureRange);

                while (true) {
                    final int duration = minCpuLoadPercentage + random.nextInt(cpuPressureRange);
                    final long startTime = System.currentTimeMillis();
                    long currentDuration = (System.currentTimeMillis() - startTime);
                    while (currentDuration < duration) {
                        final int cellToAccess = random.nextInt(memory.length);
                        final byte previousValue = memory[cellToAccess];
                        memory[cellToAccess] = (byte) (previousValue + 1);

                        currentDuration = (System.currentTimeMillis() - startTime);
                    }
                    if (currentDuration > 100) {
                        currentDuration = 100;
                    }
                    Thread.sleep(100 - currentDuration);
                }
            } catch (InterruptedException e) {
                System.out.println(String.format(Locale.US, "Thread %s failed with %s", getName(), e.getMessage()));
                e.printStackTrace();
            }
        }
    }

}
