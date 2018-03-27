package lib.cli.parameter;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

import jacusa.cli.parameters.HasConditionParameter;
import jacusa.filter.FilterConfig;
import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.result.Result;
import lib.io.ResultFormat;
import lib.io.ResultWriter;
import lib.method.AbstractMethodFactory;
import lib.util.AbstractTool;

public abstract class AbstractParameter<T extends AbstractData, R extends Result<T>>
implements HasConditionParameter<T> {

	public static final String FILE_SUFFIX = ".filtered";
	
	// cache related
	private int activeWindowSize;
	private int reservedWindowSize;

	private int maxThreads;
	
	private BaseCallConfig baseConfig;
		private char[] bases;
		private boolean showReferenceBase;

	private String referenceFilename;
	private IndexedFastaSequenceFile referenceFile;
		
	// bed file to scan for variants
	private String inputBedFilename;

	// chosen method
	private AbstractMethodFactory<T, R> methodFactory;

	protected List<AbstractConditionParameter<T>> conditionParameters;

	private String resultFilename;
	private ResultWriter<T, R> resultWriter;
	private ResultFormat<T, R> resultFormat;

	private ResultWriter<T, R> filteredResultWriter;
	
	private FilterConfig<T> filterConfig;

		private boolean separate;
	
	// debug flag
	private boolean debug;
	
	protected AbstractParameter() {
		activeWindowSize 	= 10000;
		reservedWindowSize	= 10 * activeWindowSize;
		
		baseConfig			= BaseCallConfig.getInstance();
		bases				= BaseCallConfig.BASES.clone();
		showReferenceBase 	= false;

		maxThreads			= 1;
		
		inputBedFilename	= new String();
		conditionParameters	= new ArrayList<AbstractConditionParameter<T>>(2);

		filterConfig		= new FilterConfig<T>();
		
		separate			= false;
		
		debug				= false;
	}

	public AbstractParameter(final int conditionSize) {
		this();
		
		for (int conditionIndex = 0; conditionIndex < conditionSize; conditionIndex++) {
			conditionParameters.add(createConditionParameter(conditionIndex));
		}
	}
	
	public abstract AbstractConditionParameter<T> createConditionParameter(final int conditionIndex);
	
	public ResultFormat<T, R> getResultFormat() {
		return resultFormat;
	}

	public void setResultFormat(ResultFormat<T, R> resultFormat) {
		this.resultFormat = resultFormat;
	}
	
	/**
	 * @return the filterConfig
	 */
	public FilterConfig<T> getFilterConfig() {
		return filterConfig;
	}
	
	/**
	 * @return the output
	 */
	public ResultWriter<T, R> getResultWriter() {
		// use default resultFormat 
		if (resultWriter == null) {
			resultWriter = resultFormat.createWriter(getResultFilename());
		}

		return resultWriter;
	}

	/**
	 * @return the output
	 */
	public ResultWriter<T, R> getFilteredResultWriter() {
		if (separate && filteredResultWriter == null) {
			filteredResultWriter = resultFormat.createWriter(getResultFilename() + FILE_SUFFIX);
		}
		return filteredResultWriter;
	}
	
	public void setResultFilename(final String resultFilename) {
		this.resultFilename = resultFilename;
	}
	
	public String getResultFilename() {
		return resultFilename;
	}
	
	/**
	 * @param output the output to set
	 */
	public void resetResultWriter() {
		if (resultWriter != null) {
			try {
				resultWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			resultWriter = null;
		}
	}

	@Override
	public List<AbstractConditionParameter<T>> getConditionParameters() {
		return conditionParameters;
	}
	
	@Override
	public void setConditionParameters(
			final List<AbstractConditionParameter<T>> conditionParameters) {
		this.conditionParameters = conditionParameters;
	}
	
	@Override
	public AbstractConditionParameter<T> getConditionParameter(int conditionIndex) {
		return conditionParameters.get(conditionIndex);
	}
	
	@Override
	public int getConditionsSize() {
		return conditionParameters.size();
	}
	
	@Override
	public int getReplicates(int conditionIndex) {
		return getConditionParameter(conditionIndex).getRecordFilenames().length;
	}
	
	/**
	 * @return the baseConfig
	 */
	public char[] getBases() {
		return bases;
	}
	
	/**
	 * @return the baseConfig
	 */
	public void setBases(final char[] bases) {
		this.bases = bases.clone();
	}
	
	/**
	 * @return the windowSize
	 */
	public int getActiveWindowSize() {
		return activeWindowSize;
	}

	/**
	 * @return the threadWindowSize
	 */
	public int getReservedWindowSize() {
		return reservedWindowSize;
	}
	
	/**
	 * @param activeWindowSize the windowSize to set
	 */
	public void setActiveWindowSize(final int activeWindowSize) {
		this.activeWindowSize = activeWindowSize;
	}

	/**
	 * @param reservedWindowSize the threadWindowSize to set
	 */
	public void setReservedWindowSize(final int reservedWindowSize) {
		this.reservedWindowSize = reservedWindowSize;
	}
	
	/**
	 * @return the maxThreads
	 */
	public int getMaxThreads() {
		return maxThreads;
	}

	/**
	 * @param maxThreads the maxThreads to set
	 */
	public void setMaxThreads(final int maxThreads) {
		this.maxThreads = maxThreads;
	}

	/**
	 * @return the bedPathname
	 */
	public String getInputBedFilename() {
		return inputBedFilename;
	}

	/**
	 * @param bedPathname the bedPathname to set
	 */
	public void setInputBedFilename(String bedPathname) {
		this.inputBedFilename = bedPathname;
	}

	/**
	 * @return the methodFactory
	 */
	public AbstractMethodFactory<T, R> getMethodFactory() {
		return methodFactory;
	}

	/**
	 * @param methodFactory the methodFactory to set
	 */
	public void setMethodFactory(final AbstractMethodFactory<T, R> methodFactory) {
		this.methodFactory = methodFactory;
	}

	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug) {
		AbstractTool.getLogger().addDebug("DEBUG Modus!");
		this.debug = debug;
	}

	/**
	 * @return the debug
	 */
	public boolean isSeparate() {
		return separate;
	}

	public boolean showReferenceBase() {
		return showReferenceBase;
	}
	
	public void setShowReferenceBase(boolean showReferenceBase) {
		this.showReferenceBase = showReferenceBase;
	}

	public BaseCallConfig getBaseConfig() {
		return baseConfig;
	}
	
	public String getReferenceFilename() {
		return referenceFilename;
	}
	
	public void setReferernceFilename(final String referenceFilename) {
		this.referenceFilename = referenceFilename;
	}
	
	public IndexedFastaSequenceFile getReferenceFile() {
		if (referenceFile == null && getReferenceFilename() != null && ! getReferenceFilename().isEmpty()) {
			final File file = new File(getReferenceFilename());
			try {
				referenceFile = new IndexedFastaSequenceFile(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		return referenceFile;
	}
	
	public void resetReferenceFile() {
		if (referenceFile != null) {
			try {
				referenceFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			referenceFile = null;
		}
	}
	
	/**
	 * @param debug the debug to set
	 */
	public void setSeparate(boolean separate) {
		this.separate = separate;
	}
	
}
