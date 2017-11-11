package jacusa.filter.factory;

import jacusa.filter.UnstrandedFilterContainer;
import jacusa.filter.storage.DistanceStorage;
import lib.cli.parameters.AbstractParameter;
import lib.data.basecall.PileupData;

public class ReadPositionDistanceFilterFactory<T extends PileupData> 
extends AbstractDistanceFilterFactory<T> {

	public ReadPositionDistanceFilterFactory(final AbstractParameter<T> parameters) {
		super('B', "Filter distance to Read Start/End.", 6, 0.5, 2, parameters);
	}

	public ReadPositionDistanceFilter<T> getFilter() {
		return new ReadPositionDistanceFilter<T>(getC(), 
				getFilterDistance(), getFilterMinRatio(), getFilterMinCount(),
				getParameters());
	}

	@Override
	public void registerFilter(UnstrandedFilterContainer<T> filterContainer) {
		filterContainer.add(getFilter());
		
		DistanceStorage<T> storage = new DistanceStorage<T>(getC(), getFilterDistance(), getParameters().getBaseConfig());
		filterContainer.registerStorage(storage);
		filterContainer.registerProcessRecord(storage);
	}
	
}