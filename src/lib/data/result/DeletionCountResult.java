package lib.data.result;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.io.InputOutput;
import lib.util.Info;

public class DeletionCountResult implements Result {

	public static final String DELETION_SCORE 	= "deletion_score";
	public static final String DELETION_PVALUE 	= "deletion_pvalue";
	
	private static final long serialVersionUID = 1L;
	
	private final List<BaseSubstitution> baseSubs;
	private final Result result;

	/*
	private final EstimationSampleProvider estimationSampleProvider;
	private final EstimateDirMult dirMult;
	private final ChiSquaredDistribution dist;
	*/
	
	public DeletionCountResult(final SortedSet<BaseSubstitution> baseSubs, final Result result) {
		this.baseSubs 	= new ArrayList<>(baseSubs);
		this.result 	= result;
	
		/*
		final MinkaParameter minkaParameter = new MinkaParameter();
		this.estimationSampleProvider 		= new DeletionCountSampleProvider(minkaParameter.getMaxIterations());
		this.dirMult						= new EstimateDirMult(minkaParameter);
		this.dist							= new ChiSquaredDistribution(1);
		*/
		
		init();
	}

	@Override
	public boolean isFiltered() {
		return result.isFiltered();
	}
	
	@Override
	public void setFiltered(boolean isFiltered) {
		result.setFiltered(isFiltered);
	}
	
	@Override
	public Info getFilterInfo() {
		return result.getFilterInfo();
	}
	
	@Override
	public Info getFilterInfo(int valueIndex) {
		return result.getFilterInfo(valueIndex);
	}
	
	@Override
	public ParallelData getParellelData() {
		return result.getParellelData();
	}
	
	@Override
	public Info getResultInfo() {
		return result.getResultInfo();
	}
	
	@Override
	public Info getResultInfo(int valueIndex) {
		return result.getResultInfo(valueIndex);
	}
	
	@Override
	public double getStat() {
		return result.getStat();
	}
	
	@Override
	public double getStat(int valueIndex) {
		return result.getStat(valueIndex);
	}
	
	@Override
	public SortedSet<Integer> getValuesIndex() {
		return result.getValuesIndex();
	}
	
	@Override
	public int getValueSize() {
		return result.getValueSize();
	}
	
	private void init() {
		final ParallelData parallelData = getParellelData();
		for (final int valueIndex : result.getValuesIndex()) {
			for (int condition = 0; condition < parallelData.getConditions(); ++condition) {
				final int replicates = parallelData.getReplicates(condition);
				for (int replicate = 0; replicate < replicates; ++replicate) {
					if (valueIndex == Result.TOTAL) {
						addTotalDeletionCount(valueIndex, condition, replicate);
					} else {
						addStratifiedDeletionCount(valueIndex, condition, replicate);
					}
				}
			}
			/* FIXME uncomment
			if (valueIndex == Result.TOTAL) {
				final EstimationSample[] estimationSamples = estimationSampleProvider.convert(parallelData);
				final double lrt 	= dirMult.getLRT(estimationSamples);
				final double pvalue = getPValue(lrt);
				result.getResultInfo(valueIndex).add(DELETION_PVALUE, Util.format(pvalue));
				result.getResultInfo(valueIndex).add(DELETION_SCORE, Util.format(lrt));
			}
			*/
		}
	}

	/*
	private double getPValue(final double lrt) {
		return 1 - dist.cumulativeProbability(lrt);
	} 
	*/
	
	private String getKey(final int condition, final int replicate) {
		return new StringBuilder()
				.append(InputOutput.DELETION_FIELD)
				.append(condition + 1)
				.append(replicate + 1)
				.toString();
	}
	
	private String getValue(final int deletionCount, final int coverage) {
		return new StringBuilder()
				.append(deletionCount)
				.append(InputOutput.VALUE_SEP)
				.append(coverage)
				.toString();
	}
	
	private void addTotalDeletionCount(final int valueIndex, final int condition, final int replicate) {
		final DataContainer container = 
				result.getParellelData().getDataContainer(condition, replicate);
		final int deletionCount = container.getDeletionCount().getValue();
		final int coverage		= container.getCoverage().getValue();
		addDeletionCount(valueIndex, condition, replicate, deletionCount, coverage);
	}
	
	// stratified by base substitutions
	private void addStratifiedDeletionCount(final int valueIndex, final int condition, final int replicate) {
		final BaseSubstitution baseSub 	= baseSubs.get(valueIndex);
		final DataContainer container 	= result.getParellelData().getDataContainer(condition, replicate);
		final int deletionCount 		= container.getBaseSubstitution2DeletionCount().get(baseSub).getValue();
		final int coverage				= container.getBaseSubstitution2Coverage().get(baseSub).getValue();
		addDeletionCount(valueIndex, condition, replicate, deletionCount, coverage);		
	}
	
	private void addDeletionCount(
			final int valueIndex, 
			final int condition, final int replicate, 
			final int deletionCount, final int coverage) {
	
		final String key 		= getKey(condition, replicate);
		final String value 		= getValue(deletionCount, coverage); 
		final Info resultInfo 	= result.getResultInfo(valueIndex);
		resultInfo.add(key, value);
	}
}
