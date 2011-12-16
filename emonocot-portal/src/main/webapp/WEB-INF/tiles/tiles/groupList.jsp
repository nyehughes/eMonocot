<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:tags="urn:jsptagdir:/WEB-INF/tags"
	xmlns:em="http://e-monocot.org/portal/functions"
	xmlns:security="http://www.springframework.org/security/tags"
	xmlns:spring="http://www.springframework.org/tags" version="2.0">

	<div class="content">
		<div class="page-header">
		  <h2 id="page-title"><spring:message code="groups"/></h2>
		</div>
		<div class="row">
          <tags:info/>
        </div>
		<div class="row">
			<div class="pagination">
						<tags:pagination pager="${result}" url="search"/>
		  	</div>
		</div>
		<div class="row">
		  <div id="pages" class="span8">				
			 <tags:results pager="${result}"/>
	      </div>
		</div>
		<div class="row">
		  <div id="results">
		     <c:forEach var="item" items="${result.records}">
				<div class="well">
				  <jsp:element name="a">
				    <jsp:attribute name="class">result pull-left</jsp:attribute>
				    <jsp:attribute name="title">${item.name}</jsp:attribute>
				    <jsp:attribute name="href">
				      <c:url value="group/${item.identifier}"/>
				    </jsp:attribute>
				    <h4 class="h4Results">${item.name}</h4>
				  </jsp:element>
				</div>
			 </c:forEach>
		  </div>
		  <security:authorize url="hasRole('PERMISSION_CREATE_GROUP')">
		    <div>
		      <jsp:element name="a">
		        <jsp:attribute name="href">
		          <c:url value="group">
		            <c:param name="form">true</c:param>
		          </c:url>
		        </jsp:attribute>
		        <spring:message code="create.group"/>
		      </jsp:element>
		    </div>
		  </security:authorize>
		</div>
	</div>
</jsp:root>