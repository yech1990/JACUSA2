package lib.cli.options;

import java.io.File;
import java.io.FileNotFoundException;

import lib.cli.parameter.AbstractParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class BedCoordinatesOption extends AbstractACOption {

	final private AbstractParameter parameters;
	
	public BedCoordinatesOption(AbstractParameter parameters) {
		super("b", "bed");
		this.parameters = parameters;
	}

	@Override
	public Option getOption(boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase()) 
				.hasArg(true)
				.desc(getLongOpt().toUpperCase() + " file to scan for variants")
				.build();
	}

	@Override
	public void process(final CommandLine line) throws FileNotFoundException {
    	final String pathname = line.getOptionValue(getOpt());
    	final File file = new File(pathname);
    	if(! file.exists()) {
    		throw new FileNotFoundException("BED file (" + pathname + ") in not accessible!");
    	}
		parameters.setInputBedFilename(pathname);
	}

}