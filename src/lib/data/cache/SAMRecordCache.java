package lib.data.cache;

import java.util.ArrayList;
import java.util.List;

import lib.tmp.CoordinateController;
import lib.tmp.CoordinateController.WindowPositionGuard;
import lib.util.coordinate.Coordinate;

import htsjdk.samtools.AlignmentBlock;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.has.hasRecordWrapper;

public class SAMRecordCache<T extends AbstractData & hasRecordWrapper> 
extends AbstractCache<T> {

	private List<List<SAMRecordWrapper>> recordWrappers;

	public SAMRecordCache(final CoordinateController coordinateController) {
		super(coordinateController);
		recordWrappers = new ArrayList<List<SAMRecordWrapper>>(coordinateController.getActiveWindowSize());
		for (int i = 0; i < coordinateController.getActiveWindowSize(); ++i) {
			recordWrappers.add(new ArrayList<SAMRecordWrapper>(50));
		}
	}

	@Override
	public void addRecordWrapperPosition(final int readPosition, final SAMRecordWrapper recordWrapper) {
		final int referencePosition = recordWrapper.getSAMRecord().getReferencePositionAtReadPosition(readPosition);
		addSAMRecordWrappers(referencePosition, readPosition, 1, recordWrapper);
	}

	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		for (final AlignmentBlock block : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			addSAMRecordWrappers(block.getReferenceStart(), block.getReadStart() - 1, block.getLength(), recordWrapper);
		}
	}
		
	@Override
	public void addData(final T data, final Coordinate coordinate) {
		final int windowPosition = coordinateController.convert2windowPosition(coordinate); 
		data.getRecordWrapper().addAll(recordWrappers.get(windowPosition));
	}
	
	protected void addSAMRecordWrappers(final int referencePosition, final int readPosition, final int length, 
			final SAMRecordWrapper recordWrapper) {

		if (referencePosition < 0) {
			throw new IllegalArgumentException("Reference Position cannot be < 0! -> outside of alignmentBlock");
		}

		final WindowPositionGuard windowPositionGuard = coordinateController.convert(referencePosition, readPosition, length);
		
		if (windowPositionGuard.getWindowPosition() < 0 && windowPositionGuard.getLength() > 0) {
			throw new IllegalArgumentException("Window position cannot be < 0! -> outside of alignmentBlock");
		}
		
		for (int j = 0; j < windowPositionGuard.getLength(); ++j) {
			recordWrappers.get(windowPositionGuard.getWindowPosition() + j).add(recordWrapper);
		}
	}

	@Override
	public void clear() {
		for (final List<SAMRecordWrapper> r : recordWrappers) {
			r.clear();
		}
	}

	public List<SAMRecordWrapper> getRecordWrapper(final int windowPosition) {
		return recordWrappers.get(windowPosition);
	}

	@Override
	public void addRecordWrapperRegion(int readPosition, int length, SAMRecordWrapper recordWrapper) {
		final int referencePosition = recordWrapper.getSAMRecord().getReferencePositionAtReadPosition(readPosition) - 1;
		addSAMRecordWrappers(referencePosition, readPosition, length, recordWrapper);
	}

}
