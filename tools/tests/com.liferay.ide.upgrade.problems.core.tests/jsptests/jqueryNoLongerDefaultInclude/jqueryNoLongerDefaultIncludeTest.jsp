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
LayoutPrototype layoutPrototype = (LayoutPrototype)request.getAttribute("edit_layout_prototype.jsp-layoutPrototype");
String redirect = (String)request.getAttribute("edit_layout_prototype.jsp-redirect");

int mergeFailCount = SitesUtil.getMergeFailCount(layoutPrototype);
%>

<c:if test="<%= mergeFailCount > PropsValues.LAYOUT_PROTOTYPE_MERGE_FAIL_THRESHOLD %>">

	<%
	String randomNamespace = PortalUtil.generateRandomKey(request, "portlet_layout_prototypes_merge_alert") + StringPool.UNDERLINE;
	%>

	<span class="alert alert-warning">
		<liferay-ui:message arguments='<%= new Object[] {mergeFailCount, LanguageUtil.get(request, "page-template")} %>' key="the-propagation-of-changes-from-the-x-has-been-disabled-temporarily-after-x-errors" translateArguments="<%= false %>" />

		<liferay-ui:message arguments="page-template" key="click-reset-to-reset-the-failure-count-and-reenable-propagation" />

		<aui:button id='<%= randomNamespace + "resetButton" %>' useNamespace="<%= false %>" value="reset" />
	</span>

	<aui:script>
		AUI.$('#<%= randomNamespace %>resetButton').on(
			'click',
			function(event) {
				<portlet:actionURL name="/layout_prototype/reset_merge_fail_count_and_merge" var="resetMergeFailCountURL">
					<portlet:param name="redirect" value="<%= redirect %>" />
					<portlet:param name="layoutPrototypeId" value="<%= String.valueOf(layoutPrototype.getLayoutPrototypeId()) %>" />
				</portlet:actionURL>

				submitForm(document.hrefFm, '<%= resetMergeFailCountURL.toString() %>');
			}
		);
	</aui:script>
</c:if>