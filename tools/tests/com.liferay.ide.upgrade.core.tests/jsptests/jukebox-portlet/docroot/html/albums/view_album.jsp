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

long albumId = ParamUtil.getLong(request, "albumId");

Album album = null;

if (albumId > 0) {
	album = AlbumLocalServiceUtil.getAlbum(albumId);
}
else {
	album = (Album)request.getAttribute("jukebox_album");
}

Artist artist = ArtistLocalServiceUtil.getArtist(album.getArtistId());

List<Song> songs = SongLocalServiceUtil.getSongsByAlbumId(album.getAlbumId());

boolean showHeader = ParamUtil.getBoolean(request, "showHeader", true);
%>

<c:if test="<%= showHeader %>">
	<liferay-ui:header
		backURL="<%= redirect %>"
		title="<%= album.getName() %>"
	/>
</c:if>

<c:if test="<%= AlbumPermission.contains(permissionChecker, album.getAlbumId(), ActionKeys.UPDATE) %>">
	<aui:nav-bar>
		<aui:nav>
			<portlet:renderURL var="editAlbumURL">
				<portlet:param name="jspPage" value="/html/albums/edit_album.jsp" />
				<portlet:param name="albumId" value="<%= String.valueOf(album.getAlbumId()) %>" />
				<portlet:param name="redirect" value="<%= PortalUtil.getCurrentURL(liferayPortletRequest) %>" />
			</portlet:renderURL>

			<aui:nav-item href="<%= editAlbumURL %>" iconCssClass="icon-pencil" label="edit" />
		</aui:nav>

		<c:if test='<%= JukeBoxPermission.contains(permissionChecker, scopeGroupId, "ADD_SONG") %>'>
			<aui:nav>
				<portlet:renderURL var="editArtistURL">
					<portlet:param name="jspPage" value="/html/songs/edit_song.jsp" />
					<portlet:param name="albumId" value="<%= String.valueOf(albumId) %>" />
					<portlet:param name="redirect" value="<%= PortalUtil.getCurrentURL(renderRequest) %>" />
				</portlet:renderURL>

				<aui:nav-item href="<%= editArtistURL %>" iconCssClass="icon-plus" label="add-song" />
			</aui:nav>
		</c:if>

		<c:if test="<%= AlbumPermission.contains(permissionChecker, album.getAlbumId(), ActionKeys.PERMISSIONS) %>">
			<aui:nav>
				<liferay-security:permissionsURL
					modelResource="<%= Album.class.getName() %>"
					modelResourceDescription="<%= album.getName() %>"
					resourcePrimKey="<%= String.valueOf(album.getAlbumId()) %>"
					var="permissionsAlbumURL"
					windowState="<%= LiferayWindowState.POP_UP.toString() %>"
				/>

				<aui:nav-item href="<%= permissionsAlbumURL %>" iconCssClass="icon-key" label="permissions" useDialog="<%= true %>" title="permissions" />
			</aui:nav>
		</c:if>

		<c:if test="<%= AlbumPermission.contains(permissionChecker, album.getAlbumId(), ActionKeys.DELETE) %>">

			<%
			boolean trashEnabled = TrashUtil.isTrashEnabled(scopeGroupId);
			%>

			<aui:nav>
				<portlet:actionURL name="deleteAlbum" var="deleteAlbumURL">
					<portlet:param name="albumId" value="<%= String.valueOf(album.getAlbumId()) %>" />
					<portlet:param name="moveToTrash" value="<%= String.valueOf(trashEnabled) %>" />
					<portlet:param name="redirect" value="<%= redirect %>" />
				</portlet:actionURL>

				<c:choose>
					<c:when test="<%= trashEnabled %>">
						<aui:nav-item href="<%= deleteAlbumURL %>" iconCssClass="icon-trash" label="move-to-the-recycle-bin" />
					</c:when>
					<c:otherwise>
						<aui:nav-item href="<%= deleteAlbumURL %>" iconCssClass="icon-key" label="delete" useDialog="<%= true %>" />
					</c:otherwise>
				</c:choose>
			</aui:nav>
		</c:if>
	</aui:nav-bar>
</c:if>

<div class="album-detail">
	<div class="container">
		<img alt="" class="img-rounded album-image" src="<%= album.getImageURL(themeDisplay) %>" />

		<div class="album-metainfo">
			<div class="album-artist">
				<div>
					<img alt="" class="img-circle artist-image" src="<%= artist.getImageURL(themeDisplay) %>" />

					<%= artist.getName() %>

					<span class="album-year">(<%= album.getYear() %>)</span>

					<div class="album-songs-number">
						<liferay-ui:message arguments="<%= songs.size() %>" key="x-songs" />
					</div>
				</div>
			</div>

			<div class="entry-categories">
				<liferay-ui:asset-categories-summary
					className="<%= Album.class.getName() %>"
					classPK="<%= albumId %>"
					portletURL="<%= renderResponse.createRenderURL() %>"
				/>
			</div>

			<div class="entry-tags">
				<liferay-ui:asset-tags-summary
					className="<%= Album.class.getName() %>"
					classPK="<%= albumId %>"
					portletURL="<%= renderResponse.createRenderURL() %>"
				/>
			</div>

			<div class="entry-links">
				<liferay-ui:asset-links
					className="<%= Album.class.getName() %>"
					classPK="<%= albumId %>"
				/>
			</div>

			<c:if test="<%= !songs.isEmpty() %>">
				<liferay-util:include page="/html/songs/view.jsp" servletContext="<%= application %>">
					<liferay-util:param name="albumId" value="<%= String.valueOf(album.getAlbumId()) %>" />
					<liferay-util:param name="showToolbar" value="<%= String.valueOf(false) %>" />
				</liferay-util:include>
			</c:if>
		</div>
	</div>

	<c:if test="<%= showHeader %>">
		<portlet:actionURL name="invokeTaglibDiscussion" var="discussionURL" />

		<liferay-ui:discussion
			className="<%= Album.class.getName() %>"
			classPK="<%= album.getAlbumId() %>"
			formAction="<%= discussionURL %>"
			formName="fm2"
			userId="<%= album.getUserId() %>"
		/>
	</c:if>
</div>