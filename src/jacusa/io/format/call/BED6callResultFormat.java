package jacusa.io.format.call;

import java.util.ArrayList;
import java.util.List;

import jacusa.io.format.BaseSubstitution2BaseCallCountAdder;
import jacusa.io.format.StratifiedDataAdder;
import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
import lib.cli.parameter.GeneralParameter;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBaseCallCount;
import lib.io.AbstractResultFileFormat;
import lib.io.BEDlikeResultFileWriter;
import lib.io.BEDlikeResultFileWriter.BEDlikeResultFileWriterBuilder;
import lib.io.InputOutput;
import lib.io.format.bed.BED6adder;
import lib.io.format.bed.DataAdder;
import lib.io.format.bed.DefaultBED6adder;
import lib.io.format.bed.DefaultInfoAdder;

/**
 * This class implements an extended BED6 format to represent variants identified by "call" method. 
 *
 * @param <T>
 * @param <R>
 */
public class BED6callResultFormat 
extends AbstractResultFileFormat {

	// unique char id for CLI 
	public static final char CHAR = 'B';

	private final String scoreLabel;
	
	public BED6callResultFormat(
			final String methodName, 
			final GeneralParameter parameter) {
		
		super(CHAR, "BED6-extended result format", methodName, parameter);
		scoreLabel = "score";
	}

	@Override
	public BEDlikeResultFileWriter createWriter(final String outputFileName) {
		final BaseCallCount.AbstractParser bccParser = 
				new DefaultBaseCallCount.Parser(InputOutput.VALUE_SEP, InputOutput.EMPTY_FIELD);
		
		BED6adder bed6adder = new DefaultBED6adder(getMethodName(), scoreLabel);
		DataAdder dataAdder = new CallDataAdder(bccParser);
		final BEDlikeResultFileWriterBuilder builder = new BEDlikeResultFileWriterBuilder(outputFileName, getParameter());
		
		if (getParameter().getReadSubstitutions().size() > 0) {
			final List<BaseSubstitution> baseSubs = new ArrayList<>(getParameter().getReadSubstitutions());
			dataAdder = new StratifiedDataAdder(
					dataAdder, 
					new BaseSubstitution2BaseCallCountAdder(bccParser, baseSubs, dataAdder));
		}
		
		builder.addBED6Adder(bed6adder);
		builder.addDataAdder(dataAdder);
		builder.addInfoAdder(new DefaultInfoAdder(getParameter()));
		return builder.build();
	}

}
