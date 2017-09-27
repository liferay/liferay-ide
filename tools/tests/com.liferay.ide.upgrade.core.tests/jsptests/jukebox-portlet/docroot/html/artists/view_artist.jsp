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
String redirect = ParamUtil.getString(request, "redirect");

long artistId = ParamUtil.getLong(request, "artistId");

Artist artist = null;

if (artistId > 0) {
	artist = ArtistLocalServiceUtil.getArtist(artistId);
}
else {
	artist = (Artist)request.getAttribute("jukebox_artist");
}

List<Album> albums = AlbumLocalServiceUtil.getAlbumsByArtistId(artist.getArtistId());

boolean showHeader = ParamUtil.getBoolean(request, "showHeader", true);
%>

<c:if test="<%= showHeader %>">
	<liferay-ui:header
		backURL="<%= redirect %>"
		title="<%= artist.getName() %>"
	/>
</c:if>

<c:if test="<%= ArtistPermission.contains(permissionChecker, artist.getArtistId(), ActionKeys.UPDATE) %>">
	<aui:nav-bar>
		<aui:nav>
			<portlet:renderURL var="editArtistURL">
				<portlet:param name="jspPage" value="/html/artists/edit_artist.jsp" />
				<portlet:param name="artistId" value="<%= String.valueOf(artist.getArtistId()) %>" />
				<portlet:param name="redirect" value="<%= PortalUtil.getCurrentURL(liferayPortletRequest) %>" />
			</portlet:renderURL>

			<aui:nav-item href="<%= editArtistURL %>" iconCssClass="icon-pencil" label="edit" />
		</aui:nav>

		<c:if test='<%= JukeBoxPermission.contains(permissionChecker, scopeGroupId, "ADD_ALBUM") %>'>
			<aui:nav>
				<portlet:renderURL var="editArtistURL">
					<portlet:param name="jspPage" value="/html/albums/edit_album.jsp" />
					<portlet:param name="artistId" value="<%= String.valueOf(artistId) %>" />
					<portlet:param name="redirect" value="<%= PortalUtil.getCurrentURL(renderRequest) %>" />
				</portlet:renderURL>

				<aui:nav-item href="<%= editArtistURL %>" iconCssClass="icon-plus" label="add-album" />
			</aui:nav>
		</c:if>

		<c:if test="<%= ArtistPermission.contains(permissionChecker, artist.getArtistId(), ActionKeys.PERMISSIONS) %>">
			<aui:nav>
				<liferay-security:permissionsURL
					modelResource="<%= Artist.class.getName() %>"
					modelResourceDescription="<%= artist.getName() %>"
					resourcePrimKey="<%= String.valueOf(artist.getArtistId()) %>"
					var="permissionsArtistURL"
					windowState="<%= LiferayWindowState.POP_UP.toString() %>"
				/>

				<aui:nav-item href="<%= permissionsArtistURL %>" iconCssClass="icon-key" label="permissions" useDialog="<%= true %>" title="permissions" />
			</aui:nav>
		</c:if>

		<c:if test="<%= ArtistPermission.contains(permissionChecker, artist.getArtistId(), ActionKeys.DELETE) %>">
			<aui:nav>
				<portlet:actionURL name="deleteArtist" var="deleteArtistURL">
					<portlet:param name="artistId" value="<%= String.valueOf(artist.getArtistId()) %>" />
					<portlet:param name="redirect" value="<%= redirect %>" />
				</portlet:actionURL>

				<aui:nav-item href="<%= deleteArtistURL %>" iconCssClass="icon-remove" label="delete" />
			</aui:nav>
		</c:if>
	</aui:nav-bar>
</c:if>

<div class="artist-detail">
	<div class="container">
		<img alt="" class="img-circle artist-image" src="<%= artist.getImageURL(themeDisplay) %>" />

		<div class="artist-metainfo">
			<div class="artist-bio">
				<%= artist.getBio() %>
			</div>

			<div class="album-songs-number">
				<liferay-ui:message arguments="<%= albums.size() %>" key="x-albums" />
			</div>

			<div class="entry-categories">
				<liferay-ui:asset-categories-summary
					className="<%= Artist.class.getName() %>"
					classPK="<%= artistId %>"
					portletURL="<%= renderResponse.createRenderURL() %>"
				/>
			</div>

			<div class="entry-tags">
				<liferay-ui:asset-tags-summary
					className="<%= Artist.class.getName() %>"
					classPK="<%= artistId %>"
					portletURL="<%= renderResponse.createRenderURL() %>"
				/>
			</div>

			<div class="entry-links">
				<liferay-ui:asset-links
					className="<%= Artist.class.getName() %>"
					classPK="<%= artistId %>"
				/>
			</div>
		</div>
	</div>

	<c:if test="<%= !albums.isEmpty() %>">
		<liferay-util:include page="/html/albums/view.jsp" servletContext="<%= application %>">
			<liferay-util:param name="artistId" value="<%= String.valueOf(artist.getArtistId()) %>" />
			<liferay-util:param name="showToolbar" value="<%= String.valueOf(false) %>" />
		</liferay-util:include>
	</c:if>

	<c:if test="<%= showHeader %>">
		<portlet:actionURL name="invokeTaglibDiscussion" var="discussionURL" />

		<liferay-ui:discussion
			className="<%= Artist.class.getName() %>"
			classPK="<%= artist.getArtistId() %>"
			formAction="<%= discussionURL %>"
			formName="fm2"
			userId="<%= artist.getUserId() %>"
		/>
	</c:if>
</div>