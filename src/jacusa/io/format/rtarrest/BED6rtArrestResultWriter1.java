package jacusa.io.format.rtarrest;


import jacusa.io.format.BEDlikeWriter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.HasRTcount;
import lib.data.has.HasReferenceBase;
import lib.data.result.Result;
import lib.data.result.hasStatistic;

public class BED6rtArrestResultWriter1<T extends AbstractData & HasReferenceBase & HasRTcount, R extends Result<T> & hasStatistic> 
extends BEDlikeWriter<T, R> {
	
	// read start, trough, and end	
	private static final String RTinfo = "reads";

	protected BED6rtArrestResultWriter1(final String filename, final AbstractParameter<T, R> parameter) {
		super(filename, parameter);
	}

	@Override
	protected String getHeaderStat() {
		return "pvalue";
	}
	
	@Override
	protected String getFieldName() {
		return "arrest";
	}
	
	@Override
	protected void addHeaderConditionData(final StringBuilder sb, final int conditionIndex, final int replicateIndex) {
		addHeaderReadInfo(sb, conditionIndex, replicateIndex);
	}

	@Override
	protected String getStatistic(final R result) {
		return Double.toString(result.getStatistic());
	}
	
	protected void addHeaderReadInfo(final StringBuilder sb, int conditionIndex, final int replicateIndex) {
		sb.append(FIELD_SEP);
		sb.append(RTinfo);
		sb.append(conditionIndex + 1);
		sb.append(replicateIndex + 1);
		
		if (getParameter().isDebug()) {
			sb.append(FIELD_SEP);
			sb.append("readStart");
			sb.append(VALUE_SEP);
			sb.append("readInner");
			sb.append(VALUE_SEP);
			sb.append("readEnd");
		}
	}

	@Override
	protected void addResultReplicateData(final StringBuilder sb, final T data) {
		addResultReadInfoCount(sb, data);
	}
	
	protected void addResultReadInfoCount(final StringBuilder sb, final T data) {
		sb.append(FIELD_SEP);
		sb.append(data.getRTarrestCount().getReadArrest());
		sb.append(VALUE_SEP);
		sb.append(data.getRTarrestCount().getReadThrough());
		
		if (getParameter().isDebug()) {
			sb.append(FIELD_SEP);
			sb.append(data.getRTarrestCount().getReadStart());
			sb.append(VALUE_SEP);
			sb.append(data.getRTarrestCount().getReadInternal());
			sb.append(VALUE_SEP);
			sb.append(data.getRTarrestCount().getReadEnd());
		}
	}

}