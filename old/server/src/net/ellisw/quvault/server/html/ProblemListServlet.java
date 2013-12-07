package net.ellisw.quvault.server.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ellisw.quvault.server.database.Database;
import net.ellisw.quvault.server.database.ProblemHeaderRow;


public class ProblemListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        PrintWriter w = resp.getWriter();
        
        w.println("<html><body>");
        
        w.println("<table>");
        w.println("<tr><th>Author</th><th>Problem Title</th></tr>");
        Database db = new Database();
        List<ProblemHeaderRow> rows = db.getProblemHeaderRows();
        for (ProblemHeaderRow row : rows) {
        	w.print("<tr><td>" + row.getAuthor() + "</td>");
        	w.print("<td><a href='problem/" + row.getSource() + "/" + row.getSourceUri() + "'>" + row.getTitle() + "</a> - " + row.getKeywords() + "</td>");
        	w.println("</tr>");
        }
        w.println("</table>");
        w.println("</body></html>");
    }
}
