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
package loadgenerator.strategies.factory;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import loadgenerator.strategies.LoadGenerationStrategyI;
import loadgenerator.strategies.ConstIncreaseCPULoad;
import loadgenerator.strategies.ConstIncreaseLoadAverage;
import loadgenerator.strategies.CPULoadGeneratorWithMemoryPressure;

public class SimpleStrategyFactory implements SimpleStrategyFactoryI {

    private static enum LoadType {
        CPU_LOAD("cpuload", "cpuload_config.yaml"),
        LOAD_AVERAGE("loadaverage", "loadaverage_config.yaml"),
        CPU_LOAD_WITH_MEMORY_PRESSURE("cpuload_memorypressure",
                "cpuload_memorypressure_config.yaml");

        private String name;
        private final String configFilename;

        LoadType(String s, String configFilename) {
            name = s;
            this.configFilename = configFilename;
        }

        String getName() {
            return name;
        }

        String getConfigFilename() {
            return configFilename;
        }

        static boolean isValid(String s) {
            for (LoadType loadType: values()) {
                if (loadType.getName().equals(s)) {
                    return true;
                }
            }
            return false;
        }

        static LoadType forName(String s) {
            for (LoadType loadType: values()) {
                if (loadType.getName().equals(s)) {
                    return loadType;
                }
            }
            return null;
        }
    }

    private static final String PROJECT_ROOT = System.getProperty("user.dir");
    private static Map<LoadType, LoadGenerationStrategyI> loadGenerationStrategies
            = new HashMap<>();

    @Override
    public LoadGenerationStrategyI getLoadGenerationStrategy(String loadType)
            throws UnsupportedLoadTypeException, IOException {

        if (!LoadType.isValid(loadType)) {
            throw new UnsupportedLoadTypeException("failed to create load generation strategy: " +
                    "unsupported load type");
        }

        switch (LoadType.forName(loadType)) {
            case CPU_LOAD:
                return new ConstIncreaseCPULoad.Builder()
                        .withConfig(PROJECT_ROOT + "/" + LoadType.CPU_LOAD.getConfigFilename())
                        .build();
            case LOAD_AVERAGE:
                return new ConstIncreaseLoadAverage.Builder()
                        .withConfig(PROJECT_ROOT + "/" + LoadType.LOAD_AVERAGE.getConfigFilename())
                        .build();
            case CPU_LOAD_WITH_MEMORY_PRESSURE:
                return new CPULoadGeneratorWithMemoryPressure.Builder()
                        .withConfig(PROJECT_ROOT + "/" + LoadType.CPU_LOAD_WITH_MEMORY_PRESSURE.getConfigFilename())
                        .build();
            default:
                return null;
        }
    }
}
