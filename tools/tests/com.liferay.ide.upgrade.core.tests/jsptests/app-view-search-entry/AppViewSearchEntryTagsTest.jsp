<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.search.SearchResult"
			modelVar="searchResult">
	<liferay-ui:app-view-search-entry
					containerIcon="../common/conversation"
					containerName="<%= MBUtil.getAbsolutePath(renderRequest, message.getCategoryId()) %>"
					containerType='<%= LanguageUtil.get(locale, "category[message-board]") %>'
					mbMessages="<%= searchResult.getMBMessages() %>"
					cssClass='<%= MathUtil.isEven(index) ? "search" : "search alt" %>'
					description="<%= (summary != null) ? HtmlUtil.escape(summary.getContent()) : StringPool.BLANK %>"
					fileEntryTuples="<%= searchResult.getFileEntryTuples() %>"
					queryTerms="<%= hits.getQueryTerms() %>"
					title="<%= (summary != null) ? HtmlUtil.escape(summary.getTitle()) : HtmlUtil.escape(message.getSubject()) %>"
					url="<%= rowURL %>"
	/>
</liferay-ui:search-container-row>