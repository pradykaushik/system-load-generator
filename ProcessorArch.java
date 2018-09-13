import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProcessorArch {

	/**
	 * Determine the following information about the underlying processor architecture,
	 * 1. Number of cores
	 * 2. Number of threads per core
	 */
	public static ProcessorArchInfo getProcessorArchInformation() throws Exception {
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
