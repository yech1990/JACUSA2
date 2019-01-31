package jacusa.method.lrtarrest;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import lib.stat.AbstractStatFactory;

/**
 * TODO implement this test-statistic
 * 
 * @param 
 */
public class LRTarrestStatFactory 
extends AbstractStatFactory {

	private final static String NAME = "LRTstat"; 
	private final static String DESC = "Combined BetaBin and DirMult";
	
	public LRTarrestStatFactory() {
		super(Option.builder(NAME)
				.desc(DESC)
				.build());
	}

	@Override
	public void processCLI(final String line) {}

	@Override
	public void processCLI(CommandLine cmd) {}
	
	@Override
	protected Options getOptions() {
		return new Options();
	}

	@Override
	public LRTarrestStat newInstance(final int conditions) {
		return new LRTarrestStat(this);
	}

}
