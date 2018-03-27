package jacusa.method.rtarrest;

import jacusa.cli.options.StatisticFilterOption;
import jacusa.cli.options.librarytype.OneConditionLibraryTypeOption;
import jacusa.cli.parameters.RTarrestParameter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.io.writer.BED6rtArrestResultFormat;
import jacusa.method.call.statistic.AbstractStatisticCalculator;
import jacusa.worker.RTArrestWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;

import lib.cli.options.BedCoordinatesOption;
import lib.cli.options.DebugModusOption;
import lib.cli.options.ReferenceFastaFilenameOption;
import lib.cli.options.ResultFormatOption;
import lib.cli.options.HelpOption;
import lib.cli.options.MaxThreadOption;
import lib.cli.options.ResultFileOption;
import lib.cli.options.ShowReferenceOption;
import lib.cli.options.ThreadWindowSizeOption;
import lib.cli.options.WindowSizeOption;
import lib.cli.options.condition.MaxDepthConditionOption;
import lib.cli.options.condition.MinBASQConditionOption;
import lib.cli.options.condition.MinCoverageConditionOption;
import lib.cli.options.condition.MinMAPQConditionOption;
import lib.cli.options.condition.filter.FilterFlagConditionOption;
import lib.cli.options.condition.filter.FilterNHsamTagOption;
import lib.cli.options.condition.filter.FilterNMsamTagOption;
import lib.data.LRTarrestData;
import lib.data.RTarrestData;
import lib.data.builder.factory.RTarrestDataBuilderFactory;
import lib.data.generator.RTarrestDataGenerator;
import lib.data.result.StatisticResult;
import lib.data.validator.MinCoverageValidator;
import lib.data.validator.ParallelDataValidator;
import lib.data.validator.RTArrestVariantParallelPileup;
import lib.io.AbstractResultFormat;
import lib.method.AbstractMethodFactory;
import lib.util.AbstractTool;

import org.apache.commons.cli.ParseException;

public class RTArrestFactory 
extends AbstractMethodFactory<RTarrestData, StatisticResult<RTarrestData>> {

	public final static String NAME = "rt-arrest";

	public RTArrestFactory(final RTarrestParameter rtArrestParameter) {
		super(NAME, "Reverse Transcription Arrest - 2 conditions", 
				rtArrestParameter, 
				new RTarrestDataBuilderFactory<RTarrestData>(rtArrestParameter), 
				new RTarrestDataGenerator());
	}

	protected void initGlobalACOptions() {
		// result format option only if there is a choice
		if (getResultFormats().size() > 1 ) {
			addACOption(new ResultFormatOption<RTarrestData, StatisticResult<RTarrestData>>(
					getParameter(), getResultFormats()));
		}
		
		// addACOption(new FilterModusOption(getParameter()));
		// addACOption(new BaseConfigOption(getParameter()));
		
		addACOption(new StatisticFilterOption(getParameter().getStatisticParameters()));

		addACOption(new ShowReferenceOption(getParameter()));
		addACOption(new ReferenceFastaFilenameOption(getParameter()));
		addACOption(new HelpOption(AbstractTool.getLogger().getTool().getCLI()));

		addACOption(new MaxThreadOption(getParameter()));
		addACOption(new WindowSizeOption(getParameter()));
		addACOption(new ThreadWindowSizeOption(getParameter()));

		addACOption(new BedCoordinatesOption(getParameter()));
		addACOption(new ResultFileOption(getParameter()));
		
		addACOption(new DebugModusOption(getParameter()));
	}

	protected void initConditionACOptions() {
		// for all conditions
		addACOption(new MinMAPQConditionOption<RTarrestData>(getParameter().getConditionParameters()));
		addACOption(new MinBASQConditionOption<RTarrestData>(getParameter().getConditionParameters()));
		addACOption(new MinCoverageConditionOption<RTarrestData>(getParameter().getConditionParameters()));
		addACOption(new MaxDepthConditionOption<RTarrestData>(getParameter().getConditionParameters()));
		addACOption(new FilterFlagConditionOption<RTarrestData>(getParameter().getConditionParameters()));
		
		addACOption(new FilterNHsamTagOption<RTarrestData>(getParameter().getConditionParameters()));
		addACOption(new FilterNMsamTagOption<RTarrestData>(getParameter().getConditionParameters()));
		
		// condition specific
		for (int conditionIndex = 0; conditionIndex < getParameter().getConditionsSize(); ++conditionIndex) {
			addACOption(new MinMAPQConditionOption<RTarrestData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MinBASQConditionOption<RTarrestData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MinCoverageConditionOption<RTarrestData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MaxDepthConditionOption<RTarrestData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterFlagConditionOption<RTarrestData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			
			addACOption(new FilterNHsamTagOption<RTarrestData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterNMsamTagOption<RTarrestData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			
			addACOption(new OneConditionLibraryTypeOption<RTarrestData>(
					conditionIndex, 
					getParameter().getConditionParameters().get(conditionIndex),
					getParameter()));
		}
	}
	
	public Map<String, AbstractStatisticCalculator<LRTarrestData>> getStatistics() {
		final Map<String, AbstractStatisticCalculator<LRTarrestData>> statistics = 
				new TreeMap<String, AbstractStatisticCalculator<LRTarrestData>>();

		final List<AbstractStatisticCalculator<LRTarrestData>> tmpList = new ArrayList<AbstractStatisticCalculator<LRTarrestData>>(5);
		tmpList.add(new DummyStatistic<LRTarrestData>());
		tmpList.add(new BetaBinomial<LRTarrestData>());
		
		for (final AbstractStatisticCalculator<LRTarrestData> statistic : tmpList) {
			statistics.put(statistic.getName(), statistic);
		}
		
		return statistics;
	}

	public Map<Character, AbstractFilterFactory<RTarrestData>> getFilterFactories() {
		final Map<Character, AbstractFilterFactory<RTarrestData>> abstractPileupFilters = 
				new HashMap<Character, AbstractFilterFactory<RTarrestData>>();

		List<AbstractFilterFactory<RTarrestData>> filterFactories = 
				new ArrayList<AbstractFilterFactory<RTarrestData>>(5);

		for (final AbstractFilterFactory<RTarrestData> filterFactory : filterFactories) {
			abstractPileupFilters.put(filterFactory.getC(), filterFactory);
		}

		return abstractPileupFilters;
	}

	public Map<Character, AbstractResultFormat<RTarrestData, StatisticResult<RTarrestData>>> getResultFormats() {
		Map<Character, AbstractResultFormat<RTarrestData, StatisticResult<RTarrestData>>> resultFormats = 
				new HashMap<Character, AbstractResultFormat<RTarrestData, StatisticResult<RTarrestData>>>();

		AbstractResultFormat<RTarrestData, StatisticResult<RTarrestData>> resultFormat = null;

		resultFormat = new BED6rtArrestResultFormat<RTarrestData, StatisticResult<RTarrestData>>(getParameter());
		resultFormats.put(resultFormat.getC(), resultFormat);
		
		return resultFormats;
	}

	@Override
	public RTarrestParameter getParameter() {
		return (RTarrestParameter) super.getParameter();
	}

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 2) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args);
	}

	@Override
	public List<ParallelDataValidator<RTarrestData>> getParallelDataValidators() {
		final List<ParallelDataValidator<RTarrestData>> validators = super.getParallelDataValidators();
		validators.add(new MinCoverageValidator<RTarrestData>(getParameter().getConditionParameters()));
		validators.add(new RTArrestVariantParallelPileup<RTarrestData>());
		return validators;
	}
	
	@Override
	public RTArrestWorker createWorker(final int threadId) {
		return new RTArrestWorker(getWorkerDispatcher(), threadId,
				getParameter().getResultFormat().createCopyTmp(threadId),
				getParallelDataValidators(), getParameter());
	}
	
	
	@Override
	public void debug() {
		// set custom
		AbstractTool.getLogger().addDebug("Add additional column(s) in output start,inner,end!");
	}
	
}
