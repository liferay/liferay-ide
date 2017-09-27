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

package org.liferay.jukebox.asset;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.LayoutConstants;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletURLFactoryUtil;
import com.liferay.portlet.asset.model.AssetRendererFactory;
import com.liferay.portlet.asset.model.BaseAssetRenderer;

import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.liferay.jukebox.model.Album;
import org.liferay.jukebox.portlet.AlbumsPortlet;
import org.liferay.jukebox.service.permission.AlbumPermission;
import org.liferay.jukebox.util.PortletKeys;

/**
 * @author Julio Camarero
 */

public class AlbumAssetRenderer
	extends BaseAssetRenderer implements TrashRenderer {

	public AlbumAssetRenderer(Album album) {
		_album = album;
	}

	@Override
	public String getClassName() {
		return Album.class.getName();
	}

	@Override
	public long getClassPK() {
		return _album.getAlbumId();
	}

	@Override
	public long getGroupId() {
		return _album.getGroupId();
	}

	@Override
	public String getIconPath(ThemeDisplay themeDisplay) {
		return themeDisplay.getPortalURL() +
			"/jukebox-portlet/icons/albums.png";
	}

	public String getPortletId() {
		AssetRendererFactory assetRendererFactory = getAssetRendererFactory();

		return assetRendererFactory.getPortletId();
	}

	@Override
	public String getSummary(Locale locale) {
		String summary = _album.getName();

		if (Validator.isNotNull(_album.getYear())) {
			summary = StringUtil.appendParentheticalSuffix(
				summary, String.valueOf(_album.getYear()));
		}

		return summary;
	}

	@Override
	public String getThumbnailPath(PortletRequest portletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String thumbnailSrc = _album.getImageURL(themeDisplay);

		if (Validator.isNotNull(thumbnailSrc)) {
			return thumbnailSrc;
		}

		return themeDisplay.getPortalURL() +
			"/jukebox-portlet/icons/albums.png";
	}

	@Override
	public String getTitle(Locale locale) {
		return _album.getName();
	}

	public String getType() {
		return AlbumAssetRendererFactory.TYPE;
	}

	@Override
	public PortletURL getURLEdit(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		PortletURL portletURL = liferayPortletResponse.createLiferayPortletURL(
			getControlPanelPlid(liferayPortletRequest),
			AlbumsPortlet.PORTLET_ID, PortletRequest.RENDER_PHASE);

		portletURL.setParameter("jspPage", "/html/albums/edit_album.jsp");
		portletURL.setParameter("albumId", String.valueOf(_album.getAlbumId()));

		return portletURL;
	}

	@Override
	public String getURLViewInContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		String noSuchEntryRedirect) {

		try {
			long plid = PortalUtil.getPlidFromPortletId(
				_album.getGroupId(), PortletKeys.ALBUMS);

			if (plid == LayoutConstants.DEFAULT_PLID) {
				return StringPool.BLANK;
			}

			PortletURL portletURL = PortletURLFactoryUtil.create(
				liferayPortletRequest, PortletKeys.ALBUMS, plid,
				PortletRequest.RENDER_PHASE);

			portletURL.setParameter("jspPage", "/html/albums/view_album.jsp");
			portletURL.setParameter(
				"albumId", String.valueOf(_album.getAlbumId()));

			return portletURL.toString();
		}
		catch (Exception e) {
		}

		return StringPool.BLANK;
	}

	@Override
	public long getUserId() {
		return _album.getUserId();
	}

	@Override
	public String getUserName() {
		return _album.getUserName();
	}

	@Override
	public String getUuid() {
		return _album.getUuid();
	}

	public boolean hasDeletePermission(PermissionChecker permissionChecker)
		throws PortalException, SystemException {

		return AlbumPermission.contains(
			permissionChecker, _album.getAlbumId(), ActionKeys.DELETE);
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker)
		throws PortalException, SystemException {

		return AlbumPermission.contains(
			permissionChecker, _album.getAlbumId(), ActionKeys.UPDATE);
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker)
		throws PortalException, SystemException {

		return AlbumPermission.contains(
			permissionChecker, _album.getAlbumId(), ActionKeys.VIEW);
	}

	@Override
	public boolean isPrintable() {
		return true;
	}

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse,
			String template)
		throws Exception {

		if (template.equals(TEMPLATE_FULL_CONTENT)) {
			renderRequest.setAttribute("jukebox_album", _album);

			return "/html/albums/asset/" + template + ".jsp";
		}
		else {
			return null;
		}
	}

	private Album _album;

}