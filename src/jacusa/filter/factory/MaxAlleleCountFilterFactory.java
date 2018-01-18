package jacusa.filter.factory;

import jacusa.filter.AbstractFilter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.builder.ConditionContainer;
import lib.data.has.hasBaseCallCount;
import lib.tmp.CoordinateController;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class MaxAlleleCountFilterFactory<T extends AbstractData & hasBaseCallCount> 
extends AbstractFilterFactory<T> {

	private static final int MAX_ALLELES = 2;
	private int alleles;

	public MaxAlleleCountFilterFactory() {
		super('M', "Max allowed alleles per parallel pileup. Default: "+ MAX_ALLELES);
		alleles = MAX_ALLELES;
	}

	@Override
	public void registerFilter(final CoordinateController coordinateController, ConditionContainer<T> conditionContainer) {
		conditionContainer.getFilterContainer().addFilter(new MaxAlleleFilter(getC()));
	}
	
	@Override
	public void processCLI(String line) throws IllegalArgumentException {
		if (line.length() == 1) {
			return;
		}

		final String[] s = line.split(Character.toString(AbstractFilterFactory.SEP));

		for (int i = 1; i < s.length; ++i) {
			switch(i) {

			case 1:
				final int alleleCount = Integer.valueOf(s[i]);
				if (alleleCount < 0) {
					throw new IllegalArgumentException("Invalid allele count " + line);
				}
				this.alleles = alleleCount;
				break;

			default:
				throw new IllegalArgumentException("Invalid argument: " + line);
			}
		}
	}
	
	private class MaxAlleleFilter extends AbstractFilter<T> {
		
		public MaxAlleleFilter(final char c) {
			super(c);
		}
		
		@Override
		public boolean filter(final ParallelData<T> parallelData) {
			return parallelData.getCombinedPooledData()
					.getBaseCallCount().getAlleles().length > alleles;
		}

		@Override
		public int getOverhang() { 
			return 0;
		}

	}
	
}
