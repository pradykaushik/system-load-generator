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

import loadgenerator.util.CPULoad;
import loadgenerator.util.ProcessorArch;
import loadgenerator.entities.ProcessorArchInfo;

public class ConstIncreaseCPULoad implements LoadGenerationStrategyI {
    // Setting the default step size for increase in CPU load to be 10%.
    private static final double DEFAULT_STEP_SIZE = 0.1;
    // Setting default duration for the load to be 4sec.
    private static final int DEFAULT_DURATION = 4000;
    // Default number of segments for creating alternating CPU load.
    private static final int DEFAULT_ALT_SEGMENTS = 2;
    private static ProcessorArchInfo processorArchInfo = null;

    // Non-defaults.
    private double stepSize = DEFAULT_STEP_SIZE;
    private int duration = DEFAULT_DURATION;
    private boolean isAlt = false;
    private int segments = DEFAULT_ALT_SEGMENTS;

    static {
        // Retrieving the processor architecture information
        try {
            processorArchInfo = ProcessorArch.getProcessorArchInformation();
        } catch (Exception e) {
            System.err.println("failed to obtain processor architecture information.");
            e.printStackTrace();
        }
        // Printing the processor architecture information
        System.err.println(processorArchInfo);
    }

    public static class Builder {
        private double stepSize = DEFAULT_STEP_SIZE;
        private int duration = DEFAULT_DURATION;
        private boolean isAlt = false;

        private int segments = DEFAULT_ALT_SEGMENTS;

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

        public Builder() {
        }

        public void setStepSize(double stepSize) {
            this.stepSize = stepSize;
        }
        public void setDuration(int duration) {
            this.duration = duration;
        }
        public void setIsAlt(boolean isAlt) {
            this.isAlt = isAlt;
        }
        public void setSegments(int segments) {
            this.segments = segments;
        }

        public ConstIncreaseCPULoad build() {
            return new ConstIncreaseCPULoad(stepSize, duration, isAlt, segments);
        }

    }

    private ConstIncreaseCPULoad(double stepSize, int duration, boolean isAlt, int segments) {
        this.stepSize = stepSize;
        this.duration = duration;
        this.isAlt = isAlt;
        this.segments = segments;
    }

    // Constantly increase the CPU load in steps = stepSize.
    // The CPU load is going to be simulated for the specified duration.
    @Override
    public void execute() {
        // Time for each a particular CPU load needs to be maintained for.
        long currentTime = System.currentTimeMillis();
        for (double load = stepSize; load <= 1.0; load += stepSize) {
            System.err.println("CPU load changing to " + load);
            CPULoad.createLoad(processorArchInfo.getNumCores(),
                    processorArchInfo.getNumThreadsPerCore(),
                    load,
                    duration,
                    isAlt,
                    segments);
            // Changing load only every <duration>/1000 seconds. If <duration>/1000 seconds has not elapsed, then we wait.
            try {
                Thread.sleep(duration);
            } catch (InterruptedException ie) {
                System.err.println(String.format("%d second wait has been interrupted! Next load change will happen sooner than you think.", (duration / 1000)));
            }
        }
    }
}
