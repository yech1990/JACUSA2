package jacusa.filter.factory.basecall.rtarrest;

import java.util.List;

import jacusa.filter.factory.basecall.INDEL_FilterFactory;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.FilteredDataFetcher;
import lib.data.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordExtendedProcessor;

/**
 * TODO add comments.
 * 
 * @param 
 */

public class RTarrestINDEL_FilterFactory 
extends AbstractRTarrestBaseCallcountFilterFactory {

	public RTarrestINDEL_FilterFactory(
			final Apply2readsBaseCallCountSwitch bccSwitch, 
			final FilteredDataFetcher<BaseCallCountFilteredData, BaseCallCount> filteredDataFetcher) {
		
		super(
				INDEL_FilterFactory.getOptionBuilder().build(),
				bccSwitch, filteredDataFetcher);
	}
	

	@Override
	protected List<RecordExtendedProcessor> createRecordProcessors(
			SharedStorage sharedStorage, PositionProcessor positionProcessor) {
		
		return INDEL_FilterFactory.createRecordProcessor(sharedStorage, getFilterDistance(), positionProcessor);
	}

}