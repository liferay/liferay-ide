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
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.asset.model.AssetRenderer;
import com.liferay.portlet.asset.model.BaseAssetRendererFactory;

import org.liferay.jukebox.model.Album;
import org.liferay.jukebox.service.AlbumLocalServiceUtil;
import org.liferay.jukebox.service.permission.AlbumPermission;

/**
 * @author Julio Camarero
 */
public class AlbumAssetRendererFactory extends BaseAssetRendererFactory {

	public static final String CLASS_NAME = Album.class.getName();

	public static final String TYPE = "album";

	public AssetRenderer getAssetRenderer(long classPK, int type)
		throws PortalException, SystemException {

		Album album = AlbumLocalServiceUtil.getAlbum(classPK);

		return new AlbumAssetRenderer(album);
	}

	public String getClassName() {
		return CLASS_NAME;
	}

	public String getType() {
		return TYPE;
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws Exception {

		return AlbumPermission.contains(permissionChecker, classPK, actionId);
	}

	@Override
	public boolean isLinkable() {
		return _LINKABLE;
	}

	@Override
	protected String getIconPath(ThemeDisplay themeDisplay) {
		return themeDisplay.getPortalURL() +
			"/jukebox-portlet/icons/albums.png";
	}

	private static final boolean _LINKABLE = true;

}