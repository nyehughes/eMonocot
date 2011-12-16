<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:em="http://e-monocot.org/portal/functions"
	xmlns:tags="urn:jsptagdir:/WEB-INF/tags"
	xmlns:form="http://www.springframework.org/tags/form"
	xmlns:spring="http://www.springframework.org/tags" version="2.0">

	<div class="content">
		<div class="page-header">
		  <h2 id="page-title">${group.name}</h2>
        </div>
        <div class="row">
          <tags:info/>
        </div>        
		<div class="row">
		  <div>	
			<h2><spring:message code="group.members"/></h2>
			<ul>
			  <c:forEach var="member" items="${group.members}">
			    <li>
			      <jsp:element name="a">
		          <jsp:attribute name="href">
		            <c:url value="/user/${member.identifier}"/>
		          </jsp:attribute>
		          ${member.identifier}
		        </jsp:element>
		        <jsp:element name="a">
		          <jsp:attribute name="href">
		            <c:url value="/group/${group.identifier}">
			          <c:param name="members">true</c:param>
			          <c:param name="delete">true</c:param>
			          <c:param name="user">${member.identifier}</c:param>
			        </c:url>
		          </jsp:attribute>
		          <jsp:attribute name="class">btn danger</jsp:attribute>
		          <jsp:attribute name="title"><spring:message code="remove.member"/></jsp:attribute>
		          ×
		        </jsp:element>
		      </li>
			  </c:forEach>
			</ul>
			<c:url value="/group/${group.identifier}" var="actionUrl">
			  <c:param name="members">true</c:param>
			</c:url>
			<form:form commandName="user" action="${actionUrl}">
				<spring:bind path="user.username">
					<c:choose>
						<c:when test="${not empty status.errorMessage}">
							<c:set var="class">clearfix error</c:set>
						</c:when>
						<c:otherwise>
							<c:set var="class">clearfix</c:set>
						</c:otherwise>
					</c:choose>
					<jsp:element name="div">
						<jsp:attribute name="class">${class}</jsp:attribute>
						<form:label path="username"><spring:message code="user.username"/></form:label>
						<div class="input">
							<form:input path="username" />
							<span class="help-inline"><form:errors path="username" /></span>
							<jsp:element name="input" class="btn primary">
					          <jsp:attribute name="type">submit</jsp:attribute>
					          <jsp:attribute name="value">
            					<spring:message code="add.member" />
			         		  </jsp:attribute>
				           </jsp:element>
						</div>
					</jsp:element>
				</spring:bind>
			</form:form>
	      </div>
	      <div>	
			<h2><spring:message code="group.permissions"/></h2>
			<ul>
			  <c:forEach var="permission" items="${group.permissions}">
			    <li><spring:message code="${permission}"/></li>
			  </c:forEach>
			</ul>
	      </div>
	      <div>	
			<h2><spring:message code="group.aces"/></h2>
			<ul>
			  <c:forEach var="row" items="${aces}">
			    <c:set var="ace" value="${row[1]}"/>
			    <c:set var="object" value="${row[0]}"/>
			    <li>${object.identifier} <spring:message code="${em:convert(ace.permission)}"/>
			    <jsp:element name="a">
		          <jsp:attribute name="href">
		            <c:url value="/group/${group.identifier}">
			          <c:param name="aces">true</c:param>
			          <c:param name="delete">true</c:param>
			          <c:param name="identifier">${object.identifier}</c:param>
			          <c:param name="clazz">${em:convert(object.class)}</c:param>
			          <c:param name="permission">${em:convert(ace.permission)}</c:param>
			        </c:url>
		          </jsp:attribute>
		          <jsp:attribute name="class">btn danger</jsp:attribute>
		          <jsp:attribute name="title"><spring:message code="remove.ace"/></jsp:attribute>
		          ×
		        </jsp:element></li>
			  </c:forEach>
			</ul>
			<c:url value="/group/${group.identifier}" var="actionUrl">
			  <c:param name="aces">true</c:param>
			</c:url>
			<form:form commandName="ace" action="${actionUrl}">
			    <form:hidden path="clazz"/>
			    <form:hidden path="permission"/>
				<spring:bind path="ace.object">
					<c:choose>
						<c:when test="${not empty status.errorMessage}">
							<c:set var="class">clearfix error</c:set>
						</c:when>
						<c:otherwise>
							<c:set var="class">clearfix</c:set>
						</c:otherwise>
					</c:choose>
					<jsp:element name="div">
						<jsp:attribute name="class">${class}</jsp:attribute>
						<form:label path="object"><spring:message code="ace.object"/></form:label>
						<div class="input">
							<form:input path="object" />
							<span class="help-inline"><form:errors path="object" /></span>
							<jsp:element name="input" class="btn primary">
					          <jsp:attribute name="type">submit</jsp:attribute>
					          <jsp:attribute name="value">
            					<spring:message code="add.ace" />
			         		  </jsp:attribute>
				           </jsp:element>
						</div>
					</jsp:element>
				</spring:bind>
			</form:form>
	      </div>
		</div>
		
	</div>
</jsp:root>