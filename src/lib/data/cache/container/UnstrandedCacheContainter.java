package lib.data.cache.container;

import java.util.List;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.Cache;
import lib.tmp.CoordinateController;
import lib.util.coordinate.Coordinate;

public class UnstrandedCacheContainter<T extends AbstractData> 
implements CacheContainer<T> {

	private final NextPositionCache nextPositionCache;
	
	private final List<Cache<T>> caches;

	public UnstrandedCacheContainter(final CoordinateController coordinateController, final List<Cache<T>> caches) {
		nextPositionCache = new SimpleNextPositionCache(coordinateController);
		this.caches = caches;
	}
	
	@Override
	public int getNext(final int windowPosition) {
		return nextPositionCache.getNext(windowPosition);
	}
	
	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		nextPositionCache.addRecordWrapper(recordWrapper);
		
		for (final Cache<T> cache : caches) {
			cache.addRecordWrapper(recordWrapper);
		}
	}
	
	@Override
	public void addData(final T data, Coordinate coordinate) {
		for (final Cache<T> cache : caches) {
			cache.addData(data, coordinate);
		}
	}
	
	public void clear() {
		nextPositionCache.clear();
		for (final Cache<T> cache : caches) {
			cache.clear();
		}
	}

	@Override
	public List<Cache<T>> getCaches() {
		return caches;
	}

}
