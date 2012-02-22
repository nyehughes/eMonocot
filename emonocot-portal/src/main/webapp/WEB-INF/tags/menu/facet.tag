<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
  xmlns:spring="http://www.springframework.org/tags"
  xmlns:em="http://e-monocot.org/portal/functions"
  xmlns:fn="http://java.sun.com/jsp/jstl/functions"
  xmlns:c="http://java.sun.com/jsp/jstl/core" version="2.0">
	<jsp:directive.attribute name="facetName"
		type="java.lang.String" required="true" />
	<jsp:directive.attribute name="pager" type="org.emonocot.model.pager.Page"
		required="true" />
	<jsp:directive.attribute name="title" type="java.lang.String" required="false" />
	
	<jsp:directive.attribute name="showIcons" type="java.lang.Boolean" required="false" />
	
	<spring:message var="more" code="more"/>
	<spring:message var="less" code="less"/>
	<li id="${facetName}">
		<h3>
			<c:choose>
				<c:when test="${not empty title}">
					<spring:message code="${title}" />
				</c:when>
				<c:otherwise>
					<spring:message code="${facetName}" />
				</c:otherwise>
			</c:choose>
		</h3>
		
		<ul class="facet">
			<c:set var="facetSelected" value="${em:isFacetSelected(pager,facetName)}"/>
			<c:choose>
				<c:when test="${facetSelected}">
					<li>
						<jsp:element name="a">
							<jsp:attribute name="href">
								<c:url value="search">
									<c:forEach var="p" items="${pager.paramNames}">
										<c:param name="${p}" value="${pager.params[p]}"/>
									</c:forEach>
									<c:param name="limit" value="${pager.pageSize}" />
									<c:param name="start" value="0" />
									<c:param name="sort">${pager.sort}</c:param>
									<c:forEach var="selectedFacet" items="${pager.selectedFacetNames}">
										<c:if test="${selectedFacet != facetName}">
											<c:param name="facet" value="${selectedFacet}.${pager.selectedFacets[selectedFacet]}"/>
										</c:if>
									</c:forEach>
								</c:url>
							</jsp:attribute>
							<c:if test="${facetName eq 'CLASS'}">
								<c:set value="no-small-icon" var="cssClass"></c:set>
							</c:if>
							<div class="${cssClass}"><spring:message code="${facetName}.clearFacet" /></div>
						</jsp:element>
					</li>
						
					<li>
						<c:set var="selectedFacetName" value="${pager.selectedFacets[facetName]}"/>
						<c:if test="${showIcons}">
							<spring:url var="imageUrl" value="/images/${selectedFacetName}.jpg"/>
							<img src="${imageUrl}"/>
						</c:if>
						<spring:message code="${selectedFacetName}" />
					</li>
				</c:when>
				<c:otherwise>
					<c:set var="values" value="${pager.facets[facetName]}"/>
					<c:forEach var="facet" begin="0" end="9" step="1" items="${values}">
						<li>
							<c:choose>
								<c:when test="${facet.count == 0}">
									<c:if test="${facetName eq 'CLASS'}">
										<c:set value="no-small-icon" var="cssClass"></c:set>
									</c:if>
									<div class="${cssClass}"><spring:message code="${facet.value}" /></div>
								</c:when>
								<c:otherwise>
									<c:if test="${showIcons}">
										<spring:url var="imageUrl" value="/images/${facet.value}.jpg"/>
										<img src="${imageUrl}"/>
									</c:if>
									<jsp:element name="a">
										<jsp:attribute name="href">
											<c:url value="search">
												<c:forEach var="p" items="${pager.paramNames}">
													<c:param name="${p}" value="${pager.params[p]}"/>
												</c:forEach>
												<c:param name="limit" value="${pager.pageSize}" />
												<c:param name="start" value="0" />
												<c:param name="sort">${pager.sort}</c:param>
												<c:param name="facet" value="${facetName}.${facet.value}" />
												<c:forEach var="selectedFacet" items="${pager.selectedFacetNames}">
													<c:if test="${selectedFacet != facetName}">
														<c:param name="facet" value="${selectedFacet}.${pager.selectedFacets[selectedFacet]}"/>
													</c:if>
												</c:forEach>
											</c:url>
										</jsp:attribute>
										<spring:message code="${facet.value}" />
									</jsp:element>
									<c:if test="${!em:isMultiValued(facetName)}">
										<p style="display:inline"> [${facet.count}]</p>
									</c:if>
								</c:otherwise>
							</c:choose>
						</li>
					</c:forEach>
					<c:if test="${fn:length(values) gt 10}">
						<div id="${facetName}-collapse" class="collapse">
							<c:forEach var="facet" begin="10" items="${values}">
								<li>
									<c:choose>
										<c:when test="${facet.count == 0}">
											<spring:message code="${facet.value}" />
										</c:when>
										<c:otherwise>
											<jsp:element name="a">
												<jsp:attribute name="href">
													<c:url value="search">
														<c:forEach var="p" items="${pager.paramNames}">
															<c:param name="${p}" value="${pager.params[p]}"/>
														</c:forEach>
														<c:param name="limit" value="${pager.pageSize}" />
														<c:param name="start" value="0" />
														<c:param name="sort">${pager.sort}</c:param>
														<c:param name="facet" value="${facetName}.${facet.value}" />
														<c:forEach var="selectedFacet" items="${pager.selectedFacetNames}">
															<c:if test="${selectedFacet != facetName}">
																<c:param name="facet" value="${selectedFacet}.${pager.selectedFacets[selectedFacet]}"/>
															</c:if>
														</c:forEach>
													</c:url>
												</jsp:attribute>
												<spring:message code="${facet.value}" />
											</jsp:element>
											<c:if test="${!em:isMultiValued(facetName)}">
												<p style="display:inline"> [${facet.count}]</p>
											</c:if>
										</c:otherwise>
									</c:choose>
								</li>
							</c:forEach>
						</div>						
						<a id="${facetName}-collapse-link" data-toggle="collapse" data-target="#${facetName}-collapse">${more}</a>
					</c:if>
				</c:otherwise>
			</c:choose>
		</ul>
	</li>
	<script>
		$("#${facetName}-collapse").on('hidden', function () {
			$("#${facetName}-collapse-link").html("${more}");
		}).on('shown', function () {
			$("#${facetName}-collapse-link").html("${less}");
		});
	</script>
		
				
</jsp:root>