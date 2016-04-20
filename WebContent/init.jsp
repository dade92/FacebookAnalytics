<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="test.FacebookUser"%>
<jsp:useBean id="user" scope="session" class="test.FacebookUser"></jsp:useBean>
<%//costruisce l'url dell'immagine dell'utente 
String urlUserImage="http://graph.facebook.com/"+user.getId()+"/picture";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="css/init.css">
<!-- Bootstrap -->
<link href="css/bootstrap.min.css" rel="stylesheet">
<title>Home Page</title>
</head>
<body>
	<!-- jsp iniziale -->

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
	<!-- div che visualizza le informazioni dell'utente correntemente loggato -->
	<!-- inserisce una tabella "striped" con i dati dell'utente -->
	<div class=utente class="panel panel-default">
		<table class="table table-striped">
			<tr>
				<td>id:</td>
				<td><jsp:getProperty property="id" name="user" /></td>
			</tr>
			<tr>
				<td>name:</td>
				<td><jsp:getProperty property="name" name="user" /></td>
			</tr>
			<tr>
				<td>link:</td>
				<td><a href=<jsp:getProperty property="link" name="user" />
					target="_blank"><jsp:getProperty property="link" name="user" /></a></td>
			</tr>
			<tr>
				<td>gender:</td>
				<td><jsp:getProperty property="gender" name="user" /></td>
			</tr>
			<tr>
				<td>image:</td>
				<td><img src=<%=urlUserImage %> alt="user image"
					class="img-thumbnail"></td>
			</tr>
			<tr>
				<td></td>
			</tr>
		</table>
		<!-- link alla pagina friends -->
		<a class="btn btn-primary btn-lg"
			href="http://localhost:8080/progetto/Friends">getFriends</a>
	</div>
	<br>
	<br>
</body>
</html>