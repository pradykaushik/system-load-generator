# System Load Generator
Generate different kinds of system load.

## System Requirements
1. Java version 1.8+.
2. Gradle version 6.3.

### Compile
Run `gradle build` to compile the source code.

### Run
Run `gradle run` followed by command line arguments that you want to specify.<br>
The following command line arguments are supported.
1. `--help` - Display the help string.
2. `--load-type <type>` - Specify the type of load that you want to generate. The following are the supported types.
    - [cpuload](./src/main/java/loadgenerator/strategies/ConstIncreaseCPULoad.java) - CPU Load generator that
        constantly increases the CPU utilization of a machine from 1% to 100%. See [cpu-load-generator](#cpu-load-generator) for more information.
    - [loadaverage](./src/main/java/loadgenerator/strategies/ConstIncreaseLoadAverage.java) - 1min load average
        generate that constantly increases the load average. See [load-average-generator](#load-average-generator) for more information.
    - [cpuload_memorypressure](./src/main/java/loadgenerator/strategies/CPULoadGeneratorWithMemoryPressure.java) - 
        Generate CPU load with memory pressure. See [cpu-load-generator-with-memory-pressure](#cpu-load-generator-with-memory-pressure) for more information.

## Load Generators
### CPU Load Generator
CPU Load generator that constantly increase the CPU utilization of a machine from 1% to 100%.
Forked from [SriramKeerthi-Gist](https://gist.github.com/SriramKeerthi/0f1513a62b3b09fecaeb) and added functionality.

#### Configuration
The following are the configuration parameters that are provided in the YAML config file. The configuration file
is to be stored in [configs](./configs) directory.

* **stepSize** - TYPE: _decimal_ value in the range (0.0, 1.0). This specifies the increase in CPU load for every cycle.
* **duration** - TYPE: _number_. This specifies the number of milliseconds for which the CPU load needs to be maintained for, before being increased again.
* **isAlt** - TYPE: _boolean_. Consider a time slice to be 100ms. In the default state (when **isAlt** is *false*), CPU load is generated by making the current thread sleep for `(1 - load) * 100`ms. We could, however, generate the same CPU load with a different CPU usage pattern, such as an alternating one. When **isAlt** is *true*, CPU load is generated by making the current thread sleep multiple times, but for shorter durations, within the considered time slice (100ms). This leads to an alternating CPU usage pattern to create the same CPU load.
* **segments** - TYPE: _number_. This specifies the number of times the CPU usage alternates, when **isAlt** is set to true. Suppose, the time slice is 100ms, and **segments** = 2, then to generate a CPU load of 50%, the thread would sleep twice for 25ms, once every 50ms.

Below is a sample configuration for cpu load generator.
```yaml
stepSize: 0.2
duration: 3000
isAlt: false
segments: 2
```

### Load Average Generator

A 1-minute load average generator that constantly increases the load average for the past minute.

#### Configuration
The following are the configuration parameters that are provided in the YAML config file. The configuration file
is to be stored in [configs](./configs) directory.

* **startLoadAverageCore** - TYPE: _decimal_ (default = 1/numCores). This specifies the starting value of 1min load average for a given core. This value signifies the number of processes that would be executed in the first minute.
* **steSize** - TYPE: _decimal_ (default = 0.2). This specifies the increase in load average to be generated every minute.

Below is a sample configuration for load average generator.
```yaml
startLoadAverage: 0.25 # assuming quad-core machine.
stepSize: 0.2
```

### CPU Load Generator with Memory Pressure
Generate CPU load with Memory pressure.

#### Configuration
The following are the configuration parameters that are provided in the YAML config file. The configuration file
is to be stored in [configs](./configs) directory.

* **minCpuLoadPercentage** - Minimum CPU usage pressure.
* **maxCpuLoadPercentage** - Maximum CPU usage pressure.
* **ramUsageBytes** - RAM usage bytes.

Below is a sample configuration for cpu load with memory pressure generator.
```yaml
minCpuLoadPercentage: 0.2
maxCpuLoadPercentage: 0.6
ramUsageBytes: 2048
```

## Docker
System Load Generator can be run in a docker container using the below command.
```commandline
docker run -t pkaushi1/system-load-generator:v2 [-h | --load-type=LOAD_TYPE]
```
Note that this will use the default configuration (see [configs](./configs)) for each load generation strategy.

To use a different configuration, update the corresponding file in [configs](./configs) and then bindmount it to the 
_configs/_ directory within the container using the command given below.
```commandline
docker run -v $PWD/configs:/configs -t pkaushi1/system-load-generator:v2 [-h | --load-type=LOAD_TYPE]
```
