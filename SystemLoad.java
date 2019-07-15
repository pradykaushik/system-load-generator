import java.util.Locale;
import java.util.Random;

/**
 * Generates CPU and memory pressure
 */
public class SystemLoad {
	
	public static void main(String[] args) {
		try {
			int numCore = Integer.parseInt(args[0]);
			int numThreadsPerCore = Integer.parseInt(args[1]);
			int minCpuLoadPercentage = Integer.parseInt(args[2]);
			int maxCpuLoadPercentage = Integer.parseInt(args[3]);
			int ramUsageBytes = Integer.parseInt(args[4]);
			
			int threadsToCreate = numCore * numThreadsPerCore;
			for (int thread = 0; thread < threadsToCreate; thread++) {
				byte[] memory = new byte[ramUsageBytes / threadsToCreate];
				new BusyThread("Thread-" + thread, minCpuLoadPercentage, maxCpuLoadPercentage, memory).start();
			}
		} catch (Exception e) {
			System.out.println("Failed to start!" + e.getMessage());
			e.printStackTrace();
			System.out.println("Usage: ");
			System.out.println("javac SystemLoad.java");
			System.out.println("java SystemLoad [num-of-cores] [num-of-threads-per-core] [min-cpu-usage-pressure] [max-cpu-usage-pressure] [memory-bytes-pressure]");
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
				System.out.println(String.format(Locale.US, "Thread %s starting with CPU pressure [%d-%d] and %d bytes", getName(), minCpuLoadPercentage, maxCpuLoadPercentage, memory.length));
				Thread.sleep(cpuPressureRange);

				while(true) {
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
