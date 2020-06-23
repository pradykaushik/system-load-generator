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
package loadgenerator.strategies.factory;

import java.io.IOException;
import java.util.Objects;

import loadgenerator.strategies.LoadGenerationStrategyI;
import loadgenerator.strategies.ConstIncreaseCPULoad;
import loadgenerator.strategies.ConstIncreaseLoadAverage;
import loadgenerator.strategies.CPULoadGeneratorWithMemoryPressure;

public final class SimpleStrategyFactory implements SimpleStrategyFactoryI {

    private enum LoadType {
        CPU_LOAD("cpuload", "cpuload_config.yaml"),
        LOAD_AVERAGE("loadaverage", "loadaverage_config.yaml"),
        CPU_LOAD_WITH_MEMORY_PRESSURE("cpuload_memorypressure",
                "cpuload_memorypressure_config.yaml");

        private final String name;
        private final String configFilename;

        LoadType(final String s, final String configFilename) {
            name = s;
            this.configFilename = configFilename;
        }

        String getName() {
            return name;
        }

        String getConfigFilename() {
            return configFilename;
        }

        static boolean isValid(final String s) {
            for (LoadType loadType : values()) {
                if (loadType.getName().equals(s)) {
                    return true;
                }
            }
            return false;
        }

        static LoadType forName(final String s) {
            for (LoadType loadType : values()) {
                if (loadType.getName().equals(s)) {
                    return loadType;
                }
            }
            return null;
        }
    }

    private static final String PROJECT_ROOT = System.getProperty("user.dir");
    private static final String CONFIGS_DIRECTORY = "configs";

    @Override
    public LoadGenerationStrategyI getLoadGenerationStrategy(final String loadType)
            throws UnsupportedLoadTypeException, IOException {

        if (!LoadType.isValid(loadType)) {
            throw new UnsupportedLoadTypeException("failed to create load generation strategy: "
                    + "unsupported load type");
        }

        switch (Objects.requireNonNull(LoadType.forName(loadType))) {
            case CPU_LOAD:
                return new ConstIncreaseCPULoad.Builder()
                        .withConfig(PROJECT_ROOT
                                + "/"
                                + CONFIGS_DIRECTORY
                                + "/"
                                + LoadType.CPU_LOAD.getConfigFilename())
                        .build();
            case LOAD_AVERAGE:
                return new ConstIncreaseLoadAverage.Builder()
                        .withConfig(PROJECT_ROOT
                                + "/"
                                + CONFIGS_DIRECTORY
                                + "/"
                                + LoadType.LOAD_AVERAGE.getConfigFilename())
                        .build();
            case CPU_LOAD_WITH_MEMORY_PRESSURE:
                return new CPULoadGeneratorWithMemoryPressure.Builder()
                        .withConfig(PROJECT_ROOT
                                + "/"
                                + CONFIGS_DIRECTORY
                                + "/"
                                + LoadType.CPU_LOAD_WITH_MEMORY_PRESSURE.getConfigFilename())
                        .build();
            default:
                return null;
        }
    }
}
