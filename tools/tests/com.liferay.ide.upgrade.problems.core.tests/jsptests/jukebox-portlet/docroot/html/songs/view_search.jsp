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

PortletURL portletURL = renderResponse.createRenderURL();

portletURL.setParameter("jspPage", "/html/songs/view_search.jsp");
portletURL.setParameter("redirect", PortalUtil.getCurrentURL(renderRequest));

SongSearch searchContainer = new SongSearch(renderRequest, portletURL);

SongDisplayTerms displayTerms = (SongDisplayTerms)searchContainer.getDisplayTerms();

Indexer indexer = IndexerRegistryUtil.getIndexer(Song.class);

SearchContext searchContext = SearchContextFactory.getInstance(request);

if (displayTerms.isAdvancedSearch()) {
	searchContext.setAndSearch(displayTerms.isAndOperator());
	searchContext.setAttribute(Field.TITLE, displayTerms.getTitle());
	searchContext.setAttribute("artist", String.valueOf(displayTerms.getArtist()));
}
else {
	searchContext.setKeywords(displayTerms.getKeywords());
}

searchContext.setIncludeAttachments(true);
searchContext.setIncludeDiscussions(true);

QueryConfig queryConfig = new QueryConfig();

queryConfig.setHighlightEnabled(true);

searchContext.setQueryConfig(queryConfig);

Hits hits = indexer.search(searchContext);
%>

<liferay-ui:header
	backURL="<%= redirect %>"
	title="search"
/>

<portlet:renderURL var="searchURL">
	<portlet:param name="jspPage" value="/html/songs/view_search.jsp" />
	<portlet:param name="redirect" value="<%= PortalUtil.getCurrentURL(renderRequest) %>" />
	<portlet:param name="searchView" value="<%= Boolean.TRUE.toString() %>" />
</portlet:renderURL>

<aui:form action="<%= searchURL %>" method="post" name="fm">
	<liferay-util:include page="/html/songs/toolbar.jsp" servletContext="<%= application %>">
		<liferay-util:param name="searchView" value="<%= Boolean.TRUE.toString() %>" />
	</liferay-util:include>
</aui:form>

<c:choose>
	<c:when test="<%= hits.getLength() <= 0 %>">
		<div class="alert alert-info">
			<liferay-ui:message key="there-are-no-songs" />
		</div>
	</c:when>
	<c:otherwise>
		<ul class="search-result">

			<%
			PortletURL hitURL = liferayPortletResponse.createRenderURL();

			List<SearchResult> searchResultsList = SearchResultUtil.getSearchResults(hits, locale, hitURL);

			for (int i = 0; i < searchResultsList.size(); i++) {
				SearchResult searchResult = searchResultsList.get(i);

				Summary summary = searchResult.getSummary();

				List<String> versions = searchResult.getVersions();

				Collections.sort(versions);

				Song song = SongLocalServiceUtil.getSong(searchResult.getClassPK());
			%>

				<li class="search-result-info">
					<portlet:renderURL var="viewSongURL">
						<portlet:param name="jspPage" value="/html/songs/view_song.jsp" />
						<portlet:param name="songId" value="<%= String.valueOf(song.getSongId()) %>" />
						<portlet:param name="redirect" value="<%= PortalUtil.getCurrentURL(renderRequest) %>" />
					</portlet:renderURL>

					<liferay-ui:app-view-search-entry
						description="<%= (summary != null) ? HtmlUtil.escape(summary.getContent()) : StringPool.BLANK %>"
						fileEntryTuples="<%= searchResult.getFileEntryTuples() %>"
						mbMessages="<%= searchResult.getMBMessages() %>"
						queryTerms="<%= hits.getQueryTerms() %>"
						thumbnailSrc="<%= song.getImageURL(themeDisplay) %>"
						title="<%= (summary != null) ? HtmlUtil.escape(summary.getTitle()) : song.getName() %>"
						url="<%= viewSongURL %>"
					/>
				</li>

			<%
			}
			%>

		</ul>
	</c:otherwise>
</c:choose>