package jacusa.cli.parameters;

import jacusa.io.writer.BED6callResultFormat;
import jacusa.method.call.statistic.dirmult.DirichletMultinomialRobustCompoundError;
import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.cli.parameters.JACUSAConditionParameter;
import lib.data.AbstractData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasPileupCount;
import lib.data.result.StatisticResult;

public class CallParameter<T extends AbstractData & hasBaseCallCount & hasPileupCount> 
extends AbstractParameter<T, StatisticResult<T>> implements hasStatisticCalculator<T> {

	private StatisticFactory<T> statisticFactory;
	
	public CallParameter(final int conditionSize) {
		super(conditionSize);
		
		statisticFactory = new StatisticFactory<T>(new DirichletMultinomialRobustCompoundError<T>(this), 1.0);
	}
	
	@Override
	public void setDefaultValues() {
		setResultFormat(new BED6callResultFormat<T, StatisticResult<T>>(this));
	}
	
	@Override
	public AbstractConditionParameter<T> createConditionParameter() {
		return new JACUSAConditionParameter<T>();
	}
	
	@Override
	public StatisticFactory<T> getStatisticParameters() {
		return statisticFactory;
	}

}