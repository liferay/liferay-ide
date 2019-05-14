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

long songId = ParamUtil.getLong(request, "songId");

Song song = null;

if (songId > 0) {
	 song = SongLocalServiceUtil.getSong(songId);
}
else {
	song = (Song)request.getAttribute("jukebox_song");
}

Album album = AlbumLocalServiceUtil.getAlbum(song.getAlbumId());

Artist artist = ArtistLocalServiceUtil.getArtist(song.getArtistId());

boolean showHeader = ParamUtil.getBoolean(request, "showHeader", true);
%>

<c:if test="<%= showHeader %>">
	<liferay-ui:header
		backURL="<%= redirect %>"
		title="<%= song.getName() %>"
	/>
</c:if>

<c:if test="<%= SongPermission.contains(permissionChecker, song.getSongId(), ActionKeys.UPDATE) %>">

	<aui:nav-bar>
		<aui:nav>
			<portlet:renderURL var="editSongURL">
				<portlet:param name="jspPage" value="/html/songs/edit_song.jsp" />
				<portlet:param name="songId" value="<%= String.valueOf(song.getSongId()) %>" />
				<portlet:param name="redirect" value="<%= PortalUtil.getCurrentURL(liferayPortletRequest) %>" />
			</portlet:renderURL>

			<aui:nav-item href="<%= editSongURL %>" iconCssClass="icon-pencil" label="edit" />
		</aui:nav>

		<c:if test="<%= SongPermission.contains(permissionChecker, song.getSongId(), ActionKeys.PERMISSIONS) %>">
			<aui:nav>
				<liferay-security:permissionsURL
					modelResource="<%= Song.class.getName() %>"
					modelResourceDescription="<%= song.getName() %>"
					resourcePrimKey="<%= String.valueOf(song.getSongId()) %>"
					var="permissionsSongURL"
					windowState="<%= LiferayWindowState.POP_UP.toString() %>"
				/>

				<aui:nav-item href="<%= permissionsSongURL %>" iconCssClass="icon-key" label="permissions" useDialog="<%= true %>" title="permissions" />
			</aui:nav>
		</c:if>

		<c:if test="<%= SongPermission.contains(permissionChecker, song.getSongId(), ActionKeys.DELETE) %>">

			<%
			boolean trashEnabled = TrashUtil.isTrashEnabled(scopeGroupId);
			%>

			<aui:nav>
				<portlet:actionURL name="deleteSong" var="deleteSongURL">
					<portlet:param name="songId" value="<%= String.valueOf(song.getSongId()) %>" />
					<portlet:param name="moveToTrash" value="<%= String.valueOf(trashEnabled) %>" />
					<portlet:param name="redirect" value="<%= redirect %>" />
				</portlet:actionURL>

				<c:choose>
					<c:when test="<%= trashEnabled %>">
						<aui:nav-item href="<%= deleteSongURL %>" iconCssClass="icon-trash" label="move-to-the-recycle-bin" />
					</c:when>
					<c:otherwise>
						<aui:nav-item href="<%= deleteSongURL %>" iconCssClass="icon-key" label="delete" useDialog="<%= true %>" />
					</c:otherwise>
				</c:choose>
			</aui:nav>
		</c:if>
	</aui:nav-bar>
</c:if>

<div class="song-details">
	<div class="song-info">
		<div class="song-artist">
			<img alt="" class="img-circle artist-image" src="<%= artist.getImageURL(themeDisplay) %>" />

			<%= artist.getName() %>
		</div>

		<div class="song-album">
			<img alt="" class="img-rounded album-image" src="<%= album.getImageURL(themeDisplay) %>" />

			<%= album.getName() %>
			<span class="song-year">(<%= album.getYear() %>)</span>
		</div>
	</div>

	<div class="song-metainfo">
		<div class="song-player">
			<ul class="songs-list graphic">
				<li class="song">

					<%
					String songURL = song.getSongURL(themeDisplay, "mp3");
					%>

					<c:choose>
						<c:when test="<%= Validator.isNotNull(songURL) %>">
							<a class="song-link" href="<%= songURL %>" type="audio/mpeg">
								<%= song.isInTrash() ? TrashUtil.getOriginalTitle(song.getName()) : song.getName() %>
							</a>
						</c:when>
						<c:otherwise>
							<span class="song-link">
								<%= song.isInTrash() ? TrashUtil.getOriginalTitle(song.getName()) : song.getName() %>
							</span>
						</c:otherwise>
					</c:choose>

					<c:if test="<%= Validator.isNotNull(song.getLyricsURL(themeDisplay)) %>">
						<liferay-ui:icon cssClass="song-small-link" image="../aui/align-left" label="<%= true %>" message="lyrics" method="get" url="<%= song.getLyricsURL(themeDisplay) %>" />
					</c:if>
				</li>
			</ul>
		</div>

		<div class="entry-categories">
			<liferay-ui:asset-categories-summary
				className="<%= Song.class.getName() %>"
				classPK="<%= songId %>"
				portletURL="<%= renderResponse.createRenderURL() %>"
			/>
		</div>

		<div class="entry-tags">
			<liferay-ui:asset-tags-summary
				className="<%= Song.class.getName() %>"
				classPK="<%= songId %>"
				portletURL="<%= renderResponse.createRenderURL() %>"
			/>
		</div>

		<div class="entry-links">
			<liferay-ui:asset-links
				className="<%= Song.class.getName() %>"
				classPK="<%= songId %>"
			/>
		</div>
	</div>
</div>

<c:if test="<%= showHeader %>">
	<portlet:actionURL name="invokeTaglibDiscussion" var="discussionURL" />

	<liferay-ui:discussion
		className="<%= Song.class.getName() %>"
		classPK="<%= song.getSongId() %>"
		formAction="<%= discussionURL %>"
		formName="fm2"
		userId="<%= song.getUserId() %>"
	/>
</c:if>