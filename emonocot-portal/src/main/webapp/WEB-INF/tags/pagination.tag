<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:spring="http://www.springframework.org/tags"
	xmlns:c="http://java.sun.com/jsp/jstl/core" version="2.0">
	<jsp:directive.attribute name="pager"
		type="org.emonocot.model.pager.Page" required="true" />
    <jsp:directive.attribute name="url"
		type="java.lang.String" required="true" />
	<ul>
		<c:choose>
			<c:when test="${pager.prevIndex != null}">
				<li class="prev"><jsp:element name="a">
								<jsp:attribute name="href">
									<c:url value="${url}">
										<c:forEach var="p" items="${pager.paramNames}">									
										    <c:param name="${p}" value="${pager.params[p]}"/>																	
										</c:forEach>	
										<c:param name="limit" value="${pager.pageSize}" />
										<c:param name="start" value="${pager.prevIndex}" />
										<c:param name="sort">${pager.sort}</c:param>
										<c:forEach var="selectedFacet" items="${pager.selectedFacetNames}">
											<c:param name="facet">
												<jsp:scriptlet>
												     String selectedFacet = (String) jspContext.getAttribute("selectedFacet");
                                                     org.emonocot.model.pager.Page pager = (org.emonocot.model.pager.Page) jspContext.getAttribute("pager");
                                                     out.println(selectedFacet + "." + pager.getSelectedFacets().get(selectedFacet));</jsp:scriptlet>
											</c:param>
										</c:forEach>
									</c:url>
								</jsp:attribute>
                      	← <spring:message code="previous" />
					</jsp:element></li>
			</c:when>
			<c:otherwise>
				<li class="prev disabled"><a href="#">← <spring:message
							code="previous" /> </a>
				</li>
			</c:otherwise>
		</c:choose>
		<!--<c:forEach var="index" items="${pager.indices}">
			<c:choose>
				<c:when test="${pager.currentIndex eq index}">-->
					<li class="active"><a href="#">${pager.currentPageNumber}</a></li>
				<!--</c:when>
				<c:otherwise>
					<li><jsp:element name="a">
								<jsp:attribute name="href">
									<c:url value="${url}">
										<c:forEach var="p" items="${pager.paramNames}">									
										    <c:param name="${p}" value="${pager.params[p]}"/>																	
										</c:forEach>
										<c:param name="limit" value="${pager.pageSize}" />
										<c:param name="start" value="${index}" />
										<c:param name="sort">${pager.sort}</c:param>
										<c:forEach var="selectedFacet"
										items="${pager.selectedFacetNames}">
											<c:param name="facet">
												<jsp:scriptlet>
												     String selectedFacet = (String) jspContext.getAttribute("selectedFacet");
                                                     org.emonocot.model.pager.Page pager = (org.emonocot.model.pager.Page) jspContext.getAttribute("pager");
                                                     out.println(selectedFacet + "." + pager.getSelectedFacets().get(selectedFacet));</jsp:scriptlet>
											</c:param>
										</c:forEach>
									</c:url>
								</jsp:attribute>
								<jsp:scriptlet>
								    Integer index = (Integer) jspContext.getAttribute("index");
                                    org.emonocot.model.pager.Page pager = (org.emonocot.model.pager.Page) jspContext.getAttribute("pager");
                                    out.println(pager.getPageNumber(index));
                                </jsp:scriptlet>
                         </jsp:element>
				    </li>
				</c:otherwise>
			</c:choose>
		</c:forEach>-->
		<c:choose>
			<c:when test="${pager.nextIndex != null}">
				<li class="next"><jsp:element name="a">
								<jsp:attribute name="href">
									<c:url value="${url}">
										<c:forEach var="p" items="${pager.paramNames}">									
										    <c:param name="${p}" value="${pager.params[p]}"/>																	
										</c:forEach>
										<c:param name="limit" value="${pager.pageSize}" />
										<c:param name="start" value="${pager.nextIndex}" />
										<c:param name="sort">${pager.sort}</c:param>
										<c:forEach var="selectedFacet"
									items="${pager.selectedFacetNames}">
											<c:param name="facet">
												<jsp:scriptlet>String selectedFacet = (String) jspContext
                                    .getAttribute("selectedFacet");
                            org.emonocot.model.pager.Page pager = (org.emonocot.model.pager.Page) jspContext
                                    .getAttribute("pager");
                            out.println(selectedFacet
                                    + "."
                                    + pager.getSelectedFacets().get(
                                            selectedFacet));</jsp:scriptlet>
											</c:param>
										</c:forEach>
									</c:url>
								</jsp:attribute>
						<spring:message code="next" /> →</jsp:element></li>
			</c:when>
			<c:otherwise>
				<li class="next disabled"><a href="#"><spring:message
							code="next" /> →</a>
				</li>
			</c:otherwise>
		</c:choose>
	</ul>
</jsp:root>
