package jacusa.cli.options.pileupbuilder;

import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.data.BaseQualReadInfoData;
import lib.data.builder.AbstractDataBuilderFactory;
import lib.data.builder.RTArrestPileupBuilderFactory;
import lib.data.builder.hasLibraryType.LIBRARY_TYPE;
import lib.method.AbstractMethodFactory;

public class OneConditionBaseQualReadInfoDataBuilderOption<T extends BaseQualReadInfoData>
extends OneConditionBaseQualDataBuilderOption<T> {

	public OneConditionBaseQualReadInfoDataBuilderOption(final int conditionIndex, final AbstractConditionParameter<T> conditionParameter) {
		super(conditionIndex, conditionParameter);
	}

	public OneConditionBaseQualReadInfoDataBuilderOption(List<AbstractConditionParameter<T>> conditionParameters) {
		super(conditionParameters);
	}
	
	protected AbstractDataBuilderFactory<T> buildPileupBuilderFactory(
			final AbstractMethodFactory<T> abstractMethodFactory,
			final LIBRARY_TYPE libraryType) {
		return new RTArrestPileupBuilderFactory<T>(super.buildPileupBuilderFactory(libraryType));
	}
	
}