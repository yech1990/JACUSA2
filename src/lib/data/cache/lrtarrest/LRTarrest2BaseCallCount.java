package lib.data.cache.lrtarrest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lib.cli.options.BaseCallConfig;
import lib.data.BaseCallCount;
import lib.util.coordinate.CoordinateController;

public class LRTarrest2BaseCallCount {

	private static final int READ_LENGTH = 100; 
	
	private final CoordinateController coordinateController;
	
	private final Set<Integer> arrest;
	private final int[] arrest2count;
	private final List<Set<Integer>> arrest2window;
	private final List<Set<Integer>> arrest2windowNonRef;
	private final int[][][] arrest2window2bc; 
	
	// nonRef base call outside of window
	private final Map<Integer, Set<Integer>> arrest2ref;
	private final Map<Integer, Set<Integer>> arrest2refNonRef;
	private final Map<Integer, Map<Integer, BaseCallCount>> arrest2ref2bc;
	
	public LRTarrest2BaseCallCount(CoordinateController coordinateController, int n) {
		this.coordinateController = coordinateController;
		
		arrest 				= new HashSet<Integer>(n);
		arrest2count		= new int[n];
		arrest2window 		= new ArrayList<Set<Integer>>(n);
		arrest2windowNonRef	= new ArrayList<Set<Integer>>(n);
		arrest2window2bc 	= new int[n][n][BaseCallConfig.BASES.length];
		for (int i = 0; i < n; ++i) {
			arrest2window.add(new HashSet<Integer>(READ_LENGTH));
			arrest2windowNonRef.add(new HashSet<Integer>(5));
		}

		arrest2ref			= new HashMap<Integer, Set<Integer>>();
		arrest2refNonRef	= new HashMap<Integer, Set<Integer>>();
		arrest2ref2bc		= new HashMap<Integer, Map<Integer,BaseCallCount>>();
	}

	public void addArrest(final int windowArrestPos) {
		arrest.add(windowArrestPos);
		arrest2count[windowArrestPos]++;
	}
	
	public void addBaseCall(final int winArrestPos, final int refBCPos, 
			final int baseIndex) {

		final int winBCPos = coordinateController.convert2windowPosition(refBCPos);
		if (winBCPos < 0) {
			_addBaseCallOutsideWindow(winArrestPos, refBCPos, baseIndex);
		} else {
			_addBaseCall(winArrestPos, refBCPos, baseIndex, winBCPos);
		}
	}

	public void addNonRefBaseCall(final int winArrestPos, final int refBCPos, 
			final int baseIndex) {

		final int winBCPos = coordinateController.convert2windowPosition(refBCPos);
		if (winBCPos < 0) {
			_addBaseCallOutsideWindow(winArrestPos, refBCPos, baseIndex);
			if (! arrest2refNonRef.containsKey(winArrestPos)) {
				arrest2refNonRef.put(winArrestPos, new HashSet<Integer>(5));
			}
			arrest2refNonRef.get(winArrestPos).add(refBCPos);
		} else {
			_addBaseCall(winArrestPos, refBCPos, baseIndex, winBCPos);
			arrest2windowNonRef.get(winArrestPos).add(winBCPos);
		}
	}

	private void _addBaseCallOutsideWindow(final int winArrestPos, final int refBCPos, final int baseIndex) {
		if (! arrest2ref.containsKey(winArrestPos)) {
			arrest2ref.put(winArrestPos, new HashSet<Integer>(5));
		}
		arrest2ref.get(winArrestPos).add(refBCPos);

		if (! arrest2ref2bc.containsKey(winArrestPos)) {
			arrest2ref2bc.put(winArrestPos, new HashMap<Integer, BaseCallCount>(10));
		}

		if (arrest2ref2bc.get(winArrestPos).containsKey(refBCPos)) {
			arrest2ref2bc.get(winArrestPos).put(refBCPos, new BaseCallCount());
		}
		
		arrest2ref2bc.get(winArrestPos).get(refBCPos).increment(baseIndex);
	}
	
	private void _addBaseCall(final int winArrestPos, final int refBCPos, 
			final int baseIndex, final int winBCPos) {

		arrest2window.get(winArrestPos).add(winBCPos);
		arrest2window2bc[winArrestPos][winBCPos][baseIndex]++;
	}
	
	public void copyAll(final int winArrestPos, final boolean invert, final Map<Integer, BaseCallCount> dest) {
		final Set<Integer> winRefPositions = arrest2window.get(winArrestPos);
		
		_copy(winArrestPos, invert, winRefPositions, dest);
		if (arrest2ref.containsKey(winArrestPos)) {
			_copyOutsideWindow(winArrestPos, invert, arrest2ref.get(winArrestPos), dest);
		}
	}
	
	public void copyNonRef(final int winArrestPos, final boolean invert,
			final Map<Integer, BaseCallCount> dest) {

		final Set<Integer> winRefPositions = arrest2windowNonRef.get(winArrestPos);
		_copy(winArrestPos, invert, winRefPositions, dest);
		if (arrest2refNonRef.containsKey(winArrestPos)) {
			_copyOutsideWindow(winArrestPos, invert, arrest2refNonRef.get(winArrestPos), dest);
		}
	}

	private void _copy(final int winArrestPos, final boolean invert, 
			final Set<Integer> winRefPositions, final Map<Integer, BaseCallCount> dest) {

		for (final int winRefPosition : winRefPositions) {
			final BaseCallCount baseCallCount = new BaseCallCount(arrest2window2bc[winArrestPos][winRefPosition]);
			if (invert) {
				baseCallCount.invert();
			}

			final int refPosition = coordinateController.convert2referencePosition(winRefPosition);
			
			if (! dest.containsKey(refPosition)) {
				dest.put(refPosition, new BaseCallCount());
			}
			dest.get(refPosition).add(baseCallCount);
		}
	}

	private void _copyOutsideWindow(final int winArrestPos, final boolean invert, 
			final Set<Integer> refPositions, final Map<Integer, BaseCallCount> dest) {

		for (final int refPosition : refPositions) {
			final BaseCallCount baseCallCount = new BaseCallCount(arrest2ref2bc.get(winArrestPos).get(refPosition));
			if (invert) {
				baseCallCount.invert();
			}
			
			if (! dest.containsKey(refPosition)) {
				dest.put(refPosition, new BaseCallCount());
			}
			dest.get(refPosition).add(baseCallCount);
		}
	}

	
	public Set<Integer> getArrest() {
		return arrest;
	}

	public int getArrestCount(final int arrestPos) {
		return arrest2count[arrestPos];
	}

	public void clear() {
		for (final int arrestPos : arrest) {
			for (final int windowPos : arrest2windowNonRef.get(arrestPos)) {
				Arrays.fill(arrest2window2bc[arrestPos][windowPos], 0);
			}
			for (final int windowPos : arrest2window.get(arrestPos)) {
				Arrays.fill(arrest2window2bc[arrestPos][windowPos], 0);
			}
			arrest2count[arrestPos] = 0;
			arrest2window.get(arrestPos).clear();
		}
		arrest.clear();
		
		// TODO test
		arrest2ref.clear();
		arrest2refNonRef.clear();
		arrest2ref2bc.clear();
	}

}
