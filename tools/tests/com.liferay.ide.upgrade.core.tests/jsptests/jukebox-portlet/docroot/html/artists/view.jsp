<%--
/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="../init.jsp" %>

<liferay-ui:success key="artistAdded" message="the-artist-was-added-successfully" />
<liferay-ui:success key="artistUpdated" message="the-artist-was-updated-successfully" />
<liferay-ui:success key="artistDeleted" message="the-artist-was-deleted-successfully" />

<portlet:renderURL var="searchURL">
	<portlet:param name="jspPage" value="/html/artists/view_search.jsp" />
	<portlet:param name="redirect" value="<%= PortalUtil.getCurrentURL(renderRequest) %>" />
	<portlet:param name="searchView" value="<%= Boolean.TRUE.toString() %>" />
</portlet:renderURL>

<aui:form action="<%= searchURL %>" method="post" name="fm">
	<liferay-util:include page="/html/artists/toolbar.jsp" servletContext="<%= application %>" />
</aui:form>

<div id="<portlet:namespace />artistPanel">
	<liferay-util:include page="/html/artists/view_resources.jsp" servletContext="<%= application %>" />
</div>