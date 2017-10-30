/**
 * Generates Load on the CPU by keeping it busy for the given load percentage
 * @author Sriram
 */
public class Load {
    /**
     * Starts the Load Generation
     * @param args Command line arguments, ignored
     */
    public static void main(String[] args) {
        int numCore = 2;
        int numThreadsPerCore = 2;
        double load = 0.8;
        final long duration = 100000;
        for (int thread = 0; thread < numCore * numThreadsPerCore; thread++) {
            new BusyThread("Thread" + thread, load, duration).start();
        }
    }

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
     */
    public static void createLoad(int numCore, int numThreadsPerCore, double load, long duration) {
        for (int thread = 0; thread < numCore * numThreadsPerCore; thread++) {
            new BusyThread("Thread" + thread, load, duration).start();
        }
    }

    /**
     * Thread that actually generates the given load
     * @author Sriram
     */
    private static class BusyThread extends Thread {
        private double load;
        private long duration;

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
                while (System.currentTimeMillis() - startTime < duration) {
                    // Every 100ms, sleep for the percentage of unladen time
                    if (System.currentTimeMillis() % 100 == 0) {
                        Thread.sleep((long) Math.floor((1 - load) * 100));
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
