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

long artistId = ParamUtil.getLong(request, "artistId");

String displayStyle = GetterUtil.getString(portletPreferences.getValue("displayStyle", StringPool.BLANK));
long displayStyleGroupId = GetterUtil.getLong(portletPreferences.getValue("displayStyleGroupId", null), scopeGroupId);

long portletDisplayDDMTemplateId = PortletDisplayTemplateUtil.getPortletDisplayTemplateDDMTemplateId(displayStyleGroupId, displayStyle);

List<Album> albums = null;

if (artistId > 0) {
	albums = AlbumServiceUtil.getAlbumsByArtistId(scopeGroupId, artistId);
}
else if (Validator.isNotNull(keywords)) {
	albums = AlbumServiceUtil.getAlbums(scopeGroupId, StringPool.PERCENT + keywords + StringPool.PERCENT);
}
else {
	albums = AlbumServiceUtil.getAlbums(scopeGroupId);
}
%>

<c:choose>
	<c:when test="<%= portletDisplayDDMTemplateId > 0 %>">
		<%= PortletDisplayTemplateUtil.renderDDMTemplate(pageContext, portletDisplayDDMTemplateId, albums) %>
	</c:when>
	<c:when test="<%= albums.isEmpty() %>">
		<div class="alert alert-info">
			<c:choose>
				<c:when test="<%= artistId > 0 %>">
					<liferay-ui:message key="this-artist-does-not-have-any-album" />
				</c:when>
				<c:otherwise>
					<liferay-ui:message key="there-are-no-albums" />
				</c:otherwise>
			</c:choose>
		</div>
	</c:when>
	<c:otherwise>
		<ul class="unstyled albums-list">

			<%
			for (Album album : albums) {
			%>

			<li class="album">

				<%
				Artist artist = ArtistLocalServiceUtil.getArtist(album.getArtistId());
				%>

				<portlet:renderURL var="viewAlbumURL">
					<portlet:param name="jspPage" value="/html/albums/view_album.jsp" />
					<portlet:param name="albumId" value="<%= String.valueOf(album.getAlbumId()) %>" />
					<portlet:param name="redirect" value="<%= PortalUtil.getCurrentURL(liferayPortletRequest) %>" />
				</portlet:renderURL>

				<aui:a href="<%= viewAlbumURL %>">
					<img alt="" class="album-image img-rounded" src="<%= album.getImageURL(themeDisplay) %>" />

					<%= album.getName() %>
				</aui:a>

				<c:if test="<%= AlbumPermission.contains(permissionChecker, album.getAlbumId(), ActionKeys.UPDATE) %>">
					<portlet:renderURL var="editAlbumURL">
						<portlet:param name="jspPage" value="/html/albums/edit_album.jsp" />
						<portlet:param name="albumId" value="<%= String.valueOf(album.getAlbumId()) %>" />
						<portlet:param name="redirect" value="<%= PortalUtil.getCurrentURL(liferayPortletRequest) %>" />
					</portlet:renderURL>

					<liferay-ui:icon cssClass="album-small-link" image="../aui/pencil" message="edit" url="<%= editAlbumURL %>" />
				</c:if>

				<div class="album-artist-name">
					<%= artist.getName() %>
				</div>

			</li>

			<%
			}
			%>

		</ul>
	</c:otherwise>
</c:choose>