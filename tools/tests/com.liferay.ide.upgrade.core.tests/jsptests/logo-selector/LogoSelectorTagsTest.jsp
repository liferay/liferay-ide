<aui:fieldset>
	<aui:input label="allow-site-administrators-to-use-their-own-logo" name='<%= "settings--" + PropsKeys.COMPANY_SECURITY_SITE_LOGO + "--" %>' type="checkbox" value="<%= company.isSiteLogo() %>" />

	<portlet:renderURL var="editCompanyLogoURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
		<portlet:param name="struts_action" value="/portal_settings/edit_company_logo" />
		<portlet:param name="redirect" value="<%= currentURL %>" />
	</portlet:renderURL>

	<liferay-ui:logo-selector
		defaultLogoURL='<%= themeDisplay.getPathImage() + "/company_logo?img_id=0" %>'
		editLogoURL="<%= editCompanyLogoURL %>"
		imageId="<%= company.getLogoId() %>"
		logoDisplaySelector=".company-logo"
	/>
</aui:fieldset>