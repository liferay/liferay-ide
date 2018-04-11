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

<%
String redirect = ParamUtil.getString(request, "redirect");

long rosterId = ParamUtil.getLong(request, "rosterId");

Roster roster = null;

if (rosterId > 0) {
	roster = RosterLocalServiceUtil.getRoster(rosterId);
}
%>

<aui:form action="<%= renderResponse.createActionURL() %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= roster == null ? Constants.ADD : Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="rosterId" type="hidden" value="<%= rosterId %>" />

	<liferay-ui:header
		backURL="<%= redirect %>"
		title='<%= (roster != null) ? roster.getClubId() : "new-roster" %>'
	/>

	<liferay-ui:asset-categories-error />

	<liferay-ui:asset-tags-error />

	<aui:model-context bean="<%= roster %>" model="<%= Roster.class %>" />

	<aui:fieldset>
		<aui:input name="clubId" />

		<aui:input name="name" />

		<liferay-ui:custom-attributes-available className="<%= Roster.class.getName() %>">
			<liferay-ui:custom-attribute-list
				className="<%= Roster.class.getName() %>"
				classPK="<%= (roster != null) ? roster.getRosterId() : 0 %>"
				editable="<%= true %>"
				label="<%= true %>"
			/>
		</liferay-ui:custom-attributes-available>
	</aui:fieldset>

	<aui:button-row>
		<aui:button type="submit" />

		<aui:button href="<%= redirect %>" type="cancel" />
	</aui:button-row>
</aui:form>