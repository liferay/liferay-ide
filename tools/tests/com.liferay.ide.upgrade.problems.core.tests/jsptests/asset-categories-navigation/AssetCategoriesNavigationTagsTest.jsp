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

<%@ include file="/html/portlet/asset_categories_navigation/init.jsp" %>

<%
long portletDisplayDDMTemplateId = PortletDisplayTemplateUtil.getPortletDisplayTemplateDDMTemplateId(displayStyleGroupId, displayStyle);
%>

<c:choose>
	<c:when test="<%= portletDisplayDDMTemplateId > 0 %>">

		<%
		List<AssetVocabulary> ddmTemplateAssetVocabularies = new ArrayList<AssetVocabulary>();

		if (allAssetVocabularies) {
			ddmTemplateAssetVocabularies = assetVocabularies;
		}
		else {
			for (long assetVocabularyId : assetVocabularyIds) {
				try {
					ddmTemplateAssetVocabularies.add(AssetVocabularyServiceUtil.getVocabulary(assetVocabularyId));
				}
				catch (NoSuchVocabularyException nsve) {
				}
			}
		}
		%>

		<%= PortletDisplayTemplateUtil.renderDDMTemplate(pageContext, portletDisplayDDMTemplateId, ddmTemplateAssetVocabularies) %>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="<%= allAssetVocabularies %>">
				<liferay-ui:asset-categories-navigation
					hidePortletWhenEmpty="<%= true %>"
				/>
			</c:when>
		</c:choose>
	</c:otherwise>
</c:choose>