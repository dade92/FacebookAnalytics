package test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import facebook4j.*;

import java.sql.*;

/**
 * Servlet implementation class MutualFriendsServlet recupera gli amici dei miei
 * amici passo gli amici tramite query string la classe recupera id e nome e
 * memorizza in database le relazioni con gli amici degli amici. fa redirect
 * verso una jsp che visualizza la lista
 */

public class MutualFriendsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// oggetto per connessione a facebook
	private Facebook facebook;

	/*
	 * oggetti per connessione al database: connection recuperata dalla
	 * sessione, PreparedStatement per sottomettere le query e per verificare se
	 * ho già inserito gli amici mutuali di un dato amico l'updateStatement
	 * serve invece per aggiornare il database inserendo gli amici mutuali
	 */
	private Connection con;
	private PreparedStatement updateStatement;
	private PreparedStatement checkMutuals;
	private ResponseList<Friend> friends;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		boolean updated = true;
		// variabile che contiene il nome del mio amico con dato id
		String friendName = "";
		// variabile che rappresenta l'url del redirect che costruisco
		// redirect verso la jsp
		String url = "mutualfriends.jsp?";

		try {
			// recupera i parametri passati,cioè gli id degli amici
			String id[] = request.getParameterValues("friend");
			// se ne ho qualcuno
			if (id != null) {
				// recupera la sessione
				HttpSession session = request.getSession(true);
				// recupera l'oggetto della sessione che si occupa
				// dell'interazione
				// con facebook
				facebook = (Facebook) session.getAttribute("facebook");
				// recupera la Connection dalla sessione
				con = (Connection) session.getAttribute("connection");
				// recupera la lista degli amici
				friends = (ResponseList<Friend>) session
						.getAttribute("friends");
				/*
				 * prepara 2 Statement:updatestatement per inserimento degli
				 * amici e l'altro che controllase ci sono già gli amici mutuali
				 */
				// statement che si occupa di aggiornare il database
				updateStatement = con
						.prepareStatement("INSERT INTO friendship VALUES (?,?)");
				// statement che controlla se ho gia inserito informazioni su
				// quell' amico
				checkMutuals = con
						.prepareStatement("SELECT iduser FROM friendship WHERE iduser=?");
				// per ogni id passatogli
				for (String i : id) {

					// trova il nome dell'amico con dato id
					for (Friend f : friends) {
						// se trova l'amico con quell'id
						if (f.getId().equals(i)) {
							// memorizza il suo nome
							friendName = f.getName();
							// esce dal ciclo
							break;
						}
					}// fine for interno
						// costruisce il redirect url:nella query string ci sono
						// gli id e i nomi degli amici da visualizzare
					url += "id=" + i + "&" + "name=" + friendName + "&";
					// setta la query per capire se ci sono gia gli amici
					// mutuali con l'id dell'amico
					checkMutuals.setString(1, i);
					// se il resultSet è vuoto,allora recupero gli amici e
					// salvo la relazione di amicizia
					if (!checkMutuals.executeQuery().next()) {
						updated = false;
						// recupera i suoi amici
						ResponseList<Friend> list = facebook
								.getMutualFriends(i);
						// per ogni amico del mio amico
						for (Friend f : list) {
							// prepara l'updateStatement
							updateStatement.setString(1, i);
							updateStatement.setString(2, f.getId());
							// lo aggiunge al batch
							updateStatement.addBatch();
						}
					}// fine if
				}// fine for
					// se il database risulta non aggiornato
				if (!updated) {
					// eseguo l'update
					updateStatement.executeBatch();
				}
				// fa redirect verso la jsp
				response.sendRedirect(url);
			}
		} catch (FacebookException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

}
