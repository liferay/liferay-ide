<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

<%@ include file="/init.jsp" %>

<strong><liferay-ui:message key="my-roster" /></strong>

<aui:button-row>
	<portlet:renderURL var="editRosterURL">
		<portlet:param name="mvcPath" value="/edit_roster.jsp" />
		<portlet:param name="redirect" value="<%= currentURL %>" />
	</portlet:renderURL>

	<aui:button href="<%= editRosterURL %>" value="add-roster" />
</aui:button-row>

<liferay-ui:search-container
	total="<%= RosterLocalServiceUtil.getRostersCount() %>"
>
	<liferay-ui:search-container-results
		results="<%= RosterLocalServiceUtil.getRosters(searchContainer.getStart(), searchContainer.getEnd()) %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.roster.model.Roster"
		escapedModel="true"
		modelVar="roster"
	>
		<liferay-ui:search-container-column-text
			name="id"
			property="rosterId"
			valign="top"
		/>

		<liferay-ui:search-container-column-text
			name="clubId"
			valign="top"
		>
			<strong><%= roster.getClubId() %></strong>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			property="name"
			valign="top"
		/>

		<liferay-ui:search-container-column-jsp
			cssClass="entry-action"
			path="/roster_action.jsp"
			valign="top"
		/>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator />
</liferay-ui:search-container>