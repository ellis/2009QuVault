package net.ellisw.quvault.client;

import java.util.List;

import net.ellisw.quvault.core.ProblemHeader;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ViewdataServiceAsync {
	void getProbsetIndexes(AsyncCallback< List<ProbsetIndex> > callback);
	void getProbsetViewdata(String probsetId, AsyncCallback<ProbsetViewdata> callback);
	void getProblemViewdata(String problemId, int[] args, AsyncCallback<ProblemViewdata> callback);
	void getProblemViewdata(String problemId, int problemIndex, AsyncCallback<ProblemViewdata> callback);
	void getProblemViewdataFromScript(String language, String script, int[] args, AsyncCallback<ProblemViewdata> callback);
	
	void getProblemHeaders(AsyncCallback< List<ProblemHeaderViewdata> > callback);
	void getProblemScript(long id, AsyncCallback<ProblemTemplViewdata> callback);
	void saveProblemScript(ProblemTemplViewdata script, AsyncCallback<ProblemTemplViewdata> callback);
	
	void getProblemParams(long scriptId, int i, AsyncCallback<ProblemParams> callaback);
}
