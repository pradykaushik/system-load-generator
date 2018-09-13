public class ProcessorArchInfo {
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
