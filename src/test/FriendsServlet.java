package test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.Friend;
import facebook4j.ResponseList;

/**
 * Servlet implementation class DatabaseServlet recupera la lista dei miei amici
 * e salva le relazioni in un database. fa redirect verso una jsp che me li
 * visualizza
 */

public class FriendsServlet extends HttpServlet {

	// costanti che memorizzano nome del driver e url database
	private String DRIVER = "";
	private String DB_URL = "";
	private String user;
	private String password;
	private static final long serialVersionUID = 1L;

	// oggetti per la connessione al database
	private Connection con;
	private PreparedStatement st;
	private PreparedStatement queryStatement;
	private PreparedStatement st2;

	// oggetto per richiedere amici di facebook
	private Facebook facebook;

	/**
	 * alla prima richiesta inizializza i parametri e carica la classe del
	 * Driver
	 */
	public void init(ServletConfig config) {

		try {
			super.init(config);
			// recupera l'oggetto ServletContext
			ServletContext context = config.getServletContext();

			// recupera i parametri di contesto necessari(alla prima richiesta)
			DRIVER = context.getInitParameter("driverDB");
			DB_URL = context.getInitParameter("db_url");
			user = context.getInitParameter("user");
			password = context.getInitParameter("password");
			// carica il driver del database
			Class.forName(DRIVER);

		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stud
		// oggetti per lista amici e sessione
		ResponseList<Friend> friends = null;
		HttpSession session = null;

		try {
			/*
			 * recupera una connessione al database e crea i preparedStatement
			 * per inserire le relazioni nel database
			 */
			// recupera la sessione
			session = request.getSession(true);
			// richiede la connessione alla sessione
			con = (Connection) session.getAttribute("connection");
			// se non c'è, perchè è la prima richiesta fatta da questo utente,la
			// crea
			if (con == null)
				con = DriverManager.getConnection(DB_URL, user, password);
			// crea i PreparedStatement
			st = con.prepareStatement("INSERT INTO user VALUES (?,?)");
			st2 = con.prepareStatement("INSERT INTO friendship VALUES (?,?)");
			// id che non devo inserire perchè ho gia relazioni di amicizia
			queryStatement = con
					.prepareStatement("SELECT user.iduser FROM user WHERE user.iduser=?");
			// salva la connessione al database nella sessione
			session.setAttribute("connection", con);
			// recupera l'utente della sessione
			FacebookUser user = (FacebookUser) session.getAttribute("user");
			// recupera l'oggetto facebook utilizzato
			facebook = (Facebook) session.getAttribute("facebook");
			// setta la query
			queryStatement.setString(1, user.getId());
			// recupera il resultset della query
			ResultSet set = queryStatement.executeQuery();
			// recupera gli amici da facebook
			friends = facebook.getFriends();
			// se non ho risultati nel database,li aggiungo
			if (!set.next()) {
				// aggiunge gli amici al database
				this.addToDatabase(friends, user.getId(), user.getName());
			}

		} catch (FacebookException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// aggiungo la lista alla sessione
		session.setAttribute("friends", friends);
		// fa redirect verso la jsp
		response.sendRedirect("friends.jsp");
	}

	/**
	 * chiude la connessione
	 * 
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	public void destroy() {
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * metodo che aggiunge gli amici al database
	 * 
	 * @param friends
	 *            lista degli amici
	 * @param myName
	 *            nome dell'utente
	 * @throws SQLException
	 *             se non riesce a fare l'update del database
	 */
	private void addToDatabase(List<Friend> friends, String id, String name)
			throws SQLException {
		/*
		 * per ogni amico scrive il suo nome e il suo id nel database usando un
		 * oggetto PreparedStatement
		 */
		// scrive l'utente
		st.setString(1, id);
		st.setString(2, name);
		st.addBatch();
		// scrive i suoi amici
		for (Friend f : friends) {
			String friendId = f.getId();
			st.setString(1, friendId);
			st.setString(2, f.getName());
			st2.setString(1, id);
			st2.setString(2, friendId);
			// aggiungo al batch l'update
			st.addBatch();
			st2.addBatch();
		}
		// salva gli utenti
		st.executeBatch();
		// memorizza le relazioni di amicizia
		st2.executeBatch();

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
