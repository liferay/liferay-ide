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
%>

<liferay-ui:header
	backURL="<%= redirect %>"
	title='<%= (artist != null) ? artist.getName() : "new-artist" %>'
/>

<liferay-ui:asset-categories-error />

<liferay-ui:asset-tags-error />

<portlet:actionURL name='<%= (artist != null) ? "updateArtist" : "addArtist" %>' var="addArtistURL" />

<aui:form action="<%= addArtistURL %>" enctype="multipart/form-data" method="post" name="fm">
	<aui:model-context bean="<%= artist %>" model="<%= Artist.class %>" />

	<aui:input name="artistId" type="hidden" value="<%= artistId %>" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />

	<liferay-ui:error exception="<%= ArtistNameException.class %>" message="please-enter-a-valid-name" />

	<aui:input name="name" />

	<aui:input name="bio" />

	<aui:input name="file" type="file" />

	<c:if test="<%= (artist == null) %>">
		<aui:field-wrapper label="permissions">
			<liferay-ui:input-permissions
				modelName="<%= Artist.class.getName() %>"
				/>
		</aui:field-wrapper>
	</c:if>

	<liferay-ui:panel defaultState="closed" extended="<%= false %>" id="artistCategorizationPanel" persistState="<%= true %>" title="categorization">
		<aui:fieldset>
			<aui:input name="categories" type="assetCategories" />

			<aui:input name="tags" type="assetTags" />
		</aui:fieldset>
	</liferay-ui:panel>

	<liferay-ui:panel defaultState="closed" extended="<%= false %>" id="artistAssetLinksPanel" persistState="<%= true %>" title="related-assets">
		<aui:fieldset>
			<liferay-ui:input-asset-links
				className="<%= Artist.class.getName() %>"
				classPK="<%= artistId %>"
			/>
		</aui:fieldset>
	</liferay-ui:panel>

	<aui:button-row>
		<aui:button type="submit" />

		<c:if test="<%= artist != null %>">

		</c:if>
	</aui:button-row>
</aui:form>