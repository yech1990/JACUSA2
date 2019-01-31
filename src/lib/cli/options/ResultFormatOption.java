package lib.cli.options;

import java.util.Map;

import lib.cli.parameter.GeneralParameter;
import lib.io.ResultFormat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class ResultFormatOption 
extends AbstractACOption {

	private final GeneralParameter parameter;
	private final Map<Character, ResultFormat> resultFormats;

	public ResultFormatOption(final GeneralParameter parameter, 
			final Map<Character, ResultFormat> resultFormats) {

		super("f", "output-format");
		this.parameter 		= parameter;
		this.resultFormats 	= resultFormats;
	}

	@Override
	public Option getOption(final boolean printExtendedHelp) {
		StringBuffer sb = new StringBuffer();

		boolean required = true;
		for (char c : resultFormats.keySet()) {
			ResultFormat resultFormat = resultFormats.get(c);
			if (parameter.getResultFormat() != null && 
					resultFormat.getC() == parameter.getResultFormat().getC()) {
				sb.append("<*>");
				required = false;
			} else {
				sb.append("< >");
			}
			sb.append(" " + c);
			sb.append(": ");
			sb.append(resultFormat.getDesc());
			sb.append("\n");
		}
		
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.required(required)
				.desc("Choose output format:\n" + sb.toString())
				.build(); 
	}

	@Override
	public void process(final CommandLine line) throws IllegalArgumentException {
		final String s = line.getOptionValue(getOpt());
		for (int i = 0; i < s.length(); ++i) {
			final char c = s.charAt(i);
			if (! resultFormats.containsKey(c)) {
				throw new IllegalArgumentException("Unknown output format: " + c);
			}
			parameter.setResultFormat(resultFormats.get(c));
		}
	}

}