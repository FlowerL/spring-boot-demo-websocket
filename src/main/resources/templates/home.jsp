<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
    String wsPath = "ws://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<html>
<head>
    <title>Home</title>
</head>
<body>
<h1>
    Hello world! This is a WebSocket demo!
</h1>
<div id="message">
</div>
<script type="text/javascript" src="../static/jquery-3.3.1.js"></script>
<script type="text/javascript" src="../static/sockjs.js"></script>

<script type="text/javascript">
    $(function () {
        //通过HTTP协议自动建立socket连接，服务端对"/socketServer"和"/sockjs/socketServer"进行拦截
        var sock;
        if ('WebSocket' in window) {
            sock = new WebSocket("<%=wsPath%>socketServer");
        } else if ('MozWebSocket' in window) {
            sock = new MozWebSocket("<%=wsPath%>socketServer");
        } else {
            sock = new SockJS("<%=basePath%>sockjs/socketServer");
        }

        sock.onopen = function (e) {
            console.log(e);
        };
        sock.onmessage = function (e) {
            console.log(e)
            $("#message").append("<p><font color='red'>" + e.data + "</font>")
        };
        sock.onerror = function (e) {
            console.log(e);
        };
        sock.onclose = function (e) {
            console.log(e);
        }
    });
</script>
</body>
</html>