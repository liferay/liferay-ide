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

package org.liferay.jukebox.trash;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletURLFactoryUtil;
import com.liferay.portlet.trash.model.TrashEntry;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import org.liferay.jukebox.asset.AlbumAssetRenderer;
import org.liferay.jukebox.model.Album;
import org.liferay.jukebox.service.AlbumLocalServiceUtil;
import org.liferay.jukebox.service.permission.AlbumPermission;
import org.liferay.jukebox.util.PortletKeys;

/**
 * Implements trash handling for the albums.
 *
 * @author Sergio González
 */
public class AlbumTrashHandler extends JukeBoxBaseTrashHandler {

	@Override
	public void deleteTrashEntry(long classPK)
		throws PortalException, SystemException {

		AlbumLocalServiceUtil.deleteAlbum(classPK);
	}

	@Override
	public String getClassName() {
		return Album.class.getName();
	}

	@Override
	public String getDeleteMessage() {
		return "found-in-deleted-album-x";
	}

	@Override
	public String getRestoreContainedModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException, SystemException {

		Album album = AlbumLocalServiceUtil.getAlbum(classPK);

		PortletURL portletURL = getRestoreURL(portletRequest, classPK);

		portletURL.setParameter("albumId", String.valueOf(album.getAlbumId()));

		return portletURL.toString();
	}

	@Override
	public String getRestoreMessage(
		PortletRequest portletRequest, long classPK) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return themeDisplay.translate("albums");
	}

	@Override
	public TrashEntry getTrashEntry(long classPK)
		throws PortalException, SystemException {

		Album album = getAlbum(classPK);

		return album.getTrashEntry();
	}

	@Override
	public TrashRenderer getTrashRenderer(long classPK)
		throws PortalException, SystemException {

		Album album = getAlbum(classPK);

		return new AlbumAssetRenderer(album);
	}

	@Override
	public boolean hasTrashPermission(
			PermissionChecker permissionChecker, long groupId, long classPK,
			String trashActionId)
		throws PortalException, SystemException {

		return super.hasTrashPermission(
			permissionChecker, groupId, classPK, trashActionId);
	}

	@Override
	public boolean isContainerModel() {
		return true;
	}

	@Override
	public boolean isInTrash(long classPK)
		throws PortalException, SystemException {

		Album album = AlbumLocalServiceUtil.getAlbum(classPK);

		return album.isInTrash();
	}

	@Override
	public boolean isMovable() {
		return false;
	}

	@Override
	public void restoreTrashEntry(long userId, long classPK)
		throws PortalException, SystemException {

		AlbumLocalServiceUtil.restoreAlbumFromTrash(userId, classPK);
	}

	protected Album getAlbum(long classPK)
		throws PortalException, SystemException {

		return AlbumLocalServiceUtil.getAlbum(classPK);
	}

	@Override
	protected long getGroupId(long classPK)
		throws PortalException, SystemException {

		Album album = getAlbum(classPK);

		return album.getGroupId();
	}

	protected PortletURL getRestoreURL(
			PortletRequest portletRequest, long classPK)
		throws PortalException, SystemException {

		String portletId = PortletKeys.ALBUMS;

		Album album = AlbumLocalServiceUtil.getAlbum(classPK);

		long plid = PortalUtil.getPlidFromPortletId(
			album.getGroupId(), PortletKeys.ALBUMS);

		PortletURL portletURL = PortletURLFactoryUtil.create(
			portletRequest, portletId, plid, PortletRequest.RENDER_PHASE);

		portletURL.setParameter("jspPage", "/html/albums/view_album.jsp");

		return portletURL;
	}

	@Override
	protected boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws PortalException, SystemException {

		return AlbumPermission.contains(permissionChecker, classPK, actionId);
	}

}