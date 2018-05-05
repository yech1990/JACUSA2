package jacusa.io.copytmp;

import java.io.IOException;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.result.Result;
import lib.io.ResultFormat;
import lib.io.ResultWriter;
import lib.io.copytmp.CopyTmpResult;

/**
 * TODO add comments. 
 * Used, when results should be split based on artefact filters for one thread
 *
 * @param <T>
 * @param <R>
 */
public class FileCopyTmpSeparatedResult<T extends AbstractData, R extends Result<T>> 
implements CopyTmpResult<T, R> {

	// handles NOT filtered results
	private FileCopyTmpResult<T, R> copyResult;
	// handles filtered results
	private FileCopyTmpResult<T, R> copyFilteredResult;
	
	public FileCopyTmpSeparatedResult(final int threadId, 
			final ResultWriter<T, R> resultWriter, 
			final ResultWriter<T, R> filteredResultWriter,
			final ResultFormat<T, R> resultFormat) {

		copyResult 			= new FileCopyTmpResult<T, R>(threadId, resultWriter, resultFormat);
		copyFilteredResult 	= new FileCopyTmpResult<T, R>(threadId, filteredResultWriter, resultFormat);
	}
	
	@Override
	public void closeTmpReader() throws IOException {
		copyResult.closeTmpReader();
		copyFilteredResult.closeTmpReader();
	}
	
	@Override
	public void closeTmpWriter() throws IOException {
		copyResult.closeTmpWriter();
		copyFilteredResult.closeTmpWriter();
	}

	@Override
	public void newIteration() {
		copyResult.newIteration();
		copyFilteredResult.newIteration();
	}

	public void addResult(final R result, final List<AbstractConditionParameter<T>> conditionParameters) throws Exception {
		if (result.isFiltered()) {
			copyFilteredResult.addResult(result, conditionParameters);
		} else {
			copyResult.addResult(result, conditionParameters);
		}
	}
	
	@Override
	public void copy(int iteration) throws IOException {
		copyResult.copy(iteration);
		copyFilteredResult.copy(iteration);
	}

}
