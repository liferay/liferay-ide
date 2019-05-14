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

package com.liferay;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portal.util.WebKeys;
import com.liferay.portlet.asset.model.AssetRenderer;
import com.liferay.portlet.asset.model.BaseAssetRendererFactory;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portlet.dynamicdatamapping.service.DDMStructureServiceUtil;
import com.liferay.portlet.dynamicdatamapping.service.permission.DDMStructurePermission;
import com.liferay.portlet.journal.NoSuchArticleException;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalArticleResource;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.journal.service.JournalArticleResourceLocalServiceUtil;
import com.liferay.portlet.journal.service.JournalArticleServiceUtil;
import com.liferay.portlet.journal.service.permission.JournalArticlePermission;
import com.liferay.portlet.journal.service.permission.JournalPermission;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;

/**
 * @author Julio Camarero
 * @author Juan Fernández
 * @author Raymond Augé
 * @author Sergio González
 */
public class JournalArticleAssetRendererFactory
	extends BaseAssetRendererFactory {

	public static final String TYPE = "content";

	@Override
	public AssetRenderer getAssetRenderer(long classPK, int type)
		throws PortalException, SystemException {

		JournalArticle article = null;

		try {
			article = JournalArticleLocalServiceUtil.getArticle(classPK);
		}
		catch (NoSuchArticleException nsae1) {
			JournalArticleResource articleResource =
				JournalArticleResourceLocalServiceUtil.getArticleResource(
					classPK);

			boolean approvedArticleAvailable = true;

			if (type == TYPE_LATEST_APPROVED) {
				try {
					article = JournalArticleLocalServiceUtil.getDisplayArticle(
						articleResource.getGroupId(),
						articleResource.getArticleId());
				}
				catch (NoSuchArticleException nsae2) {
					approvedArticleAvailable = false;
				}
			}

			if ((type != TYPE_LATEST_APPROVED) || !approvedArticleAvailable) {
				article = JournalArticleLocalServiceUtil.getLatestArticle(
					articleResource.getGroupId(),
					articleResource.getArticleId(),
					WorkflowConstants.STATUS_ANY);
			}
		}

		JournalArticleAssetRenderer journalArticleAssetRenderer =
			new JournalArticleAssetRenderer(article);

		journalArticleAssetRenderer.setAssetRendererType(type);

		return journalArticleAssetRenderer;
	}

	@Override
	public AssetRenderer getAssetRenderer(long groupId, String urlTitle)
		throws PortalException, SystemException {

		JournalArticle article =
			JournalArticleServiceUtil.getDisplayArticleByUrlTitle(
				groupId, urlTitle);

		return new JournalArticleAssetRenderer(article);
	}

	@Override
	public String getClassName() {
		return JournalArticle.class.getName();
	}

	@Override
	public List<Tuple> getClassTypeFieldNames(
			long classTypeId, Locale locale, int start, int end)
		throws Exception {

		DDMStructure ddmStructure =
			DDMStructureLocalServiceUtil.getDDMStructure(classTypeId);

		List<Tuple> fieldNames = getDDMStructureFieldNames(
			ddmStructure, locale);

		return ListUtil.subList(fieldNames, start, end);
	}

	@Override
	public int getClassTypeFieldNamesCount(long classTypeId, Locale locale)
		throws Exception {

		DDMStructure ddmStructure =
			DDMStructureLocalServiceUtil.getDDMStructure(classTypeId);

		List<Tuple> fieldNames = getDDMStructureFieldNames(
			ddmStructure, locale);

		return fieldNames.size();
	}

	@Override
	public Map<Long, String> getClassTypes(long[] groupIds, Locale locale)
		throws Exception {

		Map<Long, String> classTypes = new HashMap<Long, String>();

		List<DDMStructure> ddmStructures =
			DDMStructureServiceUtil.getStructures(
				groupIds,
				PortalUtil.getClassNameId(JournalArticle.class.getName()));

		for (DDMStructure ddmStructure : ddmStructures) {
			classTypes.put(
				ddmStructure.getStructureId(), ddmStructure.getName(locale));
		}

		return classTypes;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String getTypeName(Locale locale, boolean hasSubtypes) {
		if (hasSubtypes) {
			return LanguageUtil.get(locale, "basic-web-content");
		}

		return super.getTypeName(locale, hasSubtypes);
	}

	@Override
	public PortletURL getURLAdd(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws PortalException, SystemException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!JournalPermission.contains(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), ActionKeys.ADD_ARTICLE)) {

			return null;
		}

		long classTypeId = GetterUtil.getLong(
			liferayPortletRequest.getAttribute(
				WebKeys.ASSET_RENDERER_FACTORY_CLASS_TYPE_ID));

		if ((classTypeId > 0) &&
			!DDMStructurePermission.contains(
				themeDisplay.getPermissionChecker(), classTypeId,
				ActionKeys.VIEW)) {

			return null;
		}

		PortletURL portletURL = liferayPortletResponse.createRenderURL(
			PortletKeys.JOURNAL);

		portletURL.setParameter("struts_action", "/journal/edit_article");

		return portletURL;
	}

	@Override
	public PortletURL getURLView(
		LiferayPortletResponse liferayPortletResponse,
		WindowState windowState) {

		LiferayPortletURL liferayPortletURL =
			liferayPortletResponse.createLiferayPortletURL(
				PortletKeys.JOURNAL, PortletRequest.RENDER_PHASE);

		try {
			liferayPortletURL.setWindowState(windowState);
		}
		catch (WindowStateException wse) {
		}

		return liferayPortletURL;
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws Exception {

		return JournalArticlePermission.contains(
			permissionChecker, classPK, actionId);
	}

	@Override
	public boolean isLinkable() {
		return _LINKABLE;
	}

	@Override
	protected String getIconPath(ThemeDisplay themeDisplay) {
		return themeDisplay.getPathThemeImages() + "/common/history.png";
	}

	private static final boolean _LINKABLE = true;

}