package lib.cli.options.condition;

import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class MinMAPQConditionOption extends AbstractConditionACOption {

	private static final String OPT = "m";
	private static final String LONG_OPT = "min-mapq";
	
	public MinMAPQConditionOption(final List<AbstractConditionParameter> conditions) {
		super(OPT, LONG_OPT, conditions);
	}
	
	public MinMAPQConditionOption(final int conditionIndex, final AbstractConditionParameter condition) {
		super(OPT, LONG_OPT, conditionIndex, condition);
	}
	
	@Override
	public Option getOption(final boolean printExtendedHelp) {
		String s = new String();

		int minMapq = -1;
		if (getConditionIndex() >= 0) {
			s = " for condition " + getConditionIndex();
			minMapq = getConditionParameter().getMinMAPQ();
		} else if (getConditionParameters().size() > 1) {
			s = " for all conditions";
			minMapq = getConditionParameters().get(0).getMinMAPQ();
		}
		s = "filter positions with MAPQ < " + getLongOpt().toUpperCase() + 
				s + "\ndefault: " + minMapq;
		
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
		        .desc(s)
		        .build();
	}

	@Override
	public void process(CommandLine line) throws IllegalArgumentException {
		if (line.hasOption(getOpt())) {
	    	final String value = line.getOptionValue(getOpt());
	    	final int minMapq = Integer.parseInt(value);
	    	if(minMapq < 0 | minMapq > 255) {
	    		throw new IllegalArgumentException(getLongOpt().toUpperCase() + " = " + minMapq + " not valid.");
	    	}

	    	for (final AbstractConditionParameter condition : getConditionParameters()) {
	    		condition.setMinMAPQ(minMapq);
	    	}
	    }
	}

}
