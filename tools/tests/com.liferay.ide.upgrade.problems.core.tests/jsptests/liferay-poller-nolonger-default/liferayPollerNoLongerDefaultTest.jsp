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

<c:if test="<%= (layout != null) && LayoutPermissionUtil.contains(permissionChecker, layout, ActionKeys.UPDATE) %>">
	<aui:script>
		Liferay.provide(
			Liferay.Portlet,
			'refreshLayout',
			function(portletBound) {
				if (!portletBound.isStatic) {
					Liferay.Layout.refresh(portletBound);
				}
			},
			['liferay-layout']
		);
	</aui:script>
</c:if>

<aui:script position="inline">
	<c:choose>
		<c:when test="<%= themeDisplay.isStatePopUp() %>">
			Liferay.Util.getTop().Liferay.fire(
				'popupReady',
				{
					doc: document,
					win: window,
					windowName: Liferay.Util.getWindowName()
				}
			);
		</c:when>
		<c:otherwise>

			<%
			String scroll = ParamUtil.getString(request, "scroll");
			%>

			<c:if test="<%= Validator.isNotNull(scroll) %>">
				Liferay.on(
					'allPortletsReady',
					function(event) {
						document.getElementById('<%= HtmlUtil.escape(scroll) %>').scrollIntoView();
					}
				);
			</c:if>
		</c:otherwise>
	</c:choose>

	Liferay.BrowserSelectors.run();
</aui:script>

<aui:script>
	if (Liferay.Data.ICONS_INLINE_SVG) {
		svg4everybody(
			{
				attributeName: 'data-href',
				polyfill: true
			}
		);
	}
</aui:script>

<aui:script require="metal-dom/src/all/dom as dom" sandbox="<%= true %>">
	var focusInPortletHandler = dom.delegate(
		document,
		'focusin',
		'.portlet',
		function(event) {
			dom.addClasses(dom.closest(event.delegateTarget, '.portlet'), 'open');
		}
	);

	var focusOutPortletHandler = dom.delegate(
		document,
		'focusout',
		'.portlet',
		function(event) {
			dom.removeClasses(dom.closest(event.delegateTarget, '.portlet'), 'open');
		}
	);
</aui:script>

<aui:script use="aui-base">
	if (A.UA.mobile) {
		Liferay.Util.addInputCancel();
	}
</aui:script>

<%
Group group = null;

LayoutRevision layoutRevision = null;

if (layout != null) {
	group = layout.getGroup();

	layoutRevision = LayoutStagingUtil.getLayoutRevision(layout);
}
%>

<c:if test="<%= !themeDisplay.isStatePopUp() && !group.isControlPanel() && (layout != null) && (!group.hasStagingGroup() || group.isStagingGroup() || !PropsValues.STAGING_LIVE_GROUP_LOCKING_ENABLED || layoutTypePortlet.isCustomizable()) && (GroupPermissionUtil.contains(permissionChecker, scopeGroupId, ActionKeys.ADD_LAYOUT) || LayoutPermissionUtil.contains(permissionChecker, layout, ActionKeys.UPDATE) || (layoutTypePortlet.isCustomizable() && LayoutPermissionUtil.contains(permissionChecker, layout, ActionKeys.CUSTOMIZE))) %>">
	<c:if test="<%= layout.isTypePortlet() %>">
		<aui:script position="inline">
			Liferay.Data.layoutConfig = {
				container: '#main-content',
				dropNodes: '.portlet-column',

				<c:choose>
					<c:when test="<%= BrowserSnifferUtil.isMobile(request) %>">
						handles: Liferay.Data.PORTLET_TOUCH_DRAG_HANDLE_SELECTOR || ['.portlet-title-default'],
					</c:when>
					<c:otherwise>
						handles: Liferay.Data.PORTLET_DRAG_HANDLE_SELECTOR || ['.portlet-title-default', '.portlet-topper'],
					</c:otherwise>
				</c:choose>

				disabledDropContainerClass: 'portlet-dropzone-disabled',
				dragNodes: '.portlet-boundary:not(.portlet-static)',
				dropContainer: '.portlet-dropzone',
				emptyColumnClass: 'empty',
				invalid: '.portlet-static',
				nestedPortletId: '_<%= PortletKeys.NESTED_PORTLETS %>_INSTANCE',
				portletBoundary: '.portlet-boundary'
			};

			Liferay.fire('dataLayoutConfigReady');
		</aui:script>

		<aui:script use="liferay-layout">
			<%-- aui:script won't accept a null body --%>
		</aui:script>
	</c:if>

	<aui:script use="event-move,liferay-navigation">
		var navBlock = A.one(Liferay.Data.NAV_SELECTOR);

		Liferay.once(
			'initNavigation',
			function() {
				new Liferay.Navigation(
					{
						navBlock: navBlock
					}
				);
			}
		);

		if (navBlock) {
			if (A.UA.touchMobile) {
				var panel = document.querySelector(Liferay.Data.NAV_SELECTOR_MOBILE);

				Liferay.on('liferay.collapse.shown',
					function(event) {
						var panelId = event.panel.geAttribute('id');

						if (panelId === panel.geAttribute('id')) {
							Liferay.fire('initNavigation');
						}
					}
				);
			}
			else {
				navBlock.once(
					['gesturemovestart', 'mousemove'],
					function() {
						Liferay.fire('initNavigation');
					}
				);
			}
		}

		A.on(
			'io:complete',
			function() {
				Liferay.fire('io:complete');
			}
		);
	</aui:script>
</c:if>

<aui:script use="liferay-menu,liferay-notice,liferay-poller">
	new Liferay.Menu();

	var liferayNotices = Liferay.Data.notices;

	for (var i = 1; i < liferayNotices.length; i++) {
		new Liferay.Notice(liferayNotices[i]);
	}

	<c:if test="<%= themeDisplay.isSignedIn() %>">
		Liferay.Poller.init(
			{
				encryptedUserId: '<%= Encryptor.encrypt(company.getKeyObj(), String.valueOf(themeDisplay.getUserId())) %>',
				supportsComet: <%= ServerDetector.isSupportsComet() %>
			}
		);
	</c:if>
</aui:script>

<script type="text/javascript">
	// <![CDATA[

		<%
		String currentURL = PortalUtil.getCurrentURL(request);
		%>

		Liferay.currentURL = '<%= HtmlUtil.escapeJS(currentURL) %>';
		Liferay.currentURLEncoded = '<%= HtmlUtil.escapeJS(URLCodec.encodeURL(currentURL)) %>';

	// ]]>
</script>

<c:if test="<%= layout != null %>">

	<%-- User Inputted Layout and LayoutSet JavaScript --%>

	<%
	LayoutSet layoutSet = themeDisplay.getLayoutSet();

	UnicodeProperties layoutSetSettings = layoutSet.getSettingsProperties();

	UnicodeProperties masterLayoutTypeSettings = new UnicodeProperties();

	if (layout.getMasterLayoutPlid() > 0) {
		Layout masterLayout = LayoutLocalServiceUtil.fetchLayout(layout.getMasterLayoutPlid());

		if (masterLayout != null) {
			masterLayoutTypeSettings = masterLayout.getTypeSettingsProperties();
		}
	}

	UnicodeProperties layoutTypeSettings = layout.getTypeSettingsProperties();
	%>

	<script type="text/javascript">
		// <![CDATA[
			<c:if test="<%= !layout.isTypeControlPanel() %>">
				<%= GetterUtil.getString(layoutSetSettings.getProperty("javascript")) %>

				<%= GetterUtil.getString(masterLayoutTypeSettings.getProperty("javascript")) %>

				<%= GetterUtil.getString(layoutTypeSettings.getProperty("javascript")) %>
			</c:if>
		// ]]>
	</script>
</c:if>

