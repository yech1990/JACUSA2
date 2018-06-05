package lib.cli.options;

import jacusa.filter.factory.AbstractFilterFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class FilterConfigOption<T extends AbstractData> extends AbstractACOption {

	final private AbstractParameter<T, ?> parameters;

	private static final char OR = ',';
	//private static char AND = '&'; // Future Feature add logic

	final private Map<Character, AbstractFilterFactory<T>> filterFactories;

	public FilterConfigOption(final AbstractParameter<T, ?> parameter, 
			final Map<Character, AbstractFilterFactory<T>> filterFactories) {

		super("a", "feature-filter");
		this.parameters 		= parameter;
		this.filterFactories 	= filterFactories;
	}

	@Override
	public Option getOption() {
		final StringBuffer sb = new StringBuffer();
		
		final Options options = new Options(); 
		for (final char c : filterFactories.keySet()) {
			final AbstractFilterFactory<T> filterFactory = filterFactories.get(c);

			final String opt = "___REMOVE___" + Character.toString(c);
			Option option = Option.builder(opt)
					.desc(filterFactory.getDesc())
					.build();

			options.addOption(option);
		}

		final HelpFormatter helpFormatter = new HelpFormatter();
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		helpFormatter.printOptions(pw, 100, options, 0, 1);
		final String s = sw.toString();
		sb.append(s.replaceAll("-___REMOVE___", ""));

		final String argName = "FEATURE-FILTER";
		return Option.builder(getOpt())
				.argName(argName)
				.hasArg(true)
				.desc(
					"chain of " + argName + "; Separate " + argName + " with '" + OR + "'; Add options with ':'\n" +
					"e.g.: D,I:OPTION1:OPTION2=VALUE2\n" +
					sb.toString())
				.build(); 
	}

	@Override
	public void process(final CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
			final String s = line.getOptionValue(getOpt());
			final String[] t = s.split(Character.toString(OR));

			for (final String a : t) {
				final char c = a.charAt(0);
				if (! filterFactories.containsKey(c)) {
					throw new IllegalArgumentException("Unknown SAM processing: " + c);
				}
				final AbstractFilterFactory<T> filterFactory = filterFactories.get(c);
				filterFactory.processCLI(a);
				parameters.getFilterConfig().addFactory(filterFactory);
			}
		}
	}

}