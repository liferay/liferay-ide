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
import com.liferay.portal.kernel.trash.BaseTrashHandler;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.model.ContainerModel;

import java.util.ArrayList;
import java.util.List;

import org.liferay.jukebox.model.Album;
import org.liferay.jukebox.model.Song;
import org.liferay.jukebox.service.AlbumLocalServiceUtil;
import org.liferay.jukebox.service.SongLocalServiceUtil;

/**
 * Represents the trash handler for songs and albums entities.
 *
 * @author Sergio González
 */
public abstract class JukeBoxBaseTrashHandler extends BaseTrashHandler {

	@Override
	public ContainerModel getContainerModel(long containerModelId)
		throws PortalException, SystemException {

		return AlbumLocalServiceUtil.getAlbum(containerModelId);
	}

	@Override
	public String getContainerModelClassName() {
		return Album.class.getName();
	}

	@Override
	public String getContainerModelName() {
		return "album";
	}

	@Override
	public List<ContainerModel> getParentContainerModels(long classPK)
		throws PortalException, SystemException {

		List<ContainerModel> containerModels = new ArrayList<ContainerModel>();

		ContainerModel containerModel = getParentContainerModel(classPK);

		if (containerModel == null) {
			return containerModels;
		}

		containerModels.add(containerModel);

		while (containerModel.getParentContainerModelId() > 0) {
			containerModel = getContainerModel(
				containerModel.getParentContainerModelId());

			if (containerModel == null) {
				break;
			}

			containerModels.add(containerModel);
		}

		return containerModels;
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
	public String getTrashContainerModelName() {
		return "albums";
	}

	@Override
	public boolean isMovable() {
		return true;
	}

	protected abstract long getGroupId(long classPK)
		throws PortalException, SystemException;

}