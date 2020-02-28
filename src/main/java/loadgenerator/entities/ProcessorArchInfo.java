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
package loadgenerator.entities;

public class ProcessorArchInfo {
    private final int numCores;
    private final int numThreadsPerCore;

    public ProcessorArchInfo(int numCores, int numThreadsPerCore) {
        this.numCores = numCores;
        this.numThreadsPerCore = numThreadsPerCore;
    }

    public int getNumCores() {
        return this.numCores;
    }

    public int getNumThreadsPerCore() {
        return this.numThreadsPerCore;
    }

    public String toString() {
        return "Processor Architecture Information\n--------------------------------\n" + "Total number of cores (num of cores per socket * num of sockets) = " +
                this.numCores +
                "\n" +
                "Number of threads per core = " +
                this.numThreadsPerCore +
                "\n";
    }
}
