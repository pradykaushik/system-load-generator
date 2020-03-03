package loadGenerator.driver;

import org.apache.commons.cli.Options;

public final class CLIBuilder {
	private static Options options = new Options();

	static {
		// Defining command line options.
		options.addOption("lg", "load-generator", true, "Name of the load generator");
		options.addOption("c", "load-generator-config", true, "JSON config for load generator");
		options.addOption("h", "help", false, "Usage information");
	}

	private CLIBuilder() { }

	/**
	* Retrieve the command line options.
	* @return command line options.
	*/
	public static Options getOptions() {
		return options;
	}
}
