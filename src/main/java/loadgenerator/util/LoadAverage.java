/**
 * MIT License
 * <p>
 * Copyright (c) 2019 PRADYUMNA KAUSHIK
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package loadgenerator.util;

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
            // Run loop for specified duration.
            while (System.currentTimeMillis() < (currentTime + duration)) {
                double a = new Random().nextDouble();
                double b = new Random().nextDouble();
                double quot = a / b;
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
