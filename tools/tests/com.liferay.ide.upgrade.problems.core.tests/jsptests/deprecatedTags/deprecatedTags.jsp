
<clay:table
	contributorKey="SampleTable"
	dataSourceKey="SampleTable"
	tableSchemaContributorKey="SampleTable"
/>

<liferay-ui:alert
	closeable="true"
	icon="exclamation-full"
	message="Here is our awesome alert example"
	type="success"
/>

<liferay-ui:input-scheduler>
</liferay-ui:input-scheduler>

<liferay-ui:organization-search-container-results>
</liferay-ui:organization-search-container-results>

<liferay-ui:organization-search-form>
</liferay-ui:organization-search-form>

<liferay-ui:ratings className="<%=Entry.class.getName()%>"
	classPK="<%=entry.getEntryId()%>" type="stars" />

<liferay-ui:search-speed>
</liferay-ui:search-speed>

<liferay-ui:toggle>
	<liferay-ui:toggle-area
		id="toggle_id_journal_edit_article_extra"
		showMessage='<%= LanguageUtil.get(pageContext, "advanced") + " &raquo;" %>'
		hideMessage='<%= "&laquo; " + LanguageUtil.get(pageContext, "basic") %>'
		align="right"
	>
		<aui:field-wrapper label="template">
			<liferay-ui:table-iterator
				list="<%= templates %>"
				listType="com.liferay.portlet.journal.model.JournalTemplate"
				rowLength="3"
				rowPadding="30"
			>
		
				<%
				boolean templateChecked = false;
		
				if (templateId.equals(tableIteratorObj.getTemplateId())) {
					templateChecked = true;
				}
		
				if ((tableIteratorPos.intValue() == 0) && Validator.isNull(templateId)) {
					templateChecked = true;
				}
				%>
		
				<aui:input name='<%= "template" + tableIteratorObj.getTemplateId() + "_xsl" %>' type="hidden" value="<%= JS.encodeURIComponent(tableIteratorObj.getXsl()) %>" />
		
				<liferay-util:buffer var="templateLabel">
					<portlet:renderURL var="templateURL">
						<portlet:param name="struts_action" value="/journal/edit_template" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
						<portlet:param name="groupId" value="<%= String.valueOf(tableIteratorObj.getGroupId()) %>" />
						<portlet:param name="templateId" value="<%= tableIteratorObj.getTemplateId() %>" />
					</portlet:renderURL>
		
					<aui:a href="<%= templateURL %>" label="<%= HtmlUtil.escape(tableIteratorObj.getName()) %>" id="structureName" />
				</liferay-util:buffer>
		
				<aui:input checked="<%= templateChecked %>" inlineField="<%= true %>" label="<%= templateLabel %>" name="templateId" type="radio" value="<%= tableIteratorObj.getTemplateId() %>" />
		
				<c:if test="<%= tableIteratorObj.isSmallImage() %>">
		
					<br />
		
					<img border="0" hspace="0" src="<%= Validator.isNotNull(tableIteratorObj.getSmallImageURL()) ? tableIteratorObj.getSmallImageURL() : themeDisplay.getPathImage() + "/journal/template?img_id=" + tableIteratorObj.getSmallImageId() + "&t=" + ImageServletTokenUtil.getToken(tableIteratorObj.getSmallImageId()) %>" vspace="0" />
				</c:if>
			</liferay-ui:table-iterator>
		</aui:field-wrapper>
	</liferay-ui:toggle-area>
</liferay-ui:toggle>

<liferay-ui:user-search-container-results>
	<liferay-ui:user-search>
	</liferay-ui:user-search>
</liferay-ui:user-search-container-results>
