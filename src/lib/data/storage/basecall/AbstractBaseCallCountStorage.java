package lib.data.storage.basecall;

import lib.data.DataContainer;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;
import lib.data.storage.container.SharedStorage;
import lib.data.stroage.AbstractStorage;
import lib.data.stroage.WindowCoverage;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.position.Position;

public abstract class AbstractBaseCallCountStorage
extends AbstractStorage 
implements WindowCoverage {

	private final int[] coverage;
	
	private final Fetcher<BaseCallCount> bccFetcher;
	
	public AbstractBaseCallCountStorage(
			final SharedStorage sharedStorage,
			final Fetcher<BaseCallCount> bccFetcher) {
		
		super(sharedStorage);
		this.bccFetcher = bccFetcher;
		coverage 		= new int[sharedStorage.getCoordinateController().getActiveWindowSize()]; 
	}

	@Override
	public void populate(DataContainer container, int winPos, Coordinate coordinate) {
		if (bccFetcher == null) {
			return;
		}
		
		final BaseCallCount dest = bccFetcher.fetch(container);
		add(winPos, dest);
		if (coordinate.getStrand() == STRAND.REVERSE && dest.getCoverage() > 0) {
			dest.invert();
		}
	}
	
	void add(final int winPos, final BaseCallCount dest) {
		for (final Base base : Base.validValues()) {
			final int count = getCount(winPos, base);
			if (count > 0) {
				dest.set(base, count);
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof AbstractBaseCallCountStorage)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		
		AbstractBaseCallCountStorage bccStore = (AbstractBaseCallCountStorage)obj;
		if (! getCoordinateController().getActive().equals(bccStore.getCoordinateController().getActive())) {
			return false;
		}
		for (int winPos = 0; winPos < getCoordinateController().getActiveWindowSize(); ++winPos) {
			for (final Base base : Base.validValues()) {
				if (getCount(winPos, base) != bccStore.getCount(winPos, base)) {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return getCoordinateController().getActive().hashCode();
	}

	@Override
	final public void increment(Position position) {
		final int winPos = position.getWindowPosition();
		++coverage[winPos];
		increment(winPos, position.getReadBaseCall());
	}
	
	abstract void increment(int winPos, Base base);
	
	public abstract int getCount(final int winPos, final Base base);

	@Override
	public int getCoverage(int winPos) {
		return coverage[winPos];
	}
	
	protected void clearCoverage(int winPos) {
		coverage[winPos] = 0;
	}
		
	
}
