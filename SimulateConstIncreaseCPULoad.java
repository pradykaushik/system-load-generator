import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SimulateConstIncreaseCPULoad {
	// Setting the default step size for increase in CPU load to be 10%.
	private static final double DEFAULT_STEP_SIZE = 0.1;
	// Setting default duration for the load to be 2sec.
	// This is because PCP data is retrieved every second and we want the PCP data to record the change in load.
	private static final int DEFAULT_DURATION = 2000;
	private static ProcessorArchInfo processorArchInfo = null;
	static {
		// Retrieving the processor architecture information
		try {
			processorArchInfo = getProcessorArchInformation();
		} catch (Exception e) {
			System.err.println("Oops! Something went wrong.");
			e.printStackTrace();
		}
		// Printing the processor architecture information
		System.err.println(processorArchInfo);
	}

	/**
	 * @param args Command line arguments. 'stepSize' specifying the % increase in CPU load for each load change, 
	 * 	'duration' specifying the time duration for which the particular CPU load needs to be maintained.
	 */
	public static void main(String[] args) throws Exception {
		// Checking whether the step size has been provided from the command line.
		// If not, then using the default step size (0.1)
		if (args.length == 0) {
			generateLoad(DEFAULT_STEP_SIZE, DEFAULT_DURATION);
		} else if (args.length == 1){
			double stepSize = Double.parseDouble(args[0]);
			generateLoad(stepSize, DEFAULT_DURATION);
		} else if (args.length == 2) {
			// The stepSize has to be given as decimals, indicating the load percentage.
			double stepSize = Double.parseDouble(args[0]);
			// The duration has to be given in milliseconds.
			int duration = Integer.parseInt(args[1]);
			generateLoad(stepSize, duration);
		}
	}

	// Constantly increase the CPU load in steps = stepSize.
	// The CPU load is going to be simulated for the specified duration.
	private static void generateLoad(double stepSize, int duration) {
		// Time for each a particular CPU load needs to be maintained for.
		long currentTime = System.currentTimeMillis();
		for (double load = stepSize; load <= 1.0; load += stepSize) {
			System.err.println("CPU load changing to " + load);
			Load.createLoad(processorArchInfo.getNumCores(), processorArchInfo.getNumThreadsPerCore(), load, duration);
			// Changing load only every <duration>/1000 seconds. If <duration>/1000 seconds has not elapsed, then we wait.
			try {
				Thread.sleep(duration);
			} catch (InterruptedException ie) {
				System.err.println((duration/1000) + " second wait has been interrupted! Next load change will happen sooner than you think.");
			}
		}
	}

	/**
	 * Determine the following information about the underlying processor architecture,
	 * 1. Number of cores
	 * 2. Number of threads per core
	 */
	private static ProcessorArchInfo getProcessorArchInformation() throws Exception {
		Process p = Runtime.getRuntime().exec("lscpu");
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = "";
		int numCores = 0;
		int numThreadsPerCore = 0;
		int numSockets = 0;
		int numCoresPerSocket = 0;
		while ((line = br.readLine()) != null) {
			if (line.contains("Socket(s):")) {
				// Retrieving the number of sockets
				String[] lineComponents = line.split("\\s+");
				numSockets = Integer.parseInt(lineComponents[lineComponents.length - 1]);
			} else if (line.contains("Core(s) per socket:")) {
				// Retrieving the number of cores per socket.
				// Total number of cores would be NumCoresPerSocket * NumSockets
				String[] lineComponents = line.split("\\s+");
				numCoresPerSocket = Integer.parseInt(lineComponents[lineComponents.length - 1]);
			} else if (line.contains("Thread(s) per core:")) {
				// Retrieving the number of threads per core.
				String[] lineComponents = line.split("\\s+");
				numThreadsPerCore = Integer.parseInt(lineComponents[lineComponents.length - 1]);
			}
		}
		// Determining the total number of cores.
		numCores = numCoresPerSocket * numSockets;
		return new ProcessorArchInfo(numCores, numThreadsPerCore);
	}	
}

class ProcessorArchInfo {
	private int numCores;
	private int numThreadsPerCore;

	public ProcessorArchInfo(int numCores, int numThreadsPerCore) {
		this.numCores = numCores;
		this.numThreadsPerCore = numThreadsPerCore;
	}

	public int getNumCores() {return this.numCores;}
	public int getNumThreadsPerCore() {return this.numThreadsPerCore;}

	public String toString() {
		StringBuilder sb = new StringBuilder("Processor Architecture Information\n--------------------------------\n");
		sb.append("Total number of cores (num of cores per socket * num of sockets) = ");
		sb.append(this.numCores);
		sb.append("\n");
		sb.append("Number of threads per core = ");
		sb.append(this.numThreadsPerCore);
		sb.append("\n");
		return sb.toString();
	}
}
