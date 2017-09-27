<c:when>
		<%
		AssetRenderer assetRenderer = assetRendererFactory.getAssetRenderer(classPK);
		PortletRequest liferayPortletRequest = null;
		PortletResponse liferayPortletResponse = null;
		%>
		<liferay-util:include page="<%=  assetRenderer.getPreviewPath(
				liferayPortletRequest, liferayPortletResponse) %>" />

		<c:if test="<%= (classPK > 0) && Validator.isNotNull(className) %>">

			<%
			AssetEntry assetEntry = AssetEntryLocalServiceUtil.getEntry(className, classPK);
			AssetRendererFactory assetRendererFactory = AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(className);
			AssetRenderer assetRenderer = assetRendererFactory.getAssetRenderer(classPK);

			request.setAttribute("add_panel.jsp-assetEntry", assetEntry);
			request.setAttribute("add_panel.jsp-assetRendererFactory", assetRendererFactory);
			request.setAttribute("add_panel.jsp-assetRenderer", assetRenderer);

			%>

			<div id="<portlet:namespace />preview">

			</div>
		</c:if>
</c:when>