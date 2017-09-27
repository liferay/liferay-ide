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

<%
long artistId = ParamUtil.getLong(request, "artistId");
boolean showToolbar = ParamUtil.getBoolean(request, "showToolbar", true);
%>

<liferay-ui:success key="albumAdded" message="the-album-was-added-successfully" />
<liferay-ui:success key="albumUpdated" message="the-album-was-updated-successfully" />
<liferay-ui:success key="albumDeleted" message="the-album-was-deleted-successfully" />

<portlet:actionURL name="restoreAlbum" var="undoTrashURL" />

<liferay-ui:trash-undo portletURL="<%= undoTrashURL %>" />

<c:if test="<%= (artistId <= 0) && showToolbar %>">
	<portlet:renderURL var="searchURL">
		<portlet:param name="jspPage" value="/html/albums/view_search.jsp" />
		<portlet:param name="redirect" value="<%= PortalUtil.getCurrentURL(renderRequest) %>" />
		<portlet:param name="searchView" value="<%= Boolean.TRUE.toString() %>" />
	</portlet:renderURL>

	<aui:form action="<%= searchURL %>" method="post" name="fm">
		<liferay-util:include page="/html/albums/toolbar.jsp" servletContext="<%= application %>" />
	</aui:form>
</c:if>

<div id="<portlet:namespace />albumPanel">
	<liferay-util:include page="/html/albums/view_resources.jsp" servletContext="<%= application %>">
		<liferay-util:param name="artistId" value="<%= String.valueOf(artistId) %>" />
	</liferay-util:include>
</div>