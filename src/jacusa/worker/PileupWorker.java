package jacusa.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jacusa.cli.parameters.PileupParameter;
import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.io.copytmp.CopyTmpResult;

import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.Result;
import lib.data.has.hasPileupCount;
import lib.data.validator.ParallelDataValidator;
import lib.io.copytmp.CopyTmp;
import lib.worker.AbstractWorker;
import lib.worker.WorkerDispatcher;

public class PileupWorker<T extends AbstractData & hasPileupCount> 
extends AbstractWorker<T> {

	private PileupParameter<T> pileupParameter;
	private CopyTmpResult<T> copyTmpResult;
	private List<CopyTmp> copyTmps;
	
	public PileupWorker(final WorkerDispatcher<T> workerDispatcher, 
			final int threadId,
			final List<ParallelDataValidator<T>> parallelDataValidators, 
			final PileupParameter<T> pileupParameter) {

		super(workerDispatcher, threadId, parallelDataValidators, pileupParameter);
		this.pileupParameter = pileupParameter;
		try {
			copyTmpResult = new CopyTmpResult<T>(threadId, pileupParameter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		copyTmps = new ArrayList<CopyTmp>(1);
		copyTmps.add(copyTmpResult);
	}

	@Override
	protected void doWork(final ParallelData<T> parallelData) {
		Result<T> result = new Result<T>();
		result.setParallelData(parallelData);
		
		if (pileupParameter.getFilterConfig().hasFiters()) {
			// apply each filter
			for (final AbstractFilterFactory<T, ?> filterFactory : pileupParameter.getFilterConfig().getFilterFactories()) {
				AbstractFilter<T> filter = filterFactory.getFilter();
				filter.applyFilter(result, getConditionContainer());
			}
		}
		
		try {
			copyTmpResult.addResult(result, pileupParameter.getConditionParameters());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<CopyTmp> getCopyTmps() {
		return copyTmps;
	}
	
}
