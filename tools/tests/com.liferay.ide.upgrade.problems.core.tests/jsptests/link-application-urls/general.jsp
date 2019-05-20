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

<%@
page import="com.liferay.portlet.configuration.css.web.internal.display.context.PortletConfigurationCSSPortletDisplayContext" %>

<%
String currentURL = PortalUtil.getCurrentURL(request);

PortletConfigurationCSSPortletDisplayContext portletConfigurationCSSPortletDisplayContext = new PortletConfigurationCSSPortletDisplayContext(renderRequest);
%>

<%
Map<String, Object> contextUseForAllTitle = new HashMap<>();

contextUseForAllTitle.put("checked", portletConfigurationCSSPortletDisplayContext.isUseCustomTitle());
contextUseForAllTitle.put("disableOnChecked", false);
contextUseForAllTitle.put("label", LanguageUtil.get(request, "use-custom-title"));
contextUseForAllTitle.put("name", renderResponse.getNamespace() + "useCustomTitle");
contextUseForAllTitle.put("inputSelector", ".custom-title input");
%>

<soy:component-renderer
	context="<%= contextUseForAllTitle %>"
	module="portlet-configuration-css-web/js/ToggleDisableInputs.es"
	templateNamespace="com.liferay.portlet.configuration.css.web.ToggleDisableInputs.render"
/>

<aui:field-wrapper cssClass="custom-title lfr-input-text-container">
	<liferay-ui:input-localized
		defaultLanguageId="<%= LocaleUtil.toLanguageId(themeDisplay.getSiteDefaultLocale()) %>"
		disabled="<%= !portletConfigurationCSSPortletDisplayContext.isUseCustomTitle() %>"
		name="customTitle"
		xml="<%= portletConfigurationCSSPortletDisplayContext.getCustomTitleXML() %>"
	/>
</aui:field-wrapper>

<c:if test="<%= portletConfigurationCSSPortletDisplayContext.isShowLinkToPage() %>">
	<aui:select label="link-portlet-urls-to-page" name="linkToLayoutUuid">
		<aui:option label="current-page" selected="<%= Objects.equals(StringPool.BLANK, portletConfigurationCSSPortletDisplayContext.getLinkToLayoutUuid()) %>" value="" />

		<%
		for (LayoutDescription layoutDescription : portletConfigurationCSSPortletDisplayContext.getLayoutDescriptions()) {
			Layout layoutDescriptionLayout = LayoutLocalServiceUtil.fetchLayout(layoutDescription.getPlid());
		%>

			<aui:option label="<%= layoutDescription.getDisplayName() %>" selected="<%= Objects.equals(layoutDescriptionLayout.getUuid(), portletConfigurationCSSPortletDisplayContext.getLinkToLayoutUuid()) %>" value="<%= layoutDescriptionLayout.getUuid() %>" />

		<%
		}
		%>

	</aui:select>
</c:if>

<aui:select label="portlet-decorators" name="portletDecoratorId">

	<%
	for (PortletDecorator portletDecorator : theme.getPortletDecorators()) {
	%>

		<aui:option label="<%= portletDecorator.getName() %>" selected="<%= Objects.equals(portletDecorator.getPortletDecoratorId(), portletConfigurationCSSPortletDisplayContext.getPortletDecoratorId()) %>" value="<%= portletDecorator.getPortletDecoratorId() %>" />

	<%
	}
	%>

</aui:select>

<span class="alert alert-info form-hint hide" id="border-note">
	<liferay-ui:message key="this-change-will-only-be-shown-after-you-refresh-the-page" />
</span>