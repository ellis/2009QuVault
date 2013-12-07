package net.ellisw.quvault.server.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ellisw.quvault.client.ProblemHeaderViewdata;
import net.ellisw.quvault.core.ProblemHeader;
import net.ellisw.quvault.server.database.PMF;
import net.ellisw.quvault.server.database.ProblemHeaderRow;
import net.ellisw.quvault.vault.ProbServer;


public class ImportLocalVaultServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        PrintWriter w = resp.getWriter();
        
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			// Delete existing local entries
			String query = "select from " + ProblemHeaderRow.class.getName() + " where source == 'local'";
			List<ProblemHeaderRow> rows = (List<ProblemHeaderRow>) pm.newQuery(query).execute();
			pm.deletePersistentAll(rows);
	        
			// Add new local entries
	        ProbServer srv = new ProbServer();
	        List<ProblemHeader> headers = srv.listProblems();
	        for (ProblemHeader header : headers) {
	        	w.println(header.title);
	        	ProblemHeaderRow row = new ProblemHeaderRow();
	        	row.setTitle(header.title);
	        	row.setAuthor(header.author);
	        	row.setKeywords(header.keywords);
	        	row.setSource("local");
	        	row.setSourceUri(header.uri);
				pm.makePersistent(row);
	        }
		}
		finally {
			pm.close();
		}
    }
}
