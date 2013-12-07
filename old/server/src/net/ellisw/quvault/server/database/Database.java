package net.ellisw.quvault.server.database;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;

import net.ellisw.quvault.core.ProblemHeader;

public class Database {
	/*public List<ProblemHeader> getProblemHeaders() {
		List<ProblemHeader> list = new ArrayList<ProblemHeader>();
		List<ProblemHeaderRow> rows = getProblemHeaderRows();
		for (ProblemHeaderRow row : rows) {
			ProblemHeader header = createProblemHeader(row);
			list.add(header);
		}
		return list;
	}*/

	@SuppressWarnings("unchecked")
	public List<ProblemHeaderRow> getProblemHeaderRows() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		String query = "select from " + ProblemHeaderRow.class.getName() + " range 0,100";
		List<ProblemHeaderRow> rows = (List<ProblemHeaderRow>) pm.newQuery(query).execute();
		return rows;
	}
	/*
	private ProblemHeader createProblemHeader(ProblemHeaderRow row) {
		ProblemHeader header = new ProblemHeader();
		header.uri = row.getUrl();
		header.author = row.getAuthor();
		header.title = row.getTitle();
		if (header.title == null)
			header.title = "<NO TITLE>";
		header.keywords = row.getKeywords();
		if (header.keywords == null)
			header.keywords = "<NO KEYWORDS>";
		return header;
	}
	*/
}
