package test.lib.cli.options.condition.filter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import lib.cli.options.condition.AbstractConditionACOption;
import lib.cli.options.condition.filter.FilterNMsamTagConditionOption;
import lib.cli.options.condition.filter.samtag.MaxValueSamTagFilter;
import lib.cli.parameter.ConditionParameter;
import test.lib.cli.options.condition.AbstractConditionACOptionTest;

/**
 * Tests @see lib.cli.options.condition.filter.FilterNMsamTagConditionOption#process(org.apache.commons.cli.CommandLine)
 */
public class FilterNMsamTagConditionOptionTest extends AbstractConditionACOptionTest<Integer> {

	@Override
	protected Stream<Arguments> testProcessGeneral() {
		final int d = getDefaultValue();
		return Stream.of(
				createGeneralArguments(d, 1, 1024),
				createGeneralArguments(d, 2, 4),
				createGeneralArguments(d, 3, 8) );
	}
	
	@Test
	void testProcessGeneralFail() throws Exception {
		final List<ConditionParameter> conditionParameters 	= 
				createConditionParameters(2); 
		final AbstractConditionACOption testInstance 		= 
				createGeneralTestInstance(conditionParameters);

		// < 0
		myAssertOptThrows(IllegalArgumentException.class, testInstance, Integer.toString(-1));
		// not a number
		myAssertOptThrows(IllegalArgumentException.class, testInstance, "wrong");
	}
	
	@Override
	protected Stream<Arguments> testProcessIndividual() {
		final int d = getDefaultValue();
		return Stream.of(
				createIndividualArguments(d, Arrays.asList(), Arrays.asList()),
				createIndividualArguments(
						d, Arrays.asList(1, 2, 3), Arrays.asList(10, 20, 30)),
				createIndividualArguments(
						d, Arrays.asList(1, 2, 3), Arrays.asList(10, 15, 30)) );
		
	}
	
	@Override
	protected String convertString(Integer value) {
		return Integer.toString(value);
	}
	
	@Override
	protected AbstractConditionACOption createIndividualTestInstance(ConditionParameter conditionParameter) {
		return new FilterNMsamTagConditionOption(conditionParameter);
	}

	@Override
	protected AbstractConditionACOption createGeneralTestInstance(List<ConditionParameter> conditionParameters) {
		return new FilterNMsamTagConditionOption(conditionParameters);
	}
	
	@Override
	protected Integer getActualValue(ConditionParameter conditionParameter) {
		for (final MaxValueSamTagFilter filter : conditionParameter.getSamTagFilters()) {
			if (filter.getTag().equals(FilterNMsamTagConditionOption.TAG)) {
				return filter.getValue();
			}
		}
		return 0;
	}
	
}
