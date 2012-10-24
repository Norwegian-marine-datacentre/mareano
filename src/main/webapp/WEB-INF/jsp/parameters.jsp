<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<table width="100%" border="0">
<tr id="gridColumn" style="display:none;">
	<td width="130px" align="center">
		<a href="#" onmouseover="hideShow('hiddenGridHelp');">Grid:</a>
	</td>
	<td width="165px" align="center">
		<select id="grid" class="fishexchange" title="${grid_value_selected}">
			<c:forEach items="${grids}" var="grid">
		    	<c:choose>
		        	<c:when test="${grid_value_selected eq grid || (grid_value_selected == null && grid=='FishExChange')}">
		            	<option selected="selected" id="${grid}" title="${grid}">${grid}</option>
					</c:when>
		            <c:otherwise>
		            	<option id="${grid}" title="${grid}">${grid}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
	</td>
<td rowspan="5" align="left" valign="top" >
	<b><spring:message code="Help" text="default text" />:</b>
	<span class="hiddenFirstHelptext" style="display:none;" ><spring:message code="firstHelptext" text="default text" /></span>
	<span class="hiddenGridHelp" style="display:none;" ><spring:message code="GridHelp" text="default text" /></span>
	<span class="hiddenSpeciesHelp" style="display:none;"><spring:message code="speciesHelp" text="default text" /></span>
	<span class="hiddenSubgroupHelp" style="display:none;"><spring:message code="speciesSubgroupHelp" text="default text" /></span>
	<span class="hiddenDepthHelp" style="display:none;"><spring:message code="depthHelp" text="default text" /></span>
	<span class="hiddenPeriodHelp" style="display:none;"><spring:message code="periodHelp" text="default text" /></span>
</td></tr>

<tr><td width="130px" align="center"><a href="#" onmouseover="hideShow('hiddenSpeciesHelp');"><spring:message code="species" text="default text" /></a>: </td>
<td width="165px" align="center">
	<select id="dataset" class="fishexchange" title="${dataset_value_selected}">
	    <option><spring:message code="selectValue" text="default text" /></option>
	    <c:forEach items="${datasets}" var="dataset">
	    	<c:choose>
	        	<c:when test="${dataset_value_selected eq dataset}">
	            	<option selected="selected" id="${dataset}" title="${dataset}">${dataset}</option>
				</c:when>
	            <c:otherwise>
	            	<option id="${dataset}" title="${dataset}">${dataset}</option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</select>
</td></tr>

<tr><td width="130px" align="center" nowrap="nowrap"><a href="#" onmouseover="hideShow('hiddenSubgroupHelp');"><spring:message code="speciesSubgroup" text="default text" /></a>: </td>
<td width="165px" align="center">
	<select id="parameter" disabled="disabled" class="fishexchange" title="${parameter_value_selected}">
		<option><spring:message code="selectValue" text="default text" /></option>
	    <c:forEach items="${parameters}" var="parameter">
	    	<c:choose>
	        	<c:when test="${parameter_value_selected eq parameter}">
	            	<option selected="selected" id="${parameter}">${parameter}</option>
				</c:when>
	            <c:otherwise>
	            	<option id="${parameter}" title="${parameter}">${parameter}</option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</select>
</td></tr>
<tr id="depthLayerColumn" style="display:none;">
	<td width="130px" align="center">
		<a href="#" onmouseover="hideShow('hiddenDepthHelp');">
		<spring:message code="depthLayer" text="default text" /></a>: 
	</td>
	<td width="165px" align="center">
		<select id="depthlayer" class="depth_period fishexchange" disabled="disabled">
			<c:forEach items="${depthlayers}" var="depthlayer">
		    	<option id="${depthlayer}" title="${depthlayer}">${depthlayer}</option>
			</c:forEach>
		</select>
	</td>
</tr>
<tr id="periodColumn" style="display:none;">
	<td width="130px" align="center">
		<a href="#" onmouseover="hideShow('hiddenPeriodHelp');"><spring:message code="period" text="default text" /></a>:
	</td>
	<td width="165px" align="center">
		<select id="period" class="depth_period fishexchange" disabled="disabled">
			<c:forEach items="${periods}" var="period">
	    		<option id="${period}" title="${period}">${period}</option>
			</c:forEach>
		</select>
	</td>
</tr>
<tr><td colspan="2" align="center">
	<spring:message code="pointView" text="default text" /> <input type="radio" name="visning" id="punkt" />
	<spring:message code="areaView" text="default text" /> <input type="radio" name="visning" id="areal" checked/>
</td>
<td>
	<span style="float:left">	
		<input id="makemap" type="button" value="<spring:message code="makeMap" text="default text" />" onclick="drawmap()" disabled="disabled"/>
		<input id="createpdf" type="button" value="<spring:message code="makePdf" text="default text" />" onclick="createPDF()" disabled="disabled"/>
	</span>
	<span style="float:right">
		<a href="#" onclick="showHiddenSelect()" id="advanced"><spring:message code="advanced" text="default text"/></a>
		<a href="#" onclick="hideSelect()" id="simple" style="display:none;"><spring:message code="simple" text="default text"/></a>
	</span>
</td></tr>
</table>


<!--<div id="legend_div"><img src="" id="legend"></div>-->
<c:if test="${metadata ne null}">
	<div id="metadata" style="display:none;">${metadata}</div>
</c:if>
<div id="metadata_heading"><b><spring:message code="LayerInformation" text="default text" /></b></div>
<div id="scrollbarLayerInfo" 
	style="width:100%;height:150px;overflow:auto;border-width:2px;border-color:000000;border-style:dotted;display:none;">
	<div id="external_metadata" ></div>
</div>