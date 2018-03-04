package jacusa.cli.options.librarytype;

import java.util.ArrayList;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class TwoConditionLibraryTypeOption<T extends AbstractData> 
extends OneConditionLibraryTypeOption<T> {

	private static final char SEP = ',';
	
	public TwoConditionLibraryTypeOption(
			final AbstractConditionParameter<T> conditionParameter1, 
			final AbstractConditionParameter<T> conditionParameter2,
			final AbstractParameter<T, ?> generalParameter) {
		super(new ArrayList<AbstractConditionParameter<T>>() {
			private static final long serialVersionUID = 1L;
			{
				add(conditionParameter1);
				add(conditionParameter2);
			}
		}, generalParameter);
	}

	@Override
	public Option getOption() {
		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("Choose the library types for " +
					"condition1(cond1) and condition2(cond2).\nFormat: cond1,cond2. \n" +
					"Possible values for cond1 and cond2:\n" + getPossibleValues() + "\n" +
					"default: " + LIBRARY_TYPE.UNSTRANDED + SEP + LIBRARY_TYPE.UNSTRANDED)
				.build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
	    	String s = line.getOptionValue(getOpt());
	    	String[] ss = s.split(Character.toString(SEP));

	    	StringBuilder sb = new StringBuilder();
	    	sb.append("Format: cond1,cond2. \n");
	    	sb.append("Possible values for cond1 and cond2:\n");
	    	sb.append(getPossibleValues());

	    	if (ss.length != 2) {
	    		throw new IllegalArgumentException(sb.toString());
	    	}

	    	LIBRARY_TYPE libraryType1 = parse(ss[0]);
	    	LIBRARY_TYPE libraryType2 = parse(ss[1]);

	    	if (libraryType1 == null || libraryType2 == null) {
	    		throw new IllegalArgumentException(sb.toString());
	    	}

	    	getConditionParameters().get(0).setLibraryType(libraryType1);
	    	getConditionParameters().get(1).setLibraryType(libraryType2);
	    }
	}
	
}