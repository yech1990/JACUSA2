package lib.data.cache.lrtarrest;

// FIXME
public abstract class AbstractUniqueLRTarrest2BaseCallCountDataCache {
// extends AbstractDataCache<T> {

	/*
	private final LIBRARY_TYPE libraryType;
	private final BaseCallConfig baseCallConfig;
	private final byte minBASQ;

	private final LRTarrest2FilteredBaseCallCount start;
	private final LRTarrest2FilteredBaseCallCount end;
		
	private boolean[] visited;
	
	public AbstractUniqueLRTarrest2BaseCallCountDataCache(final LIBRARY_TYPE libraryType, 
			final byte minBASQ,
			final BaseCallConfig baseCallConfig,
			final CoordinateController coordinateController) {
		
		super(coordinateController);
		
		this.libraryType 	= libraryType;
		this.minBASQ 		= minBASQ;
		this.baseCallConfig = baseCallConfig;
		
		final int n = coordinateController.getActiveWindowSize();
		start 		= new LRTarrest2FilteredBaseCallCount(coordinateController, n);
		end 		= new LRTarrest2FilteredBaseCallCount(coordinateController, n);
	}

	@Override
	public void addData(final T data, final Coordinate coordinate) {
		final int winArrestPos = getCoordinateController().convert2windowPosition(coordinate);
		
		boolean invert = false;
		if (coordinate.getStrand() == STRAND.REVERSE) {
			invert = true;
		}

		final Map<Integer, BaseCallCount> ref2bc = getRefPos2bc(data);
		
		switch (getLibraryType()) {

		case UNSTRANDED:
			start.copyAll(winArrestPos, invert, ref2bc);
			end.copyAll(winArrestPos, invert, ref2bc);
			break;

		case FR_FIRSTSTRAND:
			end.copyAll(winArrestPos, invert, ref2bc);
			break;

		case FR_SECONDSTRAND:
			start.copyAll(winArrestPos, invert, ref2bc);
			break;
			
		case MIXED:
			throw new IllegalArgumentException("Cannot determine read arrest and read through from library type: " + getLibraryType().toString());
		}
	}
	
	public LIBRARY_TYPE getLibraryType() {
		return libraryType;
	}
	
	public void addRecord(final SAMRecordWrapper recordWrapper) {
		for (final AlignmentBlock alignmentBlock : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			final WindowPositionGuard windowPositionGuard = 
					getCoordinateController().convert(alignmentBlock.getReferenceStart(), alignmentBlock.getReadStart() - 1, alignmentBlock.getLength());

			addRegion(windowPositionGuard.getReferencePosition(), 
					windowPositionGuard.getReadPosition(), 
					windowPositionGuard.getLength(), recordWrapper);
		}
	}

	public void addRegion(final int referencePosition, final int readPosition, int length, 
			final SAMRecordWrapper recordWrapper) {
		
		if (referencePosition < 0) {
			throw new IllegalArgumentException("Reference Position cannot be < 0! -> outside of alignmentBlock");
		}
		
		final SAMRecord record = recordWrapper.getSAMRecord();
		int windowArrestPosition1 = getCoordinateController().convert2windowPosition(record.getAlignmentStart());
		int windowArrestPosition2 = getCoordinateController().convert2windowPosition(record.getAlignmentEnd());

		for (int j = 0; j < length; ++j) {
			final int tmpReferencePosition 	= referencePosition + j;
			final int tmpReadPosition 		= readPosition + j;

			// check baseCall is not "N"
			final byte bc = record.getReadBases()[tmpReadPosition];
			final int baseIndex = baseCallConfig.getBaseIndex(bc);
			if (baseIndex < 0) {
				continue;
			}

			final byte bq = record.getBaseQualities()[tmpReadPosition];
			if (bq < minBASQ) {
				continue;
			}
			
			if (! visited[tmpReadPosition]) {
				if (windowArrestPosition1 >= 0) {
					add(windowArrestPosition1, tmpReferencePosition, tmpReadPosition, baseIndex, recordWrapper, start);
				}
				if (windowArrestPosition2 >= 0) {
					add(windowArrestPosition2, tmpReferencePosition, tmpReadPosition, baseIndex, recordWrapper, end);
				}
				visited[tmpReadPosition] = true;
			}
		}
	}

	private void add(final int windowArrestPosition, final int baseCallReferencePosition, final int readPosition, final int baseIndex, 
			final SAMRecordWrapper recordWrapper, LRTarrest2FilteredBaseCallCount dest) {

		dest.addBaseCall(windowArrestPosition, baseCallReferencePosition, baseIndex);
	}
	
	protected abstract Map<Integer, BaseCallCount> getRefPos2bc(T Data);
	
	@Override
	public void clear() {
		start.clear();
		end.clear();
	}
*/
}
