package test;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.User;

/*
 * per includere la libreria di facebook
 * prima la aggiungo al classhpath,poi alla cartella web-inf/lib
 */

/**
 * Servlet implementation class InitServlet prima servlet invocata dopo il
 * redirect di facebook:reucpera un oggetto facebook utile per la connessione
 * con il social network e memorizza il javaBean dell'utente correntemente
 * loggato e l'oggetto facebook in sessione
 */

public class InitServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/*
	 * variabili in cui memorizzo l'app_id e il secret_code dell'applicazione
	 */
	private String APP_ID;
	private String SECRET_CODE;

	// oggetto Facebook che mi permette di usare le api
	private Facebook facebook;
	// javaBean per l'utente
	private FacebookUser user;
	// factory che crea l'oggetto Facebook
	private FacebookFactory factory;

	public void init(ServletConfig config) {

		try {
			super.init(config);
			// inizializzo il javaBean
			user = new FacebookUser();
			// creo l'oggetto per interfacciarsi con Facebook
			factory = new FacebookFactory();

			// recupera l'oggetto ServletContext
			ServletContext context = config.getServletContext();

			// recupera i parametri di contesto necessari
			APP_ID = context.getInitParameter("id");
			SECRET_CODE = context.getInitParameter("secret-code");
		} catch (ServletException e) {
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
		// TODO Auto-generated method stub

		// recupera il codice inviato da facebook
		String code = request.getParameter("code");

		// crea una nuova sessione se non esiste ancora
		HttpSession session = request.getSession(true);

		// utilizza le api di facebook4j
		try {
			// recupera l'oggetto Facebook dalla sessione
			facebook = (Facebook) session.getAttribute("facebook");
			// se non l'ho ancora creato
			if (facebook == null) {
				// crea nuovo oggetto per la connessione facebook
				facebook = factory.getInstance();

				// setta l'app id e il secret code dell'app
				facebook.setOAuthAppId(APP_ID, SECRET_CODE);

				// richiede l'access token
				facebook.getOAuthAccessToken(code,
						"http://localhost:8080/progetto/InitServlet");
				// memorizza l'oggetto facebook nella sessione
				session.setAttribute("facebook", facebook);
			}
			// recupera informazioni sull'utente correntemente loggato
			User me = facebook.getMe();

			/*
			 * crea il java bean dell'utente
			 */
			user.setId(me.getId());
			user.setName(me.getName());
			user.setLocation(me.getLocation());
			user.setLink(me.getLink().toString());
			user.setGender(me.getGender());
			user.setPicture(me.getPicture());

			// aggiunge il java bean alla sessione
			session.setAttribute("user", user);
			// fa redirect verso la prima jsp
			response.sendRedirect("init.jsp");

		} catch (FacebookException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// in caso di post fai come la get
		this.doGet(request, response);
	}

}
