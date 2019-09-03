package lib.data.result;

import java.util.SortedSet;

import lib.cli.options.filter.has.BaseSub;
import lib.data.DataContainer;
import lib.data.IntegerData;
import lib.estimate.MinkaParameter;
import lib.stat.estimation.provider.DeletionEstimationCountProvider;

/**
 * TODO
 */
public class DeletionCountResult extends INDELCountResult {
	
	public static final String SCORE 	= "deletion_score";
	public static final String PVALUE = "deletion_pvalue";
	
	private static final long serialVersionUID = 1L;
	
	public DeletionCountResult(		final SortedSet<BaseSub> baseSubs, final Result result,
			final MinkaParameter minkaParameter,
			final DeletionEstimationCountProvider countSampleProvider) {
		
		super(baseSubs, result, minkaParameter,countSampleProvider);
	}
	
	@Override
	void addPValue(Result result, int valueIndex, String value) {
		result.getResultInfo(valueIndex).add(PVALUE, value);
	}
	
	@Override
	void addScore(Result result, int valueIndex, String value) {
		result.getResultInfo(valueIndex).add(SCORE, value);
	}
	
	@Override
	IntegerData getCount(DataContainer container) {
		return container.getDeletionCount();
	}
	
}
