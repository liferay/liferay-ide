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

<%@ include file="/html/portlet/search/facets/init.jsp" %>

<%@ page import="com.liferay.portal.kernel.repository.model.FileEntry" %>
<%@ page import="com.liferay.portal.model.Repository" %>
<%@ page import="com.liferay.portal.portletfilerepository.PortletFileRepositoryUtil" %>
<%@ page import="com.liferay.portlet.documentlibrary.model.DLFolderConstants" %>
<%@ page import="com.liferay.portlet.documentlibrary.util.DLUtil" %>

<%
if (termCollectors.isEmpty()) {
	return;
}

int frequencyThreshold = dataJSONObject.getInt("frequencyThreshold");
int maxTerms = dataJSONObject.getInt("maxTerms", 10);
boolean showAssetCount = dataJSONObject.getBoolean("showAssetCount", true);

Indexer indexer = IndexerRegistryUtil.getIndexer("org.liferay.jukebox.model.Artist");
%>

<div class="<%= cssClass %>" data-facetFieldName="<%= facet.getFieldId() %>" id="<%= randomNamespace %>facet">
	<aui:input name="<%= facet.getFieldId() %>" type="hidden" value="<%= fieldParam %>" />

	<ul class="artist unstyled">
		<li class="facet-value default <%= Validator.isNull(fieldParam) ? "current-term" : StringPool.BLANK %>">
			<a data-value="" href="javascript:;"><img alt="" class="any-artist-result" src='<%= themeDisplay.getPortalURL() + "/jukebox-portlet/icons/artists.png" %>' /><liferay-ui:message key="any" /> <liferay-ui:message key="<%= facetConfiguration.getLabel() %>" /></a>
		</li>

		<%
		long artistId = GetterUtil.getLong(fieldParam);

		for (int i = 0; i < termCollectors.size(); i++) {
			TermCollector termCollector = termCollectors.get(i);

			long curArtistId = GetterUtil.getLong(termCollector.getTerm());

			SearchContext searchContext = SearchContextFactory.getInstance(request);

			searchContext.setAttribute("artistId", curArtistId);
			searchContext.setKeywords(StringPool.BLANK);

			Hits results = indexer.search(searchContext);

			if (results.getLength() == 0) {
				continue;
			}

			Document document = results.doc(0);

			String artistName = document.get(Field.TITLE);
		%>

			<c:if test="<%= artistId == curArtistId %>">
				<aui:script use="liferay-token-list">
					Liferay.Search.tokenList.add(
						{
							clearFields: '<%= renderResponse.getNamespace() + facet.getFieldId() %>',
							text: '<%= artistName %>'
						}
					);
				</aui:script>
			</c:if>

			<%
			if (((maxTerms > 0) && (i >= maxTerms)) || ((frequencyThreshold > 0) && (frequencyThreshold > termCollector.getFrequency()))) {
				break;
			}
			%>

			<li class="facet-value <%= (artistId == curArtistId) ? "current-term" : StringPool.BLANK %>">
				<a data-value="<%= curArtistId %>" href="javascript:;"><img alt="" class="artist-search-result img-circle" src='<%= getImageURL(curArtistId, themeDisplay) %>' /><%= HtmlUtil.escape(artistName) %></a><c:if test="<%= showAssetCount %>"> <span class="frequency">(<%= termCollector.getFrequency() %>)</span></c:if>
			</li>

		<%
		}
		%>

	</ul>
</div>

<style type="text/css">
	.artist-search-result {
		width: 30px;
		height: 30px;

	}

	.any-artist-result {
		width: 25px;
	}
</style>

<%!
protected String getImageURL(long artistId, ThemeDisplay themeDisplay) throws SystemException {
	Repository repository = PortletFileRepositoryUtil.fetchPortletRepository(themeDisplay.getScopeGroupId(), "JukeboxPortletRepository");

	try {
		if (repository != null) {
			FileEntry fileEntry = PortletFileRepositoryUtil.getPortletFileEntry(repository.getRepositoryId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, String.valueOf(artistId));

			return DLUtil.getPreviewURL(fileEntry, fileEntry.getLatestFileVersion(), themeDisplay, StringPool.BLANK);
		}
	}
	catch (Exception e) {
	}

	return themeDisplay.getPortalURL() + "/jukebox-portlet/images/singer2.jpeg";
}
%>