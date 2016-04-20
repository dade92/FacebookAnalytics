<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@page import="java.sql.*"%>

<%		
		//recupera gli i id e i nomi degli amici che ho selezionato
    	String ids[]=request.getParameterValues("id");
    	String names[]=request.getParameterValues("name");
    	//costruisce il link per il grafo
    	String url="facebookGraph.jsp?"+request.getQueryString();
    	//recupera la connessione al database
    	Connection con=(Connection) session.getAttribute("connection");
    	//prepara lo statement per sottomettere la query
    	PreparedStatement st=con.prepareStatement("select u2.name from (user as u1 "+
    			"join friendship as f on u1.iduser=f.iduser) " +
    			"join user as u2 on f.idamico=u2.iduser where u1.iduser=?" );
    	//riferimento a ResultSet che conterrà le informazioni della query
    	ResultSet set;
    
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<!-- Bootstrap -->
<!-- serve jQuery e lo script javascript di bootstrap per implementare la tendina degli amici -->
<script type="text/javascript"
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
<link href="css/bootstrap.min.css" rel="stylesheet">
<script type="text/javascript"
	src="http://localhost:8080/progetto/js/bootstrap.js"></script>


<title>mutual friends</title>
</head>
<body>

	<!-- Static navbar -->
	<div class="navbar navbar-default navbar-static-top" role="navigation">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="index.html">Facebook analytics
					project</a>
			</div>
			<div class="navbar-collapse collapse">
				<ul class="nav navbar-nav">
					<li class="active"><a href="init.jsp">userinfo</a></li>
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li><a href="LogoutServlet">Logout</a></li>
				</ul>
			</div>
			<!--/.nav-collapse -->
		</div>
	</div>

	<!-- link al grafo degli amici -->
	<div style="position: relative; left: 100px;">
		<a class="btn btn-primary btn-lg" href=<%=url %>>see graph</a>
		<!-- stampa la lista degli amici con i loro amici in comune -->
		<!-- utilizza un div con scrollBar -->
		<div
			style="width: 500px; height: 700px; line-height: 3em; overflow: scroll; padding: 10px;">
			<div class="panel-group" id="accordion">
				<%//per ogni amico
			for(int i=0;i<names.length;i++) {
						//imposta l'id nella query
		st.setString(1, ids[i]);
		//esegue la query:recupero gli amici mutuali, che ho nel database
		set=st.executeQuery();
		%>
				<div class="panel panel-default">
					<div class="panel-heading">
						<h4 class="panel-title">
							<a data-toggle="collapse" data-parent="#accordion"
								href=<%=("#"+ids[i]) %>> <%=names[i] %>
							</a>
						</h4>
					</div>
					<div id=<%=ids[i] %> class="panel-collapse collapse">
						<div class="panel-body">
							<ul>
								<% while(set.next()){ %>
								<li class="list-group-item active"><%=set.getString(1) %></li>
								<% } %>
							</ul>
						</div>
					</div>
				</div>
				<% } %>
			</div>
		</div>
	</div>
</body>
</html>