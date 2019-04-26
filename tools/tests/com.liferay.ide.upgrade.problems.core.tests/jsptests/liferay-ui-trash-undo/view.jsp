<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
long albumId = ParamUtil.getLong(request, "albumId");
boolean showToolbar = ParamUtil.getBoolean(request, "showToolbar", true);
%>

<liferay-ui:success key="songAdded" message="the-song-was-added-successfully" />
<liferay-ui:success key="songUpdated" message="the-song-was-updated-successfully" />
<liferay-ui:success key="songDeleted" message="the-song-was-deleted-successfully" />

<portlet:actionURL name="restoreSong" var="undoTrashURL" />

<liferay-ui:trash-undo portletURL="<%= undoTrashURL %>" />

<c:if test="<%= (albumId <= 0) && showToolbar %>">
	<portlet:renderURL var="searchURL">
		<portlet:param name="jspPage" value="/html/songs/view_search.jsp" />
		<portlet:param name="redirect" value="<%= PortalUtil.getCurrentURL(renderRequest) %>" />
		<portlet:param name="searchView" value="<%= Boolean.TRUE.toString() %>" />
	</portlet:renderURL>

	<aui:form action="<%= searchURL %>" method="post" name="fm">
		<liferay-util:include page="/html/songs/toolbar.jsp" servletContext="<%= application %>" />
	</aui:form>
</c:if>

<div id="<portlet:namespace />songPanel">
	<liferay-util:include page="/html/songs/view_resources.jsp" servletContext="<%= application %>">
		<liferay-util:param name="albumId" value="<%= String.valueOf(albumId) %>" />
	</liferay-util:include>
</div>