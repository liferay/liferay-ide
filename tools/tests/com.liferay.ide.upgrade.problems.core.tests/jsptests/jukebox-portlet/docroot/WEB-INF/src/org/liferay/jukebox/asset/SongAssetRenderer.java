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
import com.liferay.portlet.trash.util.TrashUtil;

import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.liferay.jukebox.model.Song;
import org.liferay.jukebox.portlet.SongsPortlet;
import org.liferay.jukebox.service.permission.SongPermission;
import org.liferay.jukebox.util.PortletKeys;

/**
 * @author Julio Camarero
 */

public class SongAssetRenderer
	extends BaseAssetRenderer implements TrashRenderer {

	public SongAssetRenderer(Song song) {
		_song = song;
	}

	@Override
	public String getClassName() {
		return Song.class.getName();
	}

	@Override
	public long getClassPK() {
		return _song.getSongId();
	}

	@Override
	public long getGroupId() {
		return _song.getGroupId();
	}

	@Override
	public String getIconPath(ThemeDisplay themeDisplay) {
		return themeDisplay.getPortalURL() +
			"/jukebox-portlet/icons/songs.png";
	}

	public String getPortletId() {
		AssetRendererFactory assetRendererFactory = getAssetRendererFactory();

		return assetRendererFactory.getPortletId();
	}

	@Override
	public String getSummary(Locale locale) {
		String summary = _song.getName();

		return summary;
	}

	@Override
	public String getThumbnailPath(PortletRequest portletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String thumbnailSrc = _song.getImageURL(themeDisplay);

		if (Validator.isNotNull(thumbnailSrc)) {
			return thumbnailSrc;
		}

		return themeDisplay.getPortalURL() +
			"/jukebox-portlet/icons/songs.png";
	}

	@Override
	public String getTitle(Locale locale) {
		if (!_song.isInTrash()) {
			return _song.getName();
		}

		return TrashUtil.getOriginalTitle(_song.getName());
	}

	public String getType() {
		return SongAssetRendererFactory.TYPE;
	}

	@Override
	public PortletURL getURLEdit(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		PortletURL portletURL = liferayPortletResponse.createLiferayPortletURL(
			getControlPanelPlid(liferayPortletRequest), SongsPortlet.PORTLET_ID,
			PortletRequest.RENDER_PHASE);

		portletURL.setParameter("jspPage", "/html/songs/edit_song.jsp");
		portletURL.setParameter("songId", String.valueOf(_song.getSongId()));

		return portletURL;
	}

	@Override
	public String getURLViewInContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		String noSuchEntryRedirect) {

		try {
			long plid = PortalUtil.getPlidFromPortletId(
				_song.getGroupId(), PortletKeys.SONGS);

			if (plid == LayoutConstants.DEFAULT_PLID) {
				return StringPool.BLANK;
			}

			PortletURL portletURL = PortletURLFactoryUtil.create(
				liferayPortletRequest, PortletKeys.SONGS, plid,
				PortletRequest.RENDER_PHASE);

			portletURL.setParameter("jspPage", "/html/songs/view_song.jsp");
			portletURL.setParameter(
				"songId", String.valueOf(_song.getSongId()));

			return portletURL.toString();
		}
		catch (Exception e) {
		}

		return StringPool.BLANK;
	}

	@Override
	public long getUserId() {
		return _song.getUserId();
	}

	@Override
	public String getUserName() {
		return _song.getUserName();
	}

	@Override
	public String getUuid() {
		return _song.getUuid();
	}

	public boolean hasDeletePermission(PermissionChecker permissionChecker)
		throws PortalException, SystemException {

		return SongPermission.contains(
			permissionChecker, _song.getSongId(), ActionKeys.DELETE);
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker)
		throws PortalException, SystemException {

		return SongPermission.contains(
			permissionChecker, _song.getSongId(), ActionKeys.UPDATE);
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker)
		throws PortalException, SystemException {

		return SongPermission.contains(
			permissionChecker, _song.getSongId(), ActionKeys.VIEW);
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
			renderRequest.setAttribute("jukebox_song", _song);

			return "/html/songs/asset/" + template + ".jsp";
		}
		else {
			return null;
		}
	}

	private Song _song;

}