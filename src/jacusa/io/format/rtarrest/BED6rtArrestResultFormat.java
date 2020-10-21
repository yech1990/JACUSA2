package jacusa.io.format.rtarrest;

import java.util.ArrayList;
import java.util.List;

import jacusa.io.format.RTarrestBaseSubDataAdder;
import jacusa.io.format.StratifiedDataAdder;
import lib.cli.options.filter.has.BaseSub;
import lib.cli.parameter.GeneralParameter;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBCC;
import lib.io.AbstractResultFileFormat;
import lib.io.BEDlikeResultFileWriter;
import lib.io.BEDlikeResultFileWriter.BEDlikeResultFileWriterBuilder;
import lib.io.InputOutput;
import lib.io.format.bed.BED6adder;
import lib.io.format.bed.DataAdder;
import lib.io.format.bed.DefaultBED6adder;
import lib.io.format.bed.DefaultInfoAdder;

/**
 * This class implements an extended BED6 format to represent 
 * variants and read arrest events by "rt-arrest" method.
 */
public class BED6rtArrestResultFormat extends AbstractResultFileFormat {

	public static final char CHAR = 'A';
	
	protected BED6rtArrestResultFormat(
			final char c,
			final String desc,
			final String methodName,
			final GeneralParameter parameter) {
		
		super(c, desc, methodName, parameter);
	}

	public BED6rtArrestResultFormat(
			final String methodName, 
			final GeneralParameter parameter) {
		
		this(CHAR, "Arrest only", methodName, parameter);
	}

	@Override
	public BEDlikeResultFileWriter createWriter(final String outputFileName) {
		final BaseCallCount.AbstractParser bccParser = 
				new DefaultBCC.Parser(InputOutput.VALUE_SEP, InputOutput.EMPTY_FIELD);
		
		BED6adder bed6adder = new DefaultBED6adder(getMethodName(), "pvalue");
		DataAdder dataAdder = new RTarrestDataAdder(bccParser);
		final BEDlikeResultFileWriterBuilder builder = new BEDlikeResultFileWriterBuilder(outputFileName, getParameter());
		
		if (! getParameter().getReadTags().isEmpty()) {
			final List<BaseSub> baseSubs = new ArrayList<>(getParameter().getReadTags());
			dataAdder = new StratifiedDataAdder(
					dataAdder, 
					new RTarrestBaseSubDataAdder(bccParser, baseSubs, dataAdder));
		}
		
		builder.addBED6Adder(bed6adder);
		builder.addDataAdder(dataAdder);
		builder.addInfoAdder(new DefaultInfoAdder(getParameter()));
		return builder.build();
	}

}
