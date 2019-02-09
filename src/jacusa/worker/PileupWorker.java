package jacusa.worker;

import java.util.SortedSet;

import jacusa.method.pileup.PileupMethod;
import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.ParallelData.Builder;
import lib.data.result.BaseSubstitutionResult;
import lib.data.result.Result;
import lib.stat.AbstractStat;
import lib.util.ReplicateContainer;
import lib.util.coordinate.Coordinate;
import lib.worker.AbstractWorker;

public class PileupWorker
extends AbstractWorker {
	
	private final AbstractStat stat;
	
	public PileupWorker(final PileupMethod method, final int threadId) {
		super(method, threadId);
		stat = method.getParameter().getStatParameter()
				.newInstance(method.getParameter().getConditionsSize());
	}

	@Override
	protected ParallelData createParallelData(Builder parallelDataBuilder, Coordinate coordinate) {
		for (int conditionIndex = 0; conditionIndex < getConditionContainer().getConditionSize() ; ++conditionIndex) {
			final ReplicateContainer replicateContainer = getConditionContainer().getReplicatContainer(conditionIndex);
			for (int replicateIndex = 0; replicateIndex < replicateContainer.getReplicateSize() ; ++replicateIndex) {
				final DataContainer replicate = getConditionContainer().getDefaultDataContainer(conditionIndex, replicateIndex, coordinate);
				parallelDataBuilder.withReplicate(conditionIndex, replicateIndex, replicate);
			}	
		}
		return parallelDataBuilder.build();
	}
	
	@Override
	protected Result process(final ParallelData parallelData) {
		final Result result = stat.filter(parallelData); 
		
		final SortedSet<BaseSubstitution> baseSubs = getParameter().getReadSubstitutions();
		if (! getParameter().getReadSubstitutions().isEmpty()) {
			return new BaseSubstitutionResult(baseSubs, result);
		}
		
		return result;
	}
		
}
