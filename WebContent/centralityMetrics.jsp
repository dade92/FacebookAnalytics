<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%
    	//recupera le metriche di centralità ricavate dalla servlet
 		String degree=request.getParameter("degree");
		String close=request.getParameter("closenness");
		String between=request.getParameter("betwenness");
		String normalizedBet=request.getParameter("norBet");
		String normalizedDegree=request.getParameter("normalizedDegree");
		String normalizedClosennes=request.getParameter("normalizedClosenness");
		String name=request.getParameter("name");
		String id=request.getParameter("id");
		String urlImage="http://graph.facebook.com/"+id+"/picture";
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><%=name %></title>
<!-- Bootstrap -->
<link href="css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
	<!-- stampa le metriche di centralità -->
	<img alt="immagine" class="img-thumbnail" src=<%=urlImage %>>
	<br>
	<table class="table table-striped table-bordered">
		<tr>
			<td>degree centrality metrics=<%=degree %></td>
			<td>normalized degree centrality metric=<%=normalizedDegree %></td>
		</tr>

		<tr>
			<td>closenness centrality metric=<%=close %></td>
			<td>normalized closenness centrality metric=<%=normalizedClosennes %></td>
		</tr>

		<tr>
			<td>betweenness centrality metric=<%=between %></td>
			<td>normalized betwenness centrality metric=<%=normalizedBet %></td>
		</tr>

	</table>

</body>
</html>