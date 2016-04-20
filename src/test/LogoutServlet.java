package test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import facebook4j.Facebook;

/**
 * Servlet implementation class LogoutServlet servlet che gestisce la logica di
 * logout
 */

public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			HttpSession session = request.getSession(true);
			// recupera la connessione al database
			Connection con = (Connection) session.getAttribute("connection");
			Facebook facebook = (Facebook) session.getAttribute("facebook");
			if (con != null) {
				// crea lo statement
				Statement s = con.createStatement();
				// resetta il database
				s.executeUpdate("DELETE FROM friendship");
				s.executeUpdate("DELETE FROM centralityMetric");
				s.executeUpdate("DELETE FROM user");
			}
			// Log Out da Facebook
			// recupera l'access token
			String accessToken = facebook.getOAuthAccessToken().getToken();
			/*
			 * fa logout
			 */
			StringBuffer next = request.getRequestURL();
			int index = next.lastIndexOf("/");
			next.replace(index + 1, next.length(), "");
			// fa redirect verso la pagina di logout di facebook
			response.sendRedirect("http://www.facebook.com/logout.php?next="
					+ next.toString() + "&access_token=" + accessToken);
			// elimina la sessione
			session.invalidate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doGet(request, response);
	}

}
