package net.ellisw.quvault.client;

import java.util.List;

import net.ellisw.quvault.core.ProblemHeader;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("viewdata")
public interface ViewdataService extends RemoteService {
	List<ProbsetIndex> getProbsetIndexes();
	ProbsetViewdata getProbsetViewdata(String probsetId);
	ProblemViewdata getProblemViewdata(String problemId, int[] args);
	ProblemViewdata getProblemViewdata(String probsetId, int problemIndex);
	ProblemViewdata getProblemViewdataFromScript(String language, String script, int[] args);
	
	List<ProblemHeaderViewdata> getProblemHeaders();
	ProblemTemplViewdata getProblemScript(long id);
	ProblemTemplViewdata saveProblemScript(ProblemTemplViewdata script);
	ProblemParams getProblemParams(long scriptId, int i);
}
