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
String keywords = ParamUtil.getString(liferayPortletRequest, "keywords");

long albumId = ParamUtil.getLong(request, "albumId");

String displayStyle = GetterUtil.getString(portletPreferences.getValue("displayStyle", StringPool.BLANK));
long displayStyleGroupId = GetterUtil.getLong(portletPreferences.getValue("displayStyleGroupId", null), scopeGroupId);

long portletDisplayDDMTemplateId = PortletDisplayTemplateUtil.getPortletDisplayTemplateDDMTemplateId(displayStyleGroupId, displayStyle);

List<Song> songs = null;

if (albumId > 0) {
	Album album = AlbumLocalServiceUtil.getAlbum(albumId);

	if (album.isInTrash()) {
		songs = SongServiceUtil.getSongsByAlbumId(scopeGroupId, albumId, WorkflowConstants.STATUS_ANY);
	}
	else {
		songs = SongServiceUtil.getSongsByAlbumId(scopeGroupId, albumId);
	}

}
else if (Validator.isNotNull(keywords)) {
	songs = SongServiceUtil.getSongs(scopeGroupId, StringPool.PERCENT + keywords + StringPool.PERCENT);
}
else {
	songs = SongServiceUtil.getSongs(scopeGroupId);
}
%>

<c:choose>
	<c:when test="<%= portletDisplayDDMTemplateId > 0 %>">
		<%= PortletDisplayTemplateUtil.renderDDMTemplate(pageContext, portletDisplayDDMTemplateId, songs) %>
	</c:when>
	<c:when test="<%= songs.isEmpty() %>">
		<div class="alert alert-info">
			<c:choose>
				<c:when test="<%= albumId > 0 %>">
					<liferay-ui:message key="this-album-does-not-have-any-song" />
				</c:when>
				<c:otherwise>
					<liferay-ui:message key="there-are-no-songs" />
				</c:otherwise>
			</c:choose>
		</div>
	</c:when>
	<c:otherwise>

		<div id="sm2-container">
		  <!-- SM2 flash goes here -->
		</div>

		<c:if test="<%= !XugglerUtil.isEnabled() %>">
			<div class="alert alert-warning">
				<liferay-ui:message key="you-should-activate-xuggler" />
			</div>
		</c:if>

		<ul class="songs-list graphic">

			<%
			for (Song song : songs) {
			%>

				<li class="song">

					<%
					String songURL = song.getSongURL(themeDisplay, "mp3");
					%>

					<c:choose>
						<c:when test="<%= Validator.isNotNull(songURL) %>">
							<a class="song-link" href="<%= songURL %>" type="audio/mpeg">
								<%= song.getName() %>
							</a>
						</c:when>
						<c:otherwise>
							<span class="song-link">
								<%= song.getName() %>
							</span>
						</c:otherwise>
					</c:choose>

					<c:if test="<%= SongPermission.contains(permissionChecker, song.getSongId(), ActionKeys.UPDATE) %>">
						<portlet:renderURL var="editSongURL">
							<portlet:param name="jspPage" value="/html/songs/edit_song.jsp" />
							<portlet:param name="songId" value="<%= String.valueOf(song.getSongId()) %>" />
							<portlet:param name="redirect" value="<%= PortalUtil.getCurrentURL(liferayPortletRequest) %>" />
						</portlet:renderURL>

						<liferay-ui:icon cssClass="song-small-link" image="../aui/pencil" message="edit" url="<%= editSongURL %>" />
					</c:if>

					<portlet:renderURL var="viewSongURL">
						<portlet:param name="jspPage" value="/html/songs/view_song.jsp" />
						<portlet:param name="songId" value="<%= String.valueOf(song.getSongId()) %>" />
						<portlet:param name="redirect" value="<%= PortalUtil.getCurrentURL(liferayPortletRequest) %>" />
					</portlet:renderURL>

					<liferay-ui:icon cssClass="song-small-link" image="../aui/info" message="info" url="<%= viewSongURL %>" />

					<c:if test="<%= Validator.isNotNull(song.getLyricsURL(themeDisplay)) %>">
						<liferay-ui:icon cssClass="song-small-link" image="../aui/align-left" message="lyrics" method="get" url="<%= song.getLyricsURL(themeDisplay) %>" />
					</c:if>
				</li>

			<%
			}
			%>

		</ul>
	</c:otherwise>
</c:choose>