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

<%@ include file="init.jsp" %>

<liferay-util:body-bottom outputKey="jukebox-css">
	<link href="<%= PortalUtil.getStaticResourceURL(request, PortalUtil.getPathContext() + "/jukebox-portlet/css/jukebox.css") %>" rel="stylesheet" type="text/css" />
</liferay-util:body-bottom>

<liferay-util:body-bottom outputKey="player-files">
	<link href="<%= PortalUtil.getStaticResourceURL(request, PortalUtil.getPathContext() + "/jukebox-portlet/css/inlineplayer.css") %>" rel="stylesheet" type="text/css" />
	<link href="<%= PortalUtil.getStaticResourceURL(request, PortalUtil.getPathContext() + "/jukebox-portlet/css/flashblock.css") %>" rel="stylesheet" type="text/css" />

	<aui:script use="aui-base">
		A.Get.script(
			'<%= HtmlUtil.escapeJS(PortalUtil.getStaticResourceURL(request, PortalUtil.getPathContext() + "/jukebox-portlet/js/soundmanager2-jsmin.js")) %>',
			{
			onEnd: function(o) {
				A.Get.script(
					'<%= HtmlUtil.escapeJS(PortalUtil.getStaticResourceURL(request, PortalUtil.getPathContext() + "/jukebox-portlet/js/inlineplayer.js")) %>',
					{
					onEnd: function(o) {
					}
					}
				);
			}
		});
	</aui:script>
</liferay-util:body-bottom>