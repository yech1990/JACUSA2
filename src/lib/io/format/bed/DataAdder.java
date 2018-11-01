package lib.io.format.bed;

import lib.data.result.Result;

public interface DataAdder {
	
	void addHeader(StringBuilder sb, int conditionIndex, int replicateIndex);
	void addData(StringBuilder sb, int valueIndex, int conditionIndex, int replicateIndex, Result result);
	
}
