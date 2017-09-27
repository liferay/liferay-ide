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

String displayStyle = GetterUtil.getString(portletPreferences.getValue("displayStyle", StringPool.BLANK));
long displayStyleGroupId = GetterUtil.getLong(portletPreferences.getValue("displayStyleGroupId", null), scopeGroupId);

long portletDisplayDDMTemplateId = PortletDisplayTemplateUtil.getPortletDisplayTemplateDDMTemplateId(displayStyleGroupId, displayStyle);

List<Artist> artists = null;

if (Validator.isNotNull(keywords)) {
	artists = ArtistServiceUtil.getArtists(scopeGroupId, StringPool.PERCENT + keywords + StringPool.PERCENT);
}
else {
	artists = ArtistServiceUtil.getArtists(scopeGroupId);
}
%>

<c:choose>
	<c:when test="<%= portletDisplayDDMTemplateId > 0 %>">
		<%= PortletDisplayTemplateUtil.renderDDMTemplate(pageContext, portletDisplayDDMTemplateId, artists) %>
	</c:when>
	<c:when test="<%= artists.isEmpty() %>">
		<div class="alert alert-info">
			<liferay-ui:message key="there-are-no-artists" />
		</div>
	</c:when>
	<c:otherwise>
		<ul class="artists-list unstyled">

			<%
			for (Artist artist : artists) {
			%>

			<li class="artist">
				<portlet:renderURL var="viewArtistURL">
					<portlet:param name="jspPage" value="/html/artists/view_artist.jsp" />
					<portlet:param name="artistId" value="<%= String.valueOf(artist.getArtistId()) %>" />
					<portlet:param name="redirect" value="<%= PortalUtil.getCurrentURL(liferayPortletRequest) %>" />
				</portlet:renderURL>

				<aui:a href="<%= viewArtistURL %>">
					<img alt="" class="artist-image img-circle" src="<%= artist.getImageURL(themeDisplay) %>" />

					<%= artist.getName() %>
				</aui:a>

				<c:if test="<%= ArtistPermission.contains(permissionChecker, artist.getArtistId(), ActionKeys.UPDATE) %>">
					<portlet:renderURL var="editArtistURL">
						<portlet:param name="jspPage" value="/html/artists/edit_artist.jsp" />
						<portlet:param name="artistId" value="<%= String.valueOf(artist.getArtistId()) %>" />
						<portlet:param name="redirect" value="<%= PortalUtil.getCurrentURL(liferayPortletRequest) %>" />
					</portlet:renderURL>

					<liferay-ui:icon cssClass="artist-small-link" image="../aui/pencil" message="edit" url="<%= editArtistURL %>" />
				</c:if>
			</li>

			<%
			}
			%>

		</ul>
	</c:otherwise>
</c:choose>