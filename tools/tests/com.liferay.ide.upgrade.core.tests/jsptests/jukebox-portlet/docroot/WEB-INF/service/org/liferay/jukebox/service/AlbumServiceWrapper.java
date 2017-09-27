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

import com.liferay.portal.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link AlbumService}.
 *
 * @author Julio Camarero
 * @see AlbumService
 * @generated
 */
public class AlbumServiceWrapper implements AlbumService,
	ServiceWrapper<AlbumService> {
	public AlbumServiceWrapper(AlbumService albumService) {
		_albumService = albumService;
	}

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	@Override
	public java.lang.String getBeanIdentifier() {
		return _albumService.getBeanIdentifier();
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	@Override
	public void setBeanIdentifier(java.lang.String beanIdentifier) {
		_albumService.setBeanIdentifier(beanIdentifier);
	}

	@Override
	public java.lang.Object invokeMethod(java.lang.String name,
		java.lang.String[] parameterTypes, java.lang.Object[] arguments)
		throws java.lang.Throwable {
		return _albumService.invokeMethod(name, parameterTypes, arguments);
	}

	@Override
	public org.liferay.jukebox.model.Album addAlbum(long artistId,
		java.lang.String name, int year, java.io.InputStream inputStream,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _albumService.addAlbum(artistId, name, year, inputStream,
			serviceContext);
	}

	@Override
	public org.liferay.jukebox.model.Album deleteAlbum(long albumId,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _albumService.deleteAlbum(albumId, serviceContext);
	}

	@Override
	public java.util.List<org.liferay.jukebox.model.Album> getAlbums(
		long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _albumService.getAlbums(groupId);
	}

	@Override
	public java.util.List<org.liferay.jukebox.model.Album> getAlbums(
		long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _albumService.getAlbums(groupId, start, end);
	}

	@Override
	public java.util.List<org.liferay.jukebox.model.Album> getAlbums(
		long groupId, java.lang.String keywords)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _albumService.getAlbums(groupId, keywords);
	}

	@Override
	public java.util.List<org.liferay.jukebox.model.Album> getAlbumsByArtistId(
		long groupId, long artistId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _albumService.getAlbumsByArtistId(groupId, artistId);
	}

	@Override
	public int getAlbumsCount(long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _albumService.getAlbumsCount(groupId);
	}

	@Override
	public int getAlbumsCount(long groupId, java.lang.String keywords)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _albumService.getAlbumsCount(groupId, keywords);
	}

	@Override
	public int getAlbumsCountByArtistId(long groupId, long artistId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _albumService.getAlbumsCountByArtistId(groupId, artistId);
	}

	@Override
	public org.liferay.jukebox.model.Album moveAlbumToTrash(long albumId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _albumService.moveAlbumToTrash(albumId);
	}

	@Override
	public org.liferay.jukebox.model.Album restoreAlbumFromTrash(long albumId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _albumService.restoreAlbumFromTrash(albumId);
	}

	@Override
	public org.liferay.jukebox.model.Album updateAlbum(long albumId,
		long artistId, java.lang.String name, int year,
		java.io.InputStream inputStream,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _albumService.updateAlbum(albumId, artistId, name, year,
			inputStream, serviceContext);
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	public AlbumService getWrappedAlbumService() {
		return _albumService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	public void setWrappedAlbumService(AlbumService albumService) {
		_albumService = albumService;
	}

	@Override
	public AlbumService getWrappedService() {
		return _albumService;
	}

	@Override
	public void setWrappedService(AlbumService albumService) {
		_albumService = albumService;
	}

	private AlbumService _albumService;
}