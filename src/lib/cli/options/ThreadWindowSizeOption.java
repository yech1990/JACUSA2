package lib.cli.options;

import lib.cli.parameter.GeneralParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class ThreadWindowSizeOption extends AbstractACOption {

	public final static int NO_WINDOWS = -1;
	public final static int MIN_WINDOWS = 100;
	
	final private GeneralParameter parameter; 
	
	public ThreadWindowSizeOption(GeneralParameter parameter) {
		super("W", "thread-window-size");
		this.parameter = parameter;
	}

	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("size of the window used per thread\n" + 
						"default: " + parameter.getReservedWindowSize())
				.build();
	}

	/**
	 * Tested int @see test.lib.cli.options.ThreadWindowSizeOptionTest
	 */
	@Override
	public void process(final CommandLine line) throws Exception {
    	final String value = line.getOptionValue(getOpt());
    	final int windowSize = Integer.parseInt(value);
    	if (windowSize != NO_WINDOWS && windowSize < MIN_WINDOWS) {
    		throw new IllegalArgumentException("THREAD-WINDOW-SIZE must be >= " + MIN_WINDOWS);
    	}

    	parameter.setReservedWindowSize(windowSize);
	}

}
