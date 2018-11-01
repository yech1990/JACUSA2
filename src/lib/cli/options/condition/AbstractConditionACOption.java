package lib.cli.options.condition;

import java.util.ArrayList;
import java.util.List;

import lib.cli.options.AbstractACOption;
import lib.cli.parameter.AbstractConditionParameter;

public abstract class AbstractConditionACOption 
extends AbstractACOption {

	private int conditionIndex;
	private final List<AbstractConditionParameter> conditionParameters;
		
	public AbstractConditionACOption(final String opt, final String longOpt, final List<AbstractConditionParameter> conditionParameters) {
		super(opt, longOpt);
		conditionIndex 	= -1;
		this.conditionParameters = conditionParameters;
	}
	
	public AbstractConditionACOption(final String opt, final String longOpt, final int conditionIndex, final AbstractConditionParameter conditionParameter) {
		super(opt != null ? opt + (conditionIndex + 1) : null,
				longOpt != null ? longOpt + (conditionIndex + 1) : null);

		this.conditionIndex = conditionIndex;
		conditionParameters = new ArrayList<AbstractConditionParameter>(1);
		conditionParameters.add(conditionParameter);
	}

	public List<AbstractConditionParameter> getConditionParameters() {
		return conditionParameters;
	}
	
	public AbstractConditionParameter getConditionParameter() {
		return conditionParameters.get(0);
	}

	public int getConditionIndex() {
		return conditionIndex == -1 ? -1 : conditionIndex + 1;
	}

}
