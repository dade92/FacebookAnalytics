<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.List" import="facebook4j.*"
	import="test.FacebookUser" import="java.sql.*"%>
<jsp:useBean id="user" scope="session" class="test.FacebookUser"></jsp:useBean>

<%    	
	//recupera la lista degli amici in sessione
	@SuppressWarnings("unchecked")
	ResponseList<Friend> friends=(ResponseList<Friend>)session.getAttribute("friends");
%>
<%//costruisce l'url dell'immagine dell'utente 
String urlUserImage="http://graph.facebook.com/"+user.getId()+"/picture";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="css/friends.css">
<link rel="stylesheet" type="text/css" href="css/init.css">
<!-- Bootstrap -->
<link href="css/bootstrap.min.css" rel="stylesheet">
<script>
	//funzione javascript che seleziona tutti i contatti
	function selectAll(obj) {
		var check=document.getElementsByClassName("check");
		var i;
		if(obj.checked==true) {
			for(i=0;i<check.length;i++)
				check[i].checked=true;
		}
		else {
			for(i=0;i<check.length;i++)
				check[i].checked=false;
		}
	}
</script>
<title>friends</title>
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
	<!-- inserisce le informazioni dell'utente -->
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
		<br>
	</div>

	<!-- form in cui inserisco gli id e i nomi dei miei amici di facebook
		 in formato tabellare. utilizzo una scrollBar per vedere gli amici -->
	<div class=friends>
		<form class=friends class="form" method=GET
			action="http://localhost:8080/progetto/MutualFriends">
			<div class="form-group">
				<!-- link alla pagina mutualfriends -->
				<input type=submit id=button class="btn btn-default"
					value="MutualFriends"> <span class=checkbox id=checkbox>
					<label> <input type=checkbox onclick="selectAll(this)">select
						all
				</label>
				</span>
			</div>
			<div class="panel panel-default"
				style="width: 700px; height: 500px; line-height: 3em; overflow: scroll; padding: 3px;">
				<div class="panel-heading">friends</div>
				<div class="panel-body">
					<table class="table table-hover">
						<tr>
							<td></td>
							<td>id</td>
							<td>name</td>
						</tr>
						<%String id;String name;String imgUrl;
				//per ogni amico
				  for(Friend f:friends) {
					  id=f.getId();
					  name=f.getName();
					  imgUrl="http://graph.facebook.com/"+id+"/picture";
			%>
						<!-- per ogni amico:faccio nuova riga
					della tabella,e inserisco checkbox più
					l'id e il nome -->
						<tr>

							<!-- inserisce in una checkbox i valori -->
							<td><input type=checkbox class="check" name="friend"
								value=<%=id %>>
							<td><%=id%>
							<td><%=name %><br>
							<td><img alt="image" src=<%=imgUrl %> class="img-thumbnail">
						</tr>
						<%} %>
					</table>
				</div>
			</div>
		</form>
	</div>
</body>
</html>