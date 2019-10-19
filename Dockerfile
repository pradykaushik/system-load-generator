# Generate CPU load for the given number of threads and cores.
# Generate Load Average.
# Load.java forked from https://gist.github.com/SriramKeerthi/0f1513a62b3b09fecaeb

FROM java:8

# Copying the source files into the Docker container's root directory.
COPY . /
# Compiling the source files for SimulateConstIncreaseCPULoad.
# RUN javac SimulateConstIncreaseCPULoad.java

# Compiling the source files for SimulateConstIncreaseLoadAverage.
# RUN javac SimulateConstIncreaseLoadAverage.java

# Compiling the source files for SimulateCPULoadWithMemoryPressure.
# RUN javac SimulateCPULoadWithMemoryPressure.java

# Generate CPU load with step size 1%.
# Please note that this is a default run command.
# Further command line arguments can be given.
# CMD java SimulateConstIncreaseCPULoad 0.01

# Generate CPU load and maintain the CPU load for the given duration (milliseconds).
# CMD java SimulateConstIncreaseCPULoad 0.01 4000

# Generate alternating CPU load, maintain it for a given duration. Using the default number of segments (2).
# CMD java SimulateConstIncreaseCPULoad 0.01 4000 true

# Generate alternating CPU load, maintain it for a given duration, and specify the number of segments.
# CMD java SimulateConstIncreaseCPULoad 0.01 4000 true 4

# Generate Load Average with LOAD_AVERAGE_LIMIT_CORE = 10.0 and STEP_SIZE = 0.2.
# CMD java SimulateConstIncreaseLoadAverage

# Generate CPU Load with Memory Pressure with CPU load in range [60,80]% and RAM usage as 4096 bytes.
# CMD java SimulateCPULoadWithMemoryPressure 60 80 4096
