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
%>

<liferay-ui:header
	backURL="<%= redirect %>"
	title='<%= (song != null) ? song.getName() : "new-song" %>'
/>

<liferay-ui:asset-categories-error />

<liferay-ui:asset-tags-error />

<portlet:actionURL name='<%= (song != null) ? "updateSong" : "addSong" %>' var="addSongURL" />

<aui:form action="<%= addSongURL %>" enctype="multipart/form-data" method="post" name="fm">
	<aui:model-context bean="<%= song %>" model="<%= Song.class %>" />

	<aui:input name="songId" type="hidden" value="<%= songId %>" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />

	<aui:select label="album" name="albumId">

		<%
		List<Album> albums = AlbumLocalServiceUtil.getAlbums(scopeGroupId);

		for (Album album : albums) {
		%>

			<aui:option label="<%= album.getName() %>" value="<%= album.getAlbumId() %>" />

		<%
		}
		%>

	</aui:select>

	<liferay-ui:error exception="<%= DuplicatedSongException.class %>" message="a-song-with-the-same-name-already-exists-in-this-album" />
	<liferay-ui:error exception="<%= SongNameException.class %>" message="please-enter-a-valid-name" />

	<aui:input name="name" />

	<aui:input label="song-file" name="songFile" type="file" />

	<aui:input label="lyrics-file" name="lyricsFile" type="file" />

	<c:if test="<%= (song == null) %>">
		<aui:field-wrapper label="permissions">
			<liferay-ui:input-permissions
				modelName="<%= Song.class.getName() %>"
				/>
		</aui:field-wrapper>
	</c:if>

	<liferay-ui:panel defaultState="closed" extended="<%= false %>" id="songCategorizationPanel" persistState="<%= true %>" title="categorization">
		<aui:fieldset>
			<aui:input name="categories" type="assetCategories" />

			<aui:input name="tags" type="assetTags" />
		</aui:fieldset>
	</liferay-ui:panel>

	<liferay-ui:panel defaultState="closed" extended="<%= false %>" id="songAssetLinksPanel" persistState="<%= true %>" title="related-assets">
		<aui:fieldset>
			<liferay-ui:input-asset-links
				className="<%= Song.class.getName() %>"
				classPK="<%= songId %>"
			/>
		</aui:fieldset>
	</liferay-ui:panel>

	<aui:button-row>
		<aui:button type="submit" />
	</aui:button-row>
</aui:form>