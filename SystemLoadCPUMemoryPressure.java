//MIT License
//
//Copyright (c) 2019 PRADYUMNA KAUSHIK
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in all
//copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//SOFTWARE.

import java.util.Locale;
import java.util.Random;

/**
 * Generates CPU and memory pressure
 */
public class SystemLoad {

	public static void main(String[] args) {
		try {
			ProcessorArchInfo procArchInfo = ProcessorArch.getProcessorArchInformation();
			int numCore = procArchInfo.getNumCores();
			int numThreadsPerCore = procArchInfo.getNumThreadsPerCore();
			int minCpuLoadPercentage = Integer.parseInt(args[0]);
			int maxCpuLoadPercentage = Integer.parseInt(args[1]);
			int ramUsageBytes = Integer.parseInt(args[2]);

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
			System.out.println("java SystemLoad [min-cpu-usage-pressure] [max-cpu-usage-pressure] [memory-bytes-pressure]");
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
