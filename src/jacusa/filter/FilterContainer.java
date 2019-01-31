package jacusa.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jacusa.filter.factory.FilterFactory;
import lib.cli.parameter.ConditionParameter;
import lib.data.storage.Cache;
import lib.data.storage.container.SharedStorage;

/**
 * This class holds the instance of filters.
 *
 * @param 
 */
public class FilterContainer {

	// reference to the filterConfig that created this FilterContainer
	private final FilterConfig filterConfig;

	// map of filters - contains both: AbstractFilter and AbstractDataFilter 
	private final Map<Character, Filter> filters;

	// max overhang that is required by some filter
	private int overhang;

	public FilterContainer(final FilterConfig filterConfig) {
		this.filterConfig 	= filterConfig;
		overhang 			= 0;
		filters				= 
				new HashMap<Character, Filter>(filterConfig.getFilterFactories().size());
	}

	/**
	 * Returns the maximum of all used filters. 
	 * 
	 * @return maximum overhang of filters
	 */
	public int getOverhang() {
		return overhang;
	}

	/**
	 * Returns the filterConfig object that created this FilterContainer.
	 * 
	 * @return filterConfig that created this FilterContainer.
	 */
	public FilterConfig getFilterConfig() {
		return filterConfig;
	}

	/**
	 * Adds a filter. AbstractDataFilter should be added by <code>addDataFilter()</code>. 
	 * 
	 * @param filter the filter to be added
	 */
	public void addFilter(final Filter filter) {
		filters.put(filter.getC(), filter);
	}

	/**
	 * Returns instances of filters that are active.
	 * 
	 * @return list of active filters
	 */
	public List<Filter> getFilters() {
		return Collections.unmodifiableList(new ArrayList<>(filters.values()));
	}
	
	public Cache createFilterCache(
			final ConditionParameter conditionParameter, 
			final SharedStorage sharedStorage) {
		
		final Cache filterCache = new Cache();
		for (final FilterFactory filterFactory : filterConfig.getFilterFactories()) {
			final Cache tmpCache = filterFactory.createFilterCache(conditionParameter, sharedStorage);
			if (tmpCache != null) {
				filterCache.addCache(tmpCache);
			}
		}
		return filterCache;
	}
	
}
