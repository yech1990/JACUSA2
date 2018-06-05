package jacusa.filter.factory.distance.lrtarrest;

import java.util.ArrayList;

import java.util.List;

import org.apache.commons.cli.Option;

import jacusa.filter.cache.processrecord.ProcessDeletionOperator;
import jacusa.filter.cache.processrecord.ProcessInsertionOperator;
import jacusa.filter.cache.processrecord.ProcessReadStartEnd;
import jacusa.filter.cache.processrecord.ProcessRecord;
import jacusa.filter.cache.processrecord.ProcessSkippedOperator;
import lib.data.AbstractData;
import lib.data.cache.region.RegionDataCache;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasLRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.filter.HasRefPos2BaseCallCountFilterData;

/**
 * TODO add comments.
 * 
 * @param <T>
 */
public class LRTarrestCombinedFilterFactory<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasLRTarrestCount & HasRefPos2BaseCallCountFilterData> 
extends AbstractLRTarrestFilterFactory<T> {

	public LRTarrestCombinedFilterFactory() {
		super(Option.builder(Character.toString('D'))
				.desc("Filter artefacts (INDEL, read start/end, and splice site) of read arrest positions.")
				.build(),
				null, null, // FIXME
				6, 0.5);
	}
	
	@Override
	protected List<ProcessRecord> createProcessRecord(RegionDataCache<T> regionDataCache) {
		final List<ProcessRecord> processRecords = new ArrayList<ProcessRecord>(4);
		// INDELs
		processRecords.add(new ProcessInsertionOperator(getDistance(), regionDataCache));
		processRecords.add(new ProcessDeletionOperator(getDistance(), regionDataCache));
		// read start end 
		processRecords.add(new ProcessReadStartEnd(getDistance(), regionDataCache));
		// introns
		processRecords.add(new ProcessSkippedOperator(getDistance(), regionDataCache));
		return processRecords;
	}
	
}