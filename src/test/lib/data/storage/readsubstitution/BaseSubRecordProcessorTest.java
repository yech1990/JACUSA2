package test.lib.data.storage.readsubstitution;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import htsjdk.samtools.SAMRecord;
import lib.cli.options.filter.has.BaseSub;
import lib.data.storage.PositionProcessor;
import lib.data.storage.Storage;
import lib.data.storage.basecall.AbstractBaseCallCountStorage;
import lib.data.storage.basecall.DefaultBCCStorage;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.readsubstitution.BaseCallInterpreter;
import lib.data.storage.readsubstitution.BaseSubRecordProcessor;
import lib.data.validator.CombinedValidator;
import lib.data.validator.Validator;
import lib.record.Record;
import lib.util.Base;
import lib.util.LibraryType;
import lib.util.coordinate.OneCoordinate;
import lib.util.position.Position;
import test.utlis.ReferenceSequence;
import test.utlis.SAMRecordBuilder;
import test.utlis.SharedStorageBuilder;

@TestInstance(Lifecycle.PER_CLASS)
class BaseSubRecordProcessorTest {

	// use this contig from ReferenceSequence to generate reads from
	private static final String CONTIG = "BaseSubstitutionTest";
	
	@ParameterizedTest(name = "{4}")
	@MethodSource("testProcess")
	void testProcess(
			BaseSubRecordProcessor testInstance,
			List<Record> records, 			// currently this is only Single End
			Map<BaseSub, Storage> actual,	// reference to internal map of testInstance
			Map<BaseSub, Storage> expected,	// this is what we expect
			String info 					// JUNIT related; gives more informative test message
			) {
		
		testInstance.preProcess();
		// let testInstance process reads
		for (final Record record : records) {
			testInstance.process(record);
		}
		testInstance.postProcess();
		assertEquals(expected.keySet(), actual.keySet());
		
		for (final BaseSub baseSub : expected.keySet()) {
			final AbstractBaseCallCountStorage expectedBccStorage = 
					(AbstractBaseCallCountStorage)expected.get(baseSub);
			final int winSize = expectedBccStorage.getCoordinateController().getActiveWindowSize();
			final AbstractBaseCallCountStorage actualBccStorage = 
					(AbstractBaseCallCountStorage)actual.get(baseSub);
			
			for (int winPos = 0; winPos < winSize; ++winPos) {
				for (final Base base : Base.validValues()) {
					assertEquals(
							expectedBccStorage.getCount(winPos, base), 
							actualBccStorage.getCount(winPos, base),
							String.format("baseSub: %s winPos: <%d> base: <%s>",
									baseSub, winPos, base) );
				}
			}
		}
	}
	
	Stream<Arguments> testProcess() {
		// 012345678901
		// ACGAACGTACGT ref.
		// 123456789012
		final List<Arguments> args = new ArrayList<>();
		final LibraryType[] libs = 
				new LibraryType[] { 
						LibraryType.UNSTRANDED, 
						LibraryType.RF_FIRSTSTRAND, 
						LibraryType.FR_SECONDSTRAND };
		
		// for stranded libs and negativeStrand base calls need to be inverted 
		// this is done in AbstractBaseCallCountStorage.populate()
		for (final LibraryType lib : libs) {
			for (final boolean negativeStrand : new boolean[] { true, false } ) {
				args.add(cSE(
						lib, 
						1, 7,
						1, negativeStrand, "3M", "",
						new HashSet<BaseSub>(),
						new String[] {} ) );
			}
			args.add(cSE(
					lib, 
					1, 7,
					1, false, "3M", "ATG", // ref:ACG
					new HashSet<BaseSub>(Arrays.asList(BaseSub.C2T)),
					new String[] { "C2T,0,A", "C2T,1,T", "C2T,2,G" } ));
		}
		/*
		args.add(cSE(
				LibraryType.UNSTRANDED, 
				1, 7,
				1, true, "3M", "ATG", // ref:ACG
				new String[] { "C2T,0,A", "C2T,1,T", "C2T,2,G" } ));
		args.add(cSE(
				LibraryType.RF_FIRSTSTRAND, 
				1, 7,
				1, true, "3M", "ATG", // ref:ACG
				new String[] { "G2A,0,A", "G2A,1,T", "G2A,2,G" } ));
		args.add(cSE(
				LibraryType.FR_SECONDSTRAND, 
				1, 7,
				1, true, "3M", "ATG", // ref:ACG
				new String[] { "G2A,0,A", "G2A,1,T", "G2A,2,G" } )); */
		// 012345678901
		// ACGAACGTACGT ref.
		// TGCTTGCATGCA reverese.
		// 123456789012
		//     [  ]
		//     >><<
		//     <<  >>
		//  <<   >>
		//  <<  >>
		//      <<  >>
		// ACGAACGTACGT
		// 123456789012
		//      <<
		//       >>
		args.add(cPE(
				LibraryType.UNSTRANDED, 
				5, 8,
				5, true, "2M", "AT", // ref:AC
				9, true, "2M", "AC",
				new HashSet<BaseSub>(Arrays.asList(BaseSub.C2T)),
				new String[] {"C2T,0,A","C2T,1,T" } ));
		args.add(cPE(
				LibraryType.UNSTRANDED, 
				5, 8,
				5, true, "2M", "TC", // ref:AC
				8, true, "2M", "AC", // ref:TA
				new HashSet<BaseSub>(Arrays.asList(BaseSub.A2T)),
				new String[] { "A2T,0,T" ,"A2T,1,C", "A2T,3,A"} ));
		args.add(cPE(
				LibraryType.UNSTRANDED, 
				5, 8,
				4, true, "2M", "AT", // ref:AA
				9, true, "2M", "AC",
				new HashSet<BaseSub>(Arrays.asList(BaseSub.A2T)),
				new String[] {"A2T,0,T" } ));
		args.add(cPE(
				LibraryType.UNSTRANDED, 
				5, 8,
				4, true, "2M", "AT", // ref:AA
				1, true, "2M", "AC",
				new HashSet<BaseSub>(Arrays.asList(BaseSub.A2T)),
				new String[] {"A2T,0,T" } ));
		// ACGAACGTACGT
		// 123456789012
		//     [  ]
		//    AT
		//     AC
		args.add(cPE(
				LibraryType.UNSTRANDED, 
				5, 8,
				4, true, "2M", "AT", // ref:AA
				5, true, "2M", "AC",
				new HashSet<BaseSub>(Arrays.asList(BaseSub.A2T)),
				new String[] {"A2T,0,A", "A2T,0,T", "A2T,1,C" } ));
		return args.stream();
	}

	// creates arguments for the test
	// this is a convenience method to have an empty validator 
	Arguments cSE(
			final LibraryType libraryType,
			final int refWinStart, final int refWinEnd, 
			final int refStart, final boolean negativeStrand, final String cigarStr, final String readSeq,
			final Set<BaseSub> queryBaseSub,
			final String[] expectedStr) {
		
		return cSE(
				libraryType, 
				refWinStart, refWinEnd, 
				refStart, negativeStrand, cigarStr, readSeq, 
				new CombinedValidator(new ArrayList<Validator>()),
				queryBaseSub,
				expectedStr);
	}
	
	Arguments cSE(
			final LibraryType libraryType,
			final int refWinStart, final int refWinEnd,
			final int refStart, final boolean negativeStrand, final String cigarStr, final String readSeq, 
			final Validator validator,
			final Set<BaseSub> queryBaseSubs,
			final String[] expectedStr) {
		
		// simulate Single End Read
		final List<Record> records = simulateSEreads(refStart, negativeStrand, cigarStr, readSeq);
		
		// make nice informative message to output along the test 
		final String info = info(libraryType, records);

		// holds some important data, e.g.: current window, reference info, stuff should be shared
		final SharedStorage sharedStorage = createSharedStorage(refWinStart, refWinEnd, libraryType);

		final BaseCallInterpreter bci = BaseCallInterpreter.build(libraryType);
		final Map<BaseSub, Storage> expected = parseExpected(expectedStr, sharedStorage);
		final Map<BaseSub, Storage> actual = new EnumMap<>(BaseSub.class);
		
		return Arguments.of(
				createTestInstance(bci, sharedStorage, validator, queryBaseSubs, actual, expected),
				records,
				actual,
				expected,
				info);
	}
	
	SharedStorage createSharedStorage(final int refWinStart, final int refWinEnd, final LibraryType libraryType ) {
		final int activeWindowSize = refWinEnd - refWinStart + 1;
		// holds some important data, e.g.: current window, reference info, stuff should be shared
		return new SharedStorageBuilder(
				activeWindowSize, libraryType, CONTIG, ReferenceSequence.get())
				.withActive(new OneCoordinate(CONTIG, refWinStart, refWinEnd))
				.build();
	}
	
	List<Record> simulateSEreads(
			final int refStart, final boolean negativeStrand, final String cigarStr, final String readSeq) {
		// simulate Single End Read
		final SAMRecord record = SAMRecordBuilder.createSERead(
						CONTIG, refStart, negativeStrand, cigarStr, readSeq);
		return Arrays.asList(new Record(record));
	}
	
	// simulate Paired End Reads
	List<Record> simulatePEreads(
			final int refStart, final boolean negativeStrand, final String cigarStr, final String readSeq, 
			final int refStart2, final boolean negativeStrand2, final String cigarStr2, final String readSeq2) {
		
		return new SAMRecordBuilder()
				.withPERead(CONTIG, refStart, negativeStrand, cigarStr, readSeq, 
						refStart2, negativeStrand2, cigarStr2, readSeq2)
			
				.getRecords().stream()
				.map(r -> new Record(r, r.getFileSource().getReader()))
				.collect(Collectors.toList());
	}
	
	Arguments cPE(
			final LibraryType libraryType,
			final int refWinStart, final int refWinEnd,
			final int refStart, final boolean negativeStrand, final String cigarStr, final String readSeq, 
			final int refStart2, final boolean negativeStrand2, final String cigarStr2, final String readSeq2,
			final Set<BaseSub> queryBaseSubs,
			final String[] expectedStr) {
		
		final Validator validator = new CombinedValidator(new ArrayList<Validator>());
		
		final List<Record> records = simulatePEreads(
				refStart, negativeStrand, cigarStr, readSeq, 
				refStart2, negativeStrand2, cigarStr2, readSeq2);
		
		// make nice informative message to output along the test 
		final String info = info(libraryType, records);

		// holds some important data, e.g.: current window, reference info, stuff should be shared
		final SharedStorage sharedStorage = createSharedStorage(refWinStart, refWinEnd, libraryType);

		final BaseCallInterpreter bci = BaseCallInterpreter.build(libraryType);
		final Map<BaseSub, Storage> expected = parseExpected(expectedStr, sharedStorage);
		final Map<BaseSub, Storage> actual = new EnumMap<>(BaseSub.class);
		
		return Arguments.of(
				createTestInstance(bci, sharedStorage, validator, queryBaseSubs, actual, expected),
				records,
				actual,
				expected,
				info);
	}
		
	String info(final LibraryType libraryType, List<Record> records) {
		if (records.size() == 1) {
			final SAMRecord record = records.get(0).getSAMRecord();
			return String.format("Lib.: %s, %d-%d %s", libraryType, record.getAlignmentStart(), record.getAlignmentEnd(), record.getCigar());

		} else if (records.size() == 2) {
			final SAMRecord record1 = records.get(0).getSAMRecord();
			final SAMRecord record2 = records.get(1).getSAMRecord();
			return String.format("Lib.: %s, %d-%d %s %d-%d %s", 
					libraryType, 
					record1.getAlignmentStart(), record1.getAlignmentEnd(), record1.getCigar(),
					record2.getAlignmentStart(), record2.getAlignmentEnd(), record2.getCigar());
		} else {
			throw new IllegalStateException();
		}
	}
	
	BaseSubRecordProcessor createTestInstance(
			final BaseCallInterpreter bci,
			final SharedStorage sharedStorage,
			final Validator validator, 
			Set<BaseSub> queryBaseSubs,
			final Map<BaseSub, Storage> actual,
			final Map<BaseSub, Storage> expected) {
		
		if (! queryBaseSubs.equals(expected.keySet())) {
			throw new IllegalStateException();
		}
		
		final Map<BaseSub, PositionProcessor> baseSub2posProcs = 
				new EnumMap<>(BaseSub.class);
		for (final BaseSub baseSub : queryBaseSubs) {
			final Storage storage = new DefaultBCCStorage(sharedStorage, null);
			actual.put(baseSub, storage);
			final PositionProcessor posProc = new PositionProcessor();
			posProc.addValidator(validator);
			posProc.addStorage(storage);
			baseSub2posProcs.put(baseSub, posProc);
		}
		
		return new BaseSubRecordProcessor(
				sharedStorage,
				bci,
				validator,
				queryBaseSubs,
				baseSub2posProcs,
				new HashMap<>(),
				new HashMap<>(),
				new HashMap<>());
	}
	
	// ',' separated array of strings of the following form: "x2y,winPos,{A|C|G|T}" 
	// where x,y in {A,C,G,T}
	Map<BaseSub, Storage> parseExpected(final String[] str, final SharedStorage sharedStorage) {
		final Map<BaseSub, Storage> expected = new EnumMap<>(BaseSub.class);
		for (final String tmpStr : str) {
			final String[] cols 			= tmpStr.split(",");
			final BaseSub baseSub 	= BaseSub.valueOf(cols[0]);
			final int winPos 				= Integer.parseInt(cols[1]);
			final Base base					= Base.valueOf(cols[2]);
			if (! expected.containsKey(baseSub)) {
				expected.put(baseSub, new DefaultBCCStorage(sharedStorage, null));
			}
			final Storage tmp = expected.get(baseSub);
			final Position toyPos = new ToyPosition(winPos, base);
			tmp.increment(toyPos);
		}
		return expected;
	}
	
	private class ToyPosition implements Position {

		private final int winPos;
		private final Base base;
		
		public ToyPosition(final int winPos, final Base base) {
			this.winPos = winPos;
			this.base 	= base;
		}
		
		@Override
		public Position copy() {
			return new ToyPosition(winPos, base);
		}
		
		@Override
		public int getReadPosition() {
			return -1;
		}
		
		@Override
		public Record getRecord() {
			return null;
		}
		
		@Override
		public int getReferencePosition() {
			return -1;
		}
		
		@Override
		public int getWindowPosition() {
			return winPos;
		}
		
		@Override
		public boolean isValidRefPos() {
			return true;
		}
		
		@Override
		public Base getReadBaseCall() {
			return base;
		}
		
		@Override
		public SAMRecord getSAMRecord() {
			return null;
		}
		
	}
	
}
