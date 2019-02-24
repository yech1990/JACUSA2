package jacusa.io.format.pileup;

import java.util.ArrayList;
import java.util.List;

import jacusa.io.format.BaseSubstitutionBED6adder;
import jacusa.io.format.BaseSubstitutionDataAdder;
import jacusa.io.format.DeletionCountDataAdder;
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

public class BED6pileupResultFormat 
extends AbstractResultFileFormat {

	public static final char CHAR = 'B';
	
	public BED6pileupResultFormat(
			final String methodName, 
			final GeneralParameter parameter) {
		
		super(CHAR, "Default", methodName, parameter);
	}

	@Override
	public BEDlikeResultFileWriter createWriter(final String outputFileName) {
		final BaseCallCount.AbstractParser bccParser = 
				new DefaultBaseCallCount.Parser(InputOutput.VALUE_SEP, InputOutput.EMPTY_FIELD);
		
		BED6adder bed6adder = new DefaultBED6adder(getMethodName(), "stat");
		DataAdder dataAdder = new PileupDataAdder(bccParser); 
		if (getParameter().getReadSubstitutions().size() > 0) {
			final List<BaseSubstitution> baseSubs = new ArrayList<>(getParameter().getReadSubstitutions()); 
			bed6adder = new BaseSubstitutionBED6adder(baseSubs, bed6adder);
			dataAdder = new StratifiedDataAdder(
					dataAdder, 
					new BaseSubstitutionDataAdder(bccParser, baseSubs, dataAdder));
		}
		
		final BEDlikeResultFileWriterBuilder builder = new BEDlikeResultFileWriterBuilder(outputFileName, getParameter())
				.addBED6Adder(bed6adder)
				.addDataAdder(dataAdder);
		if (getParameter().showDeletionCount()) {
			builder.addDataAdder(new DeletionCountDataAdder());
		}
		builder.addInfoAdder(new DefaultInfoAdder(getParameter()));
		return builder.build();
	}

}