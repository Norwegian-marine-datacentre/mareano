<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
var mergedSomeHovedtema;
<c:forEach var="hovedtema" items="${hovedtemaer}">
    if ( !("${hovedtema.hovedtema}" == "generelle") ) {
        mergedSomeHovedtema = new Ext.tree.TreeNode({
            text: "${hovedtema.hovedtema}"
        });         
    <c:forEach var="bilde" items="${hovedtema.bilder}">
        var group = addLayerToGroup("${bilde.gruppe}","${bilde.gruppe}", this.map, this.mapPanel, layers, store, app);
        if (group.attributes.expanded === true) {
            mergedSomeHovedtema.expanded = true;
        }
        group.attributes.maxExtent = [
            ${bilde.startextentMinx},
            ${bilde.startextentMiny},
            ${bilde.startextentMaxx},
            ${bilde.startextentMaxy}
        ];
        mergedSomeHovedtema.appendChild( group );
        </c:forEach>
        treeRoot.getRootNode().appendChild( mergedSomeHovedtema );
    }
</c:forEach>
treeRoot.getRootNode().appendChild( mergedSomeHovedtema );