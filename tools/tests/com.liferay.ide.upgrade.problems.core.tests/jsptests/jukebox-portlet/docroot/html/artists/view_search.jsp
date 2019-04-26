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

portletURL.setParameter("jspPage", "/html/artists/view_search.jsp");
portletURL.setParameter("redirect", PortalUtil.getCurrentURL(renderRequest));

ArtistSearch searchContainer = new ArtistSearch(renderRequest, portletURL);

ArtistDisplayTerms displayTerms = (ArtistDisplayTerms)searchContainer.getDisplayTerms();

Indexer indexer = IndexerRegistryUtil.getIndexer(Artist.class);

SearchContext searchContext = SearchContextFactory.getInstance(request);

if (displayTerms.isAdvancedSearch()) {
	searchContext.setAndSearch(displayTerms.isAndOperator());
	searchContext.setAttribute(Field.TITLE, displayTerms.getTitle());
	searchContext.setAttribute("bio", String.valueOf(displayTerms.getBio()));
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
	<portlet:param name="jspPage" value="/html/artists/view_search.jsp" />
	<portlet:param name="redirect" value="<%= PortalUtil.getCurrentURL(renderRequest) %>" />
	<portlet:param name="searchView" value="<%= Boolean.TRUE.toString() %>" />
</portlet:renderURL>

<aui:form action="<%= searchURL %>" method="post" name="fm">
	<liferay-util:include page="/html/artists/toolbar.jsp" servletContext="<%= application %>">
		<liferay-util:param name="searchView" value="<%= Boolean.TRUE.toString() %>" />
	</liferay-util:include>
</aui:form>

<c:choose>
	<c:when test="<%= hits.getLength() <= 0 %>">
		<div class="alert alert-info">
			<liferay-ui:message key="there-are-no-artists" />
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

				Artist artist = ArtistLocalServiceUtil.getArtist(searchResult.getClassPK());
			%>

			<li class="search-result-info">
				<portlet:renderURL var="viewArtistURL">
					<portlet:param name="jspPage" value="/html/artists/view_artist.jsp" />
					<portlet:param name="artistId" value="<%= String.valueOf(artist.getArtistId()) %>" />
					<portlet:param name="redirect" value="<%= PortalUtil.getCurrentURL(renderRequest) %>" />
				</portlet:renderURL>

				<liferay-ui:app-view-search-entry
					description="<%= (summary != null) ? HtmlUtil.escape(summary.getContent()) : StringPool.BLANK %>"
					fileEntryTuples="<%= searchResult.getFileEntryTuples() %>"
					mbMessages="<%= searchResult.getMBMessages() %>"
					queryTerms="<%= hits.getQueryTerms() %>"
					thumbnailSrc="<%= artist.getImageURL(themeDisplay) %>"
					title="<%= (summary != null) ? HtmlUtil.escape(summary.getTitle()) : artist.getName() %>"
					url="<%= viewArtistURL %>"
				/>
			</li>

			<%
			}
			%>

		</ul>
	</c:otherwise>
</c:choose>