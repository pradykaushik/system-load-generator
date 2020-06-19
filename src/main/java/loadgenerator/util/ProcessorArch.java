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

import loadgenerator.entities.ProcessorArchInfo;

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
