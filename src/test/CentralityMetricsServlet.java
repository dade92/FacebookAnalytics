package test;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import facebook4j.Friend;

/**
 * Servlet implementation class CentralityMetrics servlet che chiama facebook
 * per recuperare le metriche di centralità . fa redirect verso la jsp che si
 * occupa di visualizzarle
 */

public class CentralityMetricsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection con;
	private// stringhe in cui memorizzo le metriche
	String degreeMetric, closennessMetric, betwennessMetric,
			normalizedBetwenness, normalizedDegree, normalizedClosenness;
	private List<Friend> myFriends;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// numero di amici dell'utente
		int numFriends;
		// numero amici dell'amico
		int numMutualFriends;
		// id dell'utente che ho cliccato nel grafo e nome
		String id = "", name = "";
		// url per fare redirect
		String url = "centralityMetrics.jsp?";

		try {

			// recupera la sessione
			HttpSession session = request.getSession();
			// recupero la lista di amici dalla sessione
			myFriends = (List<Friend>) session.getAttribute("friends");
			// recupera dalla sessione il java bean dell'utente
			FacebookUser user = (FacebookUser) session.getAttribute("user");
			// recupera la connessione al database
			con = (Connection) session.getAttribute("connection");
			// creo un preparedStatement
			PreparedStatement s = con
					.prepareStatement("INSERT INTO centralitymetric VALUES (?,?,?,?,?,?,?)");
			// prepara lo statement per verificare se ho gia inserito le
			// metriche
			PreparedStatement query = con
					.prepareStatement("SELECT iduser FROM centralitymetric WHERE iduser=?");
			// creo un preparedStatement per recuperare il numero di amici
			// dell'utente
			PreparedStatement numFriendsQuery = con
					.prepareStatement("SELECT count(*) FROM friendship WHERE iduser=?");
			// recupera l'id dalla query string
			id = request.getParameter("id");
			// sottomette la query
			numFriendsQuery.setString(1, id);
			ResultSet set = numFriendsQuery.executeQuery();
			set.next();
			// trova il numero degli amici dell'utente
			numFriends = myFriends.size();
			// memorizza il numero di amici mutuali dell'amico selezionato
			numMutualFriends = set.getInt(1) + 1;

			/*
			 * trovo il nome dell'amico
			 */
			for (Friend f : myFriends) {
				// se trova l'amico con quell'id
				if (f.getId().equals(id)) {
					// memorizza il suo nome
					name = f.getName();
					// esce dal ciclo
					break;
				}
			}// fine for
				// se non ha trovato il nome,allora è l'utente
			if (name.equals(""))
				name = user.getName();
			/*
			 * calcolo le metriche chiamando metodi della classe: passo ai
			 * metodi il numero di amici mutuali e il numero degli amici
			 * dell'utente
			 */

			this.degree(numMutualFriends, numFriends);
			this.closenness(numMutualFriends, numFriends);
			this.betwenness(numMutualFriends, numFriends);
			/*
			 * setta il PreparedStatement
			 */
			query.setString(1, id);
			// se non ho la metrica,la inserisco
			if (!query.executeQuery().next()) {
				s.setString(1, id);
				s.setString(2, degreeMetric);
				s.setString(3, normalizedDegree);
				s.setString(4, closennessMetric);
				s.setString(5, normalizedClosenness);
				s.setString(6, betwennessMetric);
				s.setString(7, normalizedBetwenness);
				// esegue l'update
				s.executeUpdate();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// costruisce l'url
		url = url + "id=" + id + "&" + "name=" + name + "&" + "degree="
				+ degreeMetric + "&" + "normalizedDegree=" + normalizedDegree
				+ "&" + "closenness=" + closennessMetric + "&"
				+ "normalizedClosenness=" + normalizedClosenness + "&"
				+ "betwenness=" + betwennessMetric + "&" + "norBet="
				+ normalizedBetwenness;
		// fa redirect verso la jsp
		response.sendRedirect(url);
	}

	/**
	 * metodo che calcola la betwenness centrality metric
	 * 
	 * @param num
	 * @param numFriends
	 */
	private void betwenness(int num, int numFriends) {
		/*
		 * calcola la betwenness: per ogni coppia di amici c'è shortest path
		 * passante per l'utente centrale. inoltre per ogni coppia di amici del
		 * nodo v c'è un shortest path che passa da lui quindi
		 * #camminipassantiperv/#camminitotali coppie di
		 * amici(=>percorsi)=n*(n-1)/2 coppie di amici mutuali dell'amico
		 * selezionato=m*(m-1)/2
		 */

		// trova le connessioni in cui è coinvolto il nodo:betwenness
		float numCouples = (num * (num - 1)) / 2;
		// converte il parametro calcolato in stringa
		betwennessMetric = String.valueOf(numCouples);
		// normalizza la metrica
		numCouples = numCouples / ((numFriends - 1) * (numFriends - 2) / 2);
		// memorizza la normalized betwenness
		normalizedBetwenness = String.valueOf(numCouples);
	}

	/**
	 * calcola la degree
	 * 
	 * @param num
	 *            numero di amici mutuali
	 * @param numFriends
	 *            numero di amici dello user
	 */
	private void degree(int num, int numFriends) {
		// calcola la degree
		normalizedDegree = String.valueOf((float) num / numFriends);
		degreeMetric = String.valueOf(num);
	}

	/**
	 * calcola la closenness
	 * 
	 * @param num
	 *            numero di amici mutuali
	 * @param numFriends
	 *            numero di amici dello user
	 */
	private void closenness(int num, int numFriends) {
		int farness = num; // ogni amico mutuale ha distanza 1
		float closenness;
		/*
		 * il calcolo della somma delle distanze è fatto sulla base del fatto
		 * che tutti gli amici dell'utente sono per forza connessi a lui
		 */
		// gli altri amici hanno distanza due
		farness += 2 * (numFriends - num);
		// calcola la closenness come 1/farness
		closenness = (float) 1 / farness;
		normalizedClosenness = String.valueOf((float) closenness
				* (numFriends - 1));
		closennessMetric = String.valueOf(closenness);
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
