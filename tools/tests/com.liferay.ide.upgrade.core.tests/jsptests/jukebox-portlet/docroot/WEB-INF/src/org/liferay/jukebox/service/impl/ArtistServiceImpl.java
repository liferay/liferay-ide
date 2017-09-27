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

import org.liferay.jukebox.model.Artist;
import org.liferay.jukebox.service.base.ArtistServiceBaseImpl;
import org.liferay.jukebox.service.permission.ArtistPermission;
import org.liferay.jukebox.service.permission.JukeBoxPermission;

/**
 * The implementation of the artist remote service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link org.liferay.jukebox.service.ArtistService} interface.
 *
 * <p>
 * This is a remote service. Methods of this service are expected to have security checks based on the propagated JAAS credentials because this service can be accessed remotely.
 * </p>
 *
 * @author Julio Camarero
 * @author Sergio González
 * @author Eudaldo Alonso
 * @see org.liferay.jukebox.service.base.ArtistServiceBaseImpl
 * @see org.liferay.jukebox.service.ArtistServiceUtil
 */
public class ArtistServiceImpl extends ArtistServiceBaseImpl {

	public Artist addArtist(
			String name, String bio, InputStream inputStream,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		JukeBoxPermission.check(
			getPermissionChecker(), serviceContext.getScopeGroupId(),
			"ADD_ARTIST");

		return artistLocalService.addArtist(
			getUserId(), name, bio, inputStream, serviceContext);
	}

	public Artist deleteArtist(long artistId, ServiceContext serviceContext)
		throws PortalException, SystemException {

		ArtistPermission.check(
			getPermissionChecker(), artistId, ActionKeys.DELETE);

		return artistLocalService.deleteArtist(artistId);
	}

	public List<Artist> getArtists(long groupId) throws SystemException {
		return artistPersistence.filterFindByG_S(
			groupId, WorkflowConstants.STATUS_APPROVED);
	}

	public List<Artist> getArtists(long groupId, int start, int end)
		throws SystemException {

		return artistPersistence.filterFindByG_S(
			groupId, start, end, WorkflowConstants.STATUS_APPROVED);
	}

	public List<Artist> getArtists(long groupId, String keywords)
		throws SystemException {

		return artistPersistence.filterFindByG_LikeN_S(
			groupId, keywords, WorkflowConstants.STATUS_APPROVED);
	}

	public int getArtistsCount(long groupId) throws SystemException {
		return artistPersistence.filterCountByG_S(
			groupId, WorkflowConstants.STATUS_APPROVED);
	}

	public int getArtistsCount(long groupId, String keywords)
		throws SystemException {

		return artistPersistence.filterCountByG_LikeN_S(
			groupId, keywords, WorkflowConstants.STATUS_APPROVED);
	}

	public Artist updateArtist(
			long artistId, String name, String bio, InputStream inputStream,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		ArtistPermission.check(
			getPermissionChecker(), artistId, ActionKeys.UPDATE);

		return artistLocalService.updateArtist(
			getUserId(), artistId, name, bio, inputStream, serviceContext);
	}

}