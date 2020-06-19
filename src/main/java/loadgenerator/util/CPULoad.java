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
package loadgenerator.util;

/**
 * Generates Load on the CPU by keeping it busy for the given load percentage
 * @author Sriram
 * @author Pradyumna Kaushik
 */
public class CPULoad {
	/**
	 * Function to be called by a wrapper
	 * A wrapper could be a program that determines the requirements such as,
	 * 	Number of cores
	 * 	Number of threads per core, etc
	 * The wrapper would then pass those values so as to be able to create the requried amount of load.
	 * @param numCore Number of cores
	 * @param numThreadsPerCore Number of threads per core
	 * @param load % CPU load to generate
	 * @param duration Duration for which this load has to be maintained
	 * @param isAlt Whether we need to create an alternating load. An alternating load is one where we
	 * 	alternate between running and sleeping, within the load duration, to generate the required CPU load.
	 * @param segments Number of alternating segments, for an alternating CPU load, for the specified duration. 
	 */
	public static void createLoad(int numCore, int numThreadsPerCore, double load, 
			long duration, boolean isAlt, int segments) {
		if (isAlt) {
			for (int thread = 0; thread < numCore * numThreadsPerCore; thread++) {
				new AltBusyThread("Thread" + thread, load, duration, segments).start();
			}
		} else {
			for (int thread = 0; thread < numCore * numThreadsPerCore; thread++) {
				new BusyThread("Thread" + thread, load, duration).start();
			}
		}
	}

	/**
	 * Thread that actually generates the given load
	 * @author Sriram
	 */
	private static class BusyThread extends Thread {
		private final double load;
		private final long duration;

		/**
		 * Constructor which creates the thread
		 * @param name Name of this thread
		 * @param load Load % that this thread should generate
		 * @param duration Duration that this thread should generate the load for
		 */
		public BusyThread(String name, double load, long duration) {
			super(name);
			this.load = load;
			this.duration = duration;
		}

		/**
		 * Generates the load when run
		 */
		@Override
		public void run() {
			long startTime = System.currentTimeMillis();
			try {
				// Loop for the given duration
				long currentTime = System.currentTimeMillis();
				while ((currentTime - startTime) < duration) {
					// Every 100ms, sleep for the percentage of unladen time
					if ((currentTime % 100) == 0) {
						Thread.sleep((long) Math.floor((1 - load) * 100));
					}
					currentTime = System.currentTimeMillis();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Thread that generates the given load by alternating between running state and sleep state
	 * @author Pradyumna Kaushik
	 */
	private static class AltBusyThread extends Thread {
		private final double load;
		private final long duration;
		// Number of times the thread needs to switch to sleep state
		// This also represents the number of pairs of Busy:Sleep for this thread.
		private final int segments;

		/**
		 * Constructor which creates the thread
		 * @param name Name of this thread
		 * @param load Load % that this thread should generate
		 * @param duration Duration that this thread should generate the load for
		 */
		public AltBusyThread(String name, double load, long duration, int segments) {
			super(name);
			this.load = load;
			this.duration = duration;
			this.segments = segments;
		}

		/**
		 * Generates the load when run
		 */
		@Override
		public void run() {
			long startTime = System.currentTimeMillis();
			try {
				// Loop for the given duration.
				long currentTime = System.currentTimeMillis();
				while ((currentTime - startTime) < duration) {
					// We create alternating CPU utilizations in every 100ms.
					// The number of segments would determine the granularity.
					// Note that this approach would work well when the number of segments divides 100.
					long segmentInterval = (long) Math.floor(100.0 / this.segments);
					if ((currentTime % segmentInterval) == 0) {
						// Sleep for (segmentInterval)*(1 - load) milliseconds.
						long sleepFor = (long) Math.floor(segmentInterval * (1 - this.load));
						Thread.sleep(sleepFor);
					}
					currentTime = System.currentTimeMillis();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
