<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="java.util.List" import="facebook4j.*"
	import="java.sql.*" import="test.FacebookUser"%>
<jsp:useBean id="user" scope="session" class="test.FacebookUser"></jsp:useBean>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%	//recupera la lista degli amici dalla sessione
	@SuppressWarnings("unchecked")
	List<Friend> friends=(List<Friend>)session.getAttribute("friends");	
	//recupera gli id degli amici che ho selezionato precedentemente
	String ids[]=request.getParameterValues("id");
	//recupera la connessione dalla sessione
	Connection con=(Connection)session.getAttribute("connection");
	//query per recuperare l'id degli amici di un amico con dato id
	PreparedStatement s=con.prepareStatement("select u2.iduser from (user as u1 "+
			"join friendship as f on u1.iduser=f.iduser) " +
			"join user as u2 on f.idamico=u2.iduser where u1.iduser=?");
	//ResultSet in cui memorizzo i risultati della query
	ResultSet set;
%>
<html>
<head>
<title>facebook graph</title>
<!-- Bootstrap -->
<link href="css/bootstrap.min.css" rel="stylesheet">
<script type="text/javascript"
	src="http://localhost:8080/progetto/js/vivagraph.js"></script>

<script type="text/javascript"
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
<script type="text/javascript">
    /*
     * specifico la larghezza e l'altezza
     * della finestra di pop up
     */
    var w=400;
    var h=300;
    var l = Math.floor((screen.width-w)/2);
    var t = Math.floor((screen.height-h)/2);
    var b;	//variabile in cui memorizzo la metrica
    var i;	//indice di array
    
        function main () {
            //chiama la funzione graph della libreria che ottiene un oggetto graph
            var graph = Viva.Graph.graph();
            /*
            *qui costruisco il grafo
            */
            //aggiunge il nodo dell'utente
            graph.addNode('<%=user.getId()%>','<%=user.getId()%>' );
            
            <%
            	
            	String id="";
            	//per ogni amico
            	for(Friend f:friends) {
            		id=f.getId();	//prende l'id          	
            		
            %>
            //aggiunge il nodo dell'amico corrente
            graph.addNode("<%=id%>",'<%=id%>');
            //aggiunge un link dal nodo dell'utente al nodo dell'amico
            graph.addLink('<%=user.getId()%>','<%=id%>');
            
            <%}%>
            
            <% 	
            //per ogni amico selezionato
            for(String i:ids) {
            		//setto lo statement
            		s.setString(1, i);
            		//recupero gli id degli amici
            		set=s.executeQuery();
            		//itero il ResultSet
      				while(set.next()) {
            
            %>
            //fa un link dall'amico selezionato ai suoi amici mutuali
            graph.addLink('<%=set.getString(1) %>','<%=i%>');
            
            <% }}%>
            
            var graphics = Viva.Graph.View.svgGraphics(),
                nodeSize = 24,
                //metodo per colorare di rosso i link
                highlightRelatedNodes = function(nodeId, isOn) {
                   // just enumerate all realted nodes and update link color:
                   graph.forEachLinkedNode(nodeId, function(node, link){
                       var linkUI = graphics.getLinkUI(link.id);
                       if (linkUI) {
                           // linkUI is a UI object created by graphics below
                           linkUI.attr('stroke', isOn ? 'red' : 'gray');
                       }
                   });
                };

            //gestisce gli eventi del mouse
            graphics.node(function(node) {
                var ui = Viva.Graph.svg('image')
                     .attr('width', nodeSize)
                     .attr('height', nodeSize)
                     .link('http://graph.facebook.com/' + node.data + "/picture");
                //evento di click del mouse
                $(ui).click(function(){
                	//apro una finestra di popup chiamando prima una servlet
          	      window.open("CentralityMetricsServlet?id="+node.data,"metriche di centralità",
          	    		  "width="+w+",height="+h+",top="+t+",left="+l);
                });

                $(ui).hover(function() { // mouse over
                	//quando il mouse è sopra il nodo coloro il link
                    highlightRelatedNodes(node.id, true);
                }, function() { // mouse out
                	//quando esce dal nodo il link torna nero
                    highlightRelatedNodes(node.id, false);
                });
                return ui;
            }).placeNode(function(nodeUI, pos) {
                nodeUI.attr('x', pos.x - nodeSize / 2).attr('y', pos.y - nodeSize / 2);
            });

            graphics.link(function(link){
                return Viva.Graph.svg('path')
                              .attr('stroke', 'gray');
            }).placeLink(function(linkUI, fromPos, toPos) {
                var data = 'M' + fromPos.x + ',' + fromPos.y +
                           'L' + toPos.x + ',' + toPos.y;

                linkUI.attr("d", data);
            })
			//disegna il grafo
            // Finally render the graph with our customized graphics object:
            var renderer = Viva.Graph.View.renderer(graph, {
                    graphics : graphics
                });
            renderer.run();
        }
    </script>

<style type="text/css" media="screen">
html,body,svg {
	width: 100%;
	height: 100%;
}
</style>
</head>
<!-- all'avvio chiama la funzione main() javascript -->
<body onload='main()'>

	<!-- Static navbar -->
	<div class="navbar navbar-default navbar-static-top" role="navigation">
		<div class="container">
			<div class="navbar-header">
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

</body>
</html>