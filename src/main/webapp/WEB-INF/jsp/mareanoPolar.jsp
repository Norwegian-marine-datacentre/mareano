<!DOCTYPE HTML SYSTEM>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
    <head>
        <jsp:include page = "fragments/jsAndCssIncludes.jsp" />
        <script>
            var app;
            function init() {
                OpenLayers.ImgPath = "theme/imr/img/";
                GeoExt.Lang.set('no');
                <jsp:include page = "fragments/mareanoConstructor.jsp" />
                <jsp:include page = "fragments/addLayerTreeToRoot.jsp" />
            }
        </script>
    </head>
    <body onload="init()">
    </body>
</html>
