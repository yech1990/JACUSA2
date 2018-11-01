package lib.cli.options;

import lib.cli.parameter.AbstractParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class ShowReferenceOption extends AbstractACOption {

	final private AbstractParameter parameters;

	public ShowReferenceOption(final AbstractParameter parameter) {
		super("S", "show-ref");
		this.parameters = parameter;
	}

	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(false)
				.desc("Add reference base to output")
				.build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
    	parameters.setShowReferenceBase(true);
	}

}