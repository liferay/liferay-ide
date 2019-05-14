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
import com.liferay.portal.kernel.trash.TrashActionKeys;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.ContainerModel;
import com.liferay.portal.model.TrashedModel;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletURLFactoryUtil;
import com.liferay.portlet.trash.DuplicateEntryException;
import com.liferay.portlet.trash.TrashEntryConstants;
import com.liferay.portlet.trash.model.TrashEntry;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import org.liferay.jukebox.model.Album;
import org.liferay.jukebox.model.Song;
import org.liferay.jukebox.service.AlbumLocalServiceUtil;
import org.liferay.jukebox.service.SongLocalServiceUtil;
import org.liferay.jukebox.service.permission.AlbumPermission;
import org.liferay.jukebox.service.permission.SongPermission;
import org.liferay.jukebox.util.PortletKeys;

/**
 * Implements trash handling for the songs.
 *
 * @author Sergio González
 */
public class SongTrashHandler extends JukeBoxBaseTrashHandler {

	public void checkDuplicateTrashEntry(
			TrashEntry trashEntry, long containerModelId, String newName)
		throws PortalException, SystemException {

		Song song = SongLocalServiceUtil.getSong(trashEntry.getClassPK());

		if (containerModelId == TrashEntryConstants.DEFAULT_CONTAINER_ID) {
			containerModelId = song.getAlbumId();
		}

		String originalName = trashEntry.getTypeSettingsProperty("title");

		if (Validator.isNotNull(newName)) {
			originalName = newName;
		}

		Song duplicateSong = SongLocalServiceUtil.getSong(
			song.getGroupId(), song.getArtistId(), containerModelId,
			originalName);

		if (duplicateSong != null) {
			DuplicateEntryException dee = new DuplicateEntryException();

			dee.setDuplicateEntryId(duplicateSong.getSongId());
			dee.setOldName(duplicateSong.getName());
			dee.setTrashEntryId(trashEntry.getEntryId());

			throw dee;
		}
	}

	@Override
	public void deleteTrashEntry(long classPK)
		throws PortalException, SystemException {

		SongLocalServiceUtil.deleteSong(classPK);
	}

	@Override
	public String getClassName() {
		return Song.class.getName();
	}

	@Override
	public ContainerModel getParentContainerModel(long classPK)
		throws PortalException, SystemException {

		Song song = SongLocalServiceUtil.getSong(classPK);

		return getContainerModel(song.getAlbumId());
	}

	@Override
	public ContainerModel getParentContainerModel(TrashedModel trashedModel)
		throws PortalException, SystemException {

		Song song = (Song)trashedModel;

		return getContainerModel(song.getAlbumId());
	}

	@Override
	public String getRestoreContainerModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException, SystemException {

		Song song = SongLocalServiceUtil.getSong(classPK);

		PortletURL portletURL = getRestoreURL(portletRequest, classPK);

		portletURL.setParameter("songId", String.valueOf(song.getSongId()));

		return portletURL.toString();
	}

	@Override
	public String getRestoreMessage(
		PortletRequest portletRequest, long classPK) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return themeDisplay.translate("songs");
	}

	@Override
	public String getTrashContainedModelName() {
		return "songs";
	}

	@Override
	public int getTrashContainedModelsCount(long classPK)
		throws PortalException, SystemException {

		Album album = AlbumLocalServiceUtil.getAlbum(classPK);

		return SongLocalServiceUtil.getSongsByAlbumIdCount(classPK);
	}

	@Override
	public List<TrashRenderer> getTrashContainedModelTrashRenderers(
			long classPK, int start, int end)
		throws PortalException, SystemException {

		List<TrashRenderer> trashRenderers = new ArrayList<TrashRenderer>();

		List<Song> songs = SongLocalServiceUtil.getSongsByAlbumId(
			classPK, start, end);

		for (Song song : songs) {
			TrashHandler trashHandler =
				TrashHandlerRegistryUtil.getTrashHandler(Song.class.getName());

			TrashRenderer trashRenderer = trashHandler.getTrashRenderer(
				song.getSongId());

			trashRenderers.add(trashRenderer);
		}

		return trashRenderers;
	}

	@Override
	public TrashEntry getTrashEntry(long classPK)
		throws PortalException, SystemException {

		Song song = SongLocalServiceUtil.getSong(classPK);

		return song.getTrashEntry();
	}

	@Override
	public boolean hasTrashPermission(
			PermissionChecker permissionChecker, long groupId, long classPK,
			String trashActionId)
		throws PortalException, SystemException {

		if (trashActionId.equals(TrashActionKeys.MOVE)) {
			return AlbumPermission.contains(
				permissionChecker, classPK, "ADD_ALBUM");
		}

		return super.hasTrashPermission(
			permissionChecker, groupId, classPK, trashActionId);
	}

	@Override
	public boolean isInTrash(long classPK)
		throws PortalException, SystemException {

		Song song = SongLocalServiceUtil.getSong(classPK);

		return song.isInTrash();
	}

	@Override
	public boolean isInTrashContainer(long classPK)
		throws PortalException, SystemException {

		Song song = SongLocalServiceUtil.getSong(classPK);

		return song.isInTrashContainer();
	}

	@Override
	public boolean isRestorable(long classPK)
		throws PortalException, SystemException {

		Song song = SongLocalServiceUtil.getSong(classPK);

		if ((song.getAlbumId() > 0) &&
			(AlbumLocalServiceUtil.fetchAlbum(song.getAlbumId()) == null)) {

			return false;
		}

		return !song.isInTrashContainer();
	}

	@Override
	public void moveEntry(
			long userId, long classPK, long containerModelId,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		SongLocalServiceUtil.moveSong(classPK, containerModelId);
	}

	@Override
	public void moveTrashEntry(
			long userId, long classPK, long containerId,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		SongLocalServiceUtil.moveSongFromTrash(userId, classPK, containerId);
	}

	@Override
	public void restoreTrashEntry(long userId, long classPK)
		throws PortalException, SystemException {

		SongLocalServiceUtil.restoreSongFromTrash(userId, classPK);
	}

	@Override
	public void updateTitle(long classPK, String name)
		throws PortalException, SystemException {

		Song song = SongLocalServiceUtil.getSong(classPK);

		song.setName(name);

		SongLocalServiceUtil.updateSong(song);
	}

	@Override
	protected long getGroupId(long classPK)
		throws PortalException, SystemException {

		Song song = SongLocalServiceUtil.getSong(classPK);

		return song.getGroupId();
	}

	protected PortletURL getRestoreURL(
			PortletRequest portletRequest, long classPK)
		throws PortalException, SystemException {

		String portletId = PortletKeys.SONGS;

		Song song = SongLocalServiceUtil.getSong(classPK);

		long plid = PortalUtil.getPlidFromPortletId(
			song.getGroupId(), PortletKeys.SONGS);

		PortletURL portletURL = PortletURLFactoryUtil.create(
			portletRequest, portletId, plid, PortletRequest.RENDER_PHASE);

		portletURL.setParameter("jspPage", "/html/songs/view_song.jsp");

		return portletURL;
	}

	@Override
	protected boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws PortalException, SystemException {

		return SongPermission.contains(permissionChecker, classPK, actionId);
	}

}