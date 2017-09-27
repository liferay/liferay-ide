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

package org.liferay.jukebox.service.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.staging.permission.StagingPermissionUtil;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.permission.PermissionChecker;

import org.liferay.jukebox.model.Artist;
import org.liferay.jukebox.portlet.AlbumsPortlet;
import org.liferay.jukebox.service.ArtistLocalServiceUtil;

/**
 * @author Julio Camarero
 */
public class ArtistPermission {

	public static void check(
			PermissionChecker permissionChecker, long artistId, String actionId)
		throws PortalException, SystemException {

		if (!contains(permissionChecker, artistId, actionId)) {
			throw new PrincipalException();
		}
	}

	public static boolean contains(
			PermissionChecker permissionChecker, long artistId, String actionId)
		throws PortalException, SystemException {

		Artist artist = ArtistLocalServiceUtil.getArtist(artistId);

		Boolean hasPermission = StagingPermissionUtil.hasPermission(
			permissionChecker, artist.getGroupId(), Artist.class.getName(),
			artist.getArtistId(), AlbumsPortlet.PORTLET_ID, actionId);

		if (hasPermission != null) {
			return hasPermission.booleanValue();
		}

		return permissionChecker.hasPermission(
			artist.getGroupId(), Artist.class.getName(), artist.getArtistId(),
			actionId);
	}

}