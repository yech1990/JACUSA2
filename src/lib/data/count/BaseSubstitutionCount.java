package lib.data.count;

import java.util.HashMap;
import java.util.Map;

import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
import lib.data.Data;
import lib.data.count.basecall.BaseCallCount;

public class BaseSubstitutionCount implements Data<BaseSubstitutionCount> {

	private static final long serialVersionUID = 1L;

	private final Map<BaseSubstitution, BaseCallCount> count;
	
	public BaseSubstitutionCount() {
		count = new HashMap<>();
	}
	
	public BaseSubstitutionCount(final BaseSubstitutionCount bsc) {
		this();
		for (final BaseSubstitution baseSub : bsc.count.keySet()) {
			count.put(baseSub, bsc.get(baseSub).copy());
		}
	}
	
	@Override
	public BaseSubstitutionCount copy() {
		return new BaseSubstitutionCount(this);
	}
	
	@Override
	public void merge(BaseSubstitutionCount bsc) {
		for (final BaseSubstitution baseSubstitution : bsc.count.keySet()) {
			if (! count.containsKey(baseSubstitution)) {
				count.put(
						baseSubstitution, 
						bsc.get(baseSubstitution).copy() );				
			} else {
				count.get(baseSubstitution).merge(bsc.get(baseSubstitution));
			}
		}
	}

	/*
	public BaseSubstitutionCount add(final BaseSubstitution baseSubstitution, final Base base) {
		count.get(baseSubstitution).increment(base);
		return this;
	}
	*/

	public BaseSubstitutionCount set(final BaseSubstitution baseSubstitution, final BaseCallCount baseCallCount) {
		if (count.containsKey(baseSubstitution)) {
			throw new IllegalStateException();
		}
		count.put(baseSubstitution, baseCallCount);
		return this;
	}
	
	public BaseCallCount get(final BaseSubstitution baseSubstitution) {
		return count.get(baseSubstitution);
	}
	
}
