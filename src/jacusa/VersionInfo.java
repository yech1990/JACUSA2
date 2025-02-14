package jacusa;

import htsjdk.samtools.util.StringUtil;

/**
 * This class stores version info and optional branch information.
 */
public final class VersionInfo {

	public static final String BRANCH 	= "master";
	public static final String TAG 	= "2.0.3-RC";

	// change this manually
	public static final String[] LIBS	= new String[] {
			"htsjdk 2.12.0",
			"Apache commons-cli 1.4",
			"Apache commpon-math3 3.6.1"
	};

	private final String branch;
	private final String tag;
	private final String[] libs;
	
	public VersionInfo() {
		this(BRANCH, TAG, LIBS);
	}
	
	public VersionInfo(final String branch, final String tag, final String[] libs) {
		this.branch = branch;
		this.tag 	= tag;
		this.libs	= libs;
	}
	
	public String formatVersion() {
		final StringBuilder sb = new StringBuilder();
		sb.append(tag);
		
		// only add branch when not on master
		if (! branch.equals("master")) {
			sb.append(" (");
			sb.append(branch);
			sb.append(')');
		}
		
		return sb.toString();
	}
	
	public String formatLibs() {
		return StringUtil.join(", ", libs);
	}
	
}
