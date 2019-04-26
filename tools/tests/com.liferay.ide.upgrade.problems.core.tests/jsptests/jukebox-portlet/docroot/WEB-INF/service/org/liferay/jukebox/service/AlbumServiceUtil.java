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

package org.liferay.jukebox.service;

import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.util.ReferenceRegistry;
import com.liferay.portal.service.InvokableService;

/**
 * Provides the remote service utility for Album. This utility wraps
 * {@link org.liferay.jukebox.service.impl.AlbumServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on a remote server. Methods of this service are expected to have security
 * checks based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Julio Camarero
 * @see AlbumService
 * @see org.liferay.jukebox.service.base.AlbumServiceBaseImpl
 * @see org.liferay.jukebox.service.impl.AlbumServiceImpl
 * @generated
 */
public class AlbumServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link org.liferay.jukebox.service.impl.AlbumServiceImpl} and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	public static java.lang.String getBeanIdentifier() {
		return getService().getBeanIdentifier();
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	public static void setBeanIdentifier(java.lang.String beanIdentifier) {
		getService().setBeanIdentifier(beanIdentifier);
	}

	public static java.lang.Object invokeMethod(java.lang.String name,
		java.lang.String[] parameterTypes, java.lang.Object[] arguments)
		throws java.lang.Throwable {
		return getService().invokeMethod(name, parameterTypes, arguments);
	}

	public static org.liferay.jukebox.model.Album addAlbum(long artistId,
		java.lang.String name, int year, java.io.InputStream inputStream,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .addAlbum(artistId, name, year, inputStream, serviceContext);
	}

	public static org.liferay.jukebox.model.Album deleteAlbum(long albumId,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().deleteAlbum(albumId, serviceContext);
	}

	public static java.util.List<org.liferay.jukebox.model.Album> getAlbums(
		long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getAlbums(groupId);
	}

	public static java.util.List<org.liferay.jukebox.model.Album> getAlbums(
		long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getAlbums(groupId, start, end);
	}

	public static java.util.List<org.liferay.jukebox.model.Album> getAlbums(
		long groupId, java.lang.String keywords)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getAlbums(groupId, keywords);
	}

	public static java.util.List<org.liferay.jukebox.model.Album> getAlbumsByArtistId(
		long groupId, long artistId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getAlbumsByArtistId(groupId, artistId);
	}

	public static int getAlbumsCount(long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getAlbumsCount(groupId);
	}

	public static int getAlbumsCount(long groupId, java.lang.String keywords)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getAlbumsCount(groupId, keywords);
	}

	public static int getAlbumsCountByArtistId(long groupId, long artistId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getAlbumsCountByArtistId(groupId, artistId);
	}

	public static org.liferay.jukebox.model.Album moveAlbumToTrash(long albumId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().moveAlbumToTrash(albumId);
	}

	public static org.liferay.jukebox.model.Album restoreAlbumFromTrash(
		long albumId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().restoreAlbumFromTrash(albumId);
	}

	public static org.liferay.jukebox.model.Album updateAlbum(long albumId,
		long artistId, java.lang.String name, int year,
		java.io.InputStream inputStream,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .updateAlbum(albumId, artistId, name, year, inputStream,
			serviceContext);
	}

	public static void clearService() {
		_service = null;
	}

	public static AlbumService getService() {
		if (_service == null) {
			InvokableService invokableService = (InvokableService)PortletBeanLocatorUtil.locate(ClpSerializer.getServletContextName(),
					AlbumService.class.getName());

			if (invokableService instanceof AlbumService) {
				_service = (AlbumService)invokableService;
			}
			else {
				_service = new AlbumServiceClp(invokableService);
			}

			ReferenceRegistry.registerReference(AlbumServiceUtil.class,
				"_service");
		}

		return _service;
	}

	/**
	 * @deprecated As of 6.2.0
	 */
	public void setService(AlbumService service) {
	}

	private static AlbumService _service;
}