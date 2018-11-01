package lib.cli.options;

import lib.cli.parameter.AbstractParameter;
import lib.worker.WorkerDispatcher;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class FilterModusOption extends AbstractACOption {

	final private AbstractParameter parameter;
	
	public FilterModusOption(final AbstractParameter parameter) {
		super("s", "split");
		this.parameter = parameter;
	}
	
	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.hasArg(false)
		        .desc("Put feature-filtered results in to an other file (= RESULT-FILE" + 
		        		WorkerDispatcher.FILE_SUFFIX + ")")
		        .build();
	}
	
	@Override
	public void process(final CommandLine line) throws Exception {
		parameter.splitFiltered(true);
	}

}