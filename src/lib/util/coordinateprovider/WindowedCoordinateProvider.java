package lib.util.coordinateprovider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lib.cli.options.ThreadWindowSizeOption;
import lib.util.Coordinate;

public class WindowedCoordinateProvider implements CoordinateProvider {

	private Iterator<Coordinate> it;
	private int total;
	
	public WindowedCoordinateProvider(final Coordinate coordinate, final int windowSize) {
		final List<Coordinate> coordinates = makeWindows(coordinate, windowSize);
		total = coordinates.size();
		it = coordinates.iterator();
	}
	
	public WindowedCoordinateProvider(final CoordinateProvider cp, final int windowSize) {
		final List<Coordinate> coordinates = create(cp, windowSize);
		total = coordinates.size();
		it = coordinates.iterator();
	}
	
	public boolean hasNext() {
		return it.hasNext();
	}
	
	public Coordinate next() {
		return it.next();
	}
	
	@Override
	public void close() throws IOException {
		// not needed
	}

	public int getTotal() {
		return total;
	}

	private List<Coordinate> create(final CoordinateProvider cp, final int windowSize) {
		final List<Coordinate> coordinates = new ArrayList<Coordinate>(cp.getTotal());
		while (cp.hasNext()) {
			final Coordinate coordinate = cp.next();
			final List<Coordinate> windowedCoordinates = makeWindows(coordinate, windowSize);
			if (! windowedCoordinates.isEmpty()) {
				coordinates.addAll(windowedCoordinates);
			}
		}
		return coordinates;
	}
	
	private List<Coordinate> makeWindows(final Coordinate coordinate, final int windowSize) {
		if (windowSize == ThreadWindowSizeOption.NO_WINDOWS) {
			final List<Coordinate> coordinates = new ArrayList<Coordinate>(1);
			coordinates.add(new Coordinate(coordinate));
			return coordinates;
		}
		
		final int length = coordinate.getEnd() - coordinate.getStart() + 1;
		final int n = length / windowSize;
		final List<Coordinate> coordinates = new ArrayList<Coordinate>(n < 0 ? 1 : n);
		
		int start = coordinate.getStart();
		while (start < coordinate.getEnd()) {
			final int end = Math.min(start + windowSize - 1, coordinate.getEnd());
			coordinates.add(new Coordinate(coordinate.getContig(), start, end));
			start += windowSize;
		}

		return coordinates;
	}
	
}
