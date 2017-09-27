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
String toolbarItem = ParamUtil.getString(request, "toolbarItem");

boolean searchView = ParamUtil.getBoolean(request, "searchView");
%>

<aui:nav-bar>
	<aui:nav>
		<c:if test='<%= JukeBoxPermission.contains(permissionChecker, scopeGroupId, "ADD_ALBUM") %>'>
			<portlet:renderURL var="editAlbumURL">
				<portlet:param name="jspPage" value="/html/albums/edit_album.jsp" />
				<portlet:param name="redirect" value="<%= PortalUtil.getCurrentURL(renderRequest) %>" />
			</portlet:renderURL>

			<aui:nav-item href="<%= editAlbumURL %>" iconCssClass="icon-plus" label="add-album" selected='<%= toolbarItem.equals("add") %>' />
		</c:if>
	</aui:nav>

	<aui:nav-bar-search cssClass="pull-right">
		<div class="form-search">

			<%
			PortletURL portletURL = renderResponse.createRenderURL();

			portletURL.setParameter("jspPage", "/html/albums/view_search.jsp");
			portletURL.setParameter("redirect", PortalUtil.getCurrentURL(renderRequest));

			AlbumSearch searchContainer = new AlbumSearch(renderRequest, portletURL);

			AlbumDisplayTerms displayTerms = (AlbumDisplayTerms)searchContainer.getDisplayTerms();
			%>

			<liferay-ui:search-toggle
				autoFocus="<%= liferayPortletRequest.getWindowState().equals(WindowState.MAXIMIZED) %>"
				buttonLabel="search"
				displayTerms="<%= displayTerms %>"
				id="<%= renderResponse.getNamespace() %>"
			>
				<aui:fieldset>
					<aui:input name="<%= displayTerms.TITLE %>" size="20" type="text" value="<%= displayTerms.getTitle() %>" />

					<aui:input name="<%= displayTerms.ARTIST %>" size="20" type="text" value="<%= displayTerms.getArtist() %>" />

					<aui:input name="<%= displayTerms.YEAR %>" size="20" type="text" value="<%= displayTerms.getYear() %>" />
				</aui:fieldset>
			</liferay-ui:search-toggle>
		</div>
	</aui:nav-bar-search>
</aui:nav-bar>

<c:if test="<%= !searchView %>">
	<aui:script use="liferay-jukebox-search">
		var albumPanel = A.one('#<portlet:namespace />albumPanel');
		var inputNode = A.one('#<portlet:namespace />keywords');

		var search = new Liferay.JukeBoxContentSearch(
			{
				contentPanel: albumPanel,
				inputNode: inputNode,
				resourceURL: '<portlet:resourceURL><portlet:param name="jspPage" value="/html/albums/view_resources.jsp" /></portlet:resourceURL>',
				namespace: '<portlet:namespace />'
			}
		);
	</aui:script>
</c:if>