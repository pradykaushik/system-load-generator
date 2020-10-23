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
import loadgenerator.entities.ProcessorArchInfo;
import loadgenerator.util.CPULoad;
import loadgenerator.util.ProcessorArch;

import java.io.File;
import java.io.IOException;

public class ConstantCPULoad implements LoadGenerationStrategyI {
    // Setting default cpu load to be 50%.
    private static final double DEFAULT_CPU_LOAD = 0.5;
    // Setting default duration for the load to be 60sec.
    private static final int DEFAULT_DURATION = 60000;
    private static ProcessorArchInfo processorArchInfo = null;

    // Non-defaults.
    private double cpuLoad = DEFAULT_CPU_LOAD;
    private int duration = DEFAULT_DURATION;

    static {
        // Retrieving the processor architecture information
        try {
            processorArchInfo = ProcessorArch.getProcessorArchInformation();
        } catch (Exception e) {
            System.err.println("failed to obtain processor architecture information.");
            e.printStackTrace();
        }
        // Printing the processor architecture information
        System.out.println(processorArchInfo);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder {
        private double cpuLoad = DEFAULT_CPU_LOAD;
        private int duration = DEFAULT_DURATION;

        public Builder withConfig(String configFilePath) throws IOException {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Builder builder;
            try {
                builder = mapper.readValue(new File(configFilePath), Builder.class);
            } catch (IOException exception) {
                throw new IOException("failed to open load generator config file", exception);
            }

            return builder;
        }

        public Builder() { }

        public void setCpuLoad(double cpuLoad) {
            this.cpuLoad = cpuLoad;
        }
        public void setDuration(int duration) {
            this.duration = duration;
        }

        public ConstantCPULoad build() {
            return new ConstantCPULoad(cpuLoad, duration);
        }
    }

    private ConstantCPULoad(double cpuLoad, int duration) {
        this.cpuLoad = cpuLoad;
        this.duration = duration;
    }

    // Generate constant CPU Load of the configured amount and maintain that
    // load for the configured duration.
    @Override
    public void execute() {
        System.out.println(String.format("Generating %f %% CPU load for %d seconds",
                cpuLoad, duration));
        CPULoad.createLoad(processorArchInfo.getNumCores(),
                processorArchInfo.getNumThreadsPerCore(),
                cpuLoad,
                duration,
                false,
                0);
        System.out.println("Done generating CPU load!");
    }
}
