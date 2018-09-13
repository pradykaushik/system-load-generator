import java.util.concurrent.ExecutionException;

public class SimulateConstIncreaseLoadAverage {
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
	// This means that load would be increased until there are 10 processes waiting to be run on each core for the minute.
	private static final double LOAD_AVERAGE_LIMIT_CORE = 10.0;

	// Information regarding the number of cores and the number of virtual CPUs available.
	private static ProcessorArchInfo procArchInfo = null;
	static {
		// Retrieving the processor architecture information.
		try {
			procArchInfo = ProcessorArch.getProcessorArchInformation();
		} catch (Exception e) {
			System.err.println("Oops! Something went wrong.");
			e.printStackTrace();
		}
		// Printing the processor architecture information.
		System.err.println(procArchInfo);

		// Initializing default value for START_LOAD_AVERAGE_CORE.
		START_LOAD_AVERAGE_CORE = 1.0 / procArchInfo.getNumCores();
	}

	/**
	 * @param args Command line arguments. 
	 * 	'startLoadAvgCore' specifying the starting value for 1min load average.
	 * 	'stepSize' specifying the % increase in 1min load average that is to be created.
	 */
	public static void main(String[] args) {
		double startLoadAvgCore = START_LOAD_AVERAGE_CORE;
		double stepSize = STEP_SIZE;
		switch (args.length) {
			case 1:
				// Using provided value for start load average.
				startLoadAvgCore = Double.parseDouble(args[0]);
				break;
			case 2:
				// Using provided values for start load average and step size.
				startLoadAvgCore = Double.parseDouble(args[0]);
				stepSize = Double.parseDouble(args[1]);
				break;
		}
		
		generateLoadAverage(startLoadAvgCore, stepSize);

	}

	// Increases 1min load average in a step wise manner till the given limit.
	// To create a 1min load average of X, X threads need to be running for a minute.
	// However, to create a 1min load average of (X*100)%, (X * numCores) number of threads need to be running for a minute.
	private static void generateLoadAverage(double startLoadAvgCore, double stepSize) {
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
				System.err.println((duration/1000) + "second wait bas been interrupted!"
						+ "Next load change will happen sooner than you think.");
			}
			currentLoadAvgCore += stepSize;
		}
	}
}
