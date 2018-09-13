import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class LoadAverage {
	
	/**
	 * Creates load for the given amount of time.
	 * @param numThreadsToCreate Number of threads to create so as to generate the required 1min load average.
	 * @param duration Amount of time to maintain the load.
	 */
	public static void createLoad(int numThreadsToCreate, long duration) throws InterruptedException, ExecutionException {

		// Defining what each thread is going to execute.
		Callable<String> task = () -> {
			long currentTime = System.currentTimeMillis();
			long endTime;
			// Run loop for specified duration.
			while ((endTime = System.currentTimeMillis()) < (currentTime + duration)) {
				double a = new Random().nextDouble();
				double b = new Random().nextDouble();
				double quot = a/b;
			}
			// Time is up!
			return "Task execution complete :)";
		};

		// Creating a list of callable tasks.
		List<Callable<String>> callableTasks = new ArrayList<>();
		for (int i = 0; i < numThreadsToCreate; i++) {
			callableTasks.add(task);
		}

		// Just rounding the potential number of threads to be created.
		ExecutorService taskExecutorService = Executors.newFixedThreadPool(numThreadsToCreate);
		// Execute all tasks.
		List<Future<String>> results = taskExecutorService.invokeAll(callableTasks);

		// Notifying the executor service that it is okay to shutdown once all tasks complete execution.
		taskExecutorService.shutdown();

		for (Future<String> result : results) {
			System.out.println(result.get());
		}

	}
}
