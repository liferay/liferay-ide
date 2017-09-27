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

package org.liferay.jukebox.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.service.ServiceContext;

import java.io.InputStream;

import java.util.List;

import org.liferay.jukebox.model.Album;
import org.liferay.jukebox.service.base.AlbumServiceBaseImpl;
import org.liferay.jukebox.service.permission.AlbumPermission;
import org.liferay.jukebox.service.permission.JukeBoxPermission;

/**
 * The implementation of the album remote service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link org.liferay.jukebox.service.AlbumService} interface.
 *
 * <p>
 * This is a remote service. Methods of this service are expected to have security checks based on the propagated JAAS credentials because this service can be accessed remotely.
 * </p>
 *
 * @author Julio Camarero
 * @author Sergio González
 * @author Eudaldo Alonso
 * @see org.liferay.jukebox.service.base.AlbumServiceBaseImpl
 * @see org.liferay.jukebox.service.AlbumServiceUtil
 */
public class AlbumServiceImpl extends AlbumServiceBaseImpl {

	public Album addAlbum(
			long artistId, String name, int year, InputStream inputStream,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		JukeBoxPermission.check(
			getPermissionChecker(), serviceContext.getScopeGroupId(),
			"ADD_ALBUM");

		return albumLocalService.addAlbum(
			getUserId(), artistId, name, year, inputStream, serviceContext);
	}

	public Album deleteAlbum(long albumId, ServiceContext serviceContext)
		throws PortalException, SystemException {

		AlbumPermission.check(
			getPermissionChecker(), albumId, ActionKeys.DELETE);

		return albumLocalService.deleteAlbum(albumId);
	}

	public List<Album> getAlbums(long groupId) throws SystemException {
		return albumPersistence.filterFindByG_S(
			groupId, WorkflowConstants.STATUS_APPROVED);
	}

	public List<Album> getAlbums(long groupId, int start, int end)
		throws SystemException {

		return albumPersistence.filterFindByG_S(
			groupId, WorkflowConstants.STATUS_APPROVED, start, end);
	}

	public List<Album> getAlbums(long groupId, String keywords)
		throws SystemException {

		return albumPersistence.filterFindByG_LikeN_S(
			groupId, keywords, WorkflowConstants.STATUS_APPROVED);
	}

	public List<Album> getAlbumsByArtistId(long groupId, long artistId)
		throws SystemException {

		return albumPersistence.filterFindByG_A_S(
			groupId, artistId, WorkflowConstants.STATUS_APPROVED);
	}

	public int getAlbumsCount(long groupId) throws SystemException {
		return albumPersistence.filterCountByG_S(
			groupId, WorkflowConstants.STATUS_APPROVED);
	}

	public int getAlbumsCount(long groupId, String keywords)
		throws SystemException {

		return albumPersistence.filterCountByG_LikeN_S(
			groupId, keywords, WorkflowConstants.STATUS_APPROVED);
	}

	public int getAlbumsCountByArtistId(long groupId, long artistId)
		throws SystemException {

		return albumPersistence.filterCountByG_A_S(
			groupId, artistId, WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public Album moveAlbumToTrash(long albumId)
		throws PortalException, SystemException {

		Album album = albumPersistence.findByPrimaryKey(albumId);

		AlbumPermission.check(getPermissionChecker(), album, ActionKeys.DELETE);

		return albumLocalService.moveAlbumToTrash(getUserId(), albumId);
	}

	@Override
	public Album restoreAlbumFromTrash(long albumId)
		throws PortalException, SystemException {

		AlbumPermission.check(
			getPermissionChecker(), albumId, ActionKeys.DELETE);

		return albumLocalService.restoreAlbumFromTrash(getUserId(), albumId);
	}

	public Album updateAlbum(
			long albumId, long artistId, String name, int year,
			InputStream inputStream, ServiceContext serviceContext)
		throws PortalException, SystemException {

		AlbumPermission.check(
			getPermissionChecker(), albumId, ActionKeys.UPDATE);

		return albumLocalService.updateAlbum(
			getUserId(), albumId, artistId, name, year, inputStream,
			serviceContext);
	}

}