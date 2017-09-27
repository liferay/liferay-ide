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
 * Provides a wrapper for {@link SongService}.
 *
 * @author Julio Camarero
 * @see SongService
 * @generated
 */
public class SongServiceWrapper implements SongService,
	ServiceWrapper<SongService> {
	public SongServiceWrapper(SongService songService) {
		_songService = songService;
	}

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	@Override
	public java.lang.String getBeanIdentifier() {
		return _songService.getBeanIdentifier();
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	@Override
	public void setBeanIdentifier(java.lang.String beanIdentifier) {
		_songService.setBeanIdentifier(beanIdentifier);
	}

	@Override
	public java.lang.Object invokeMethod(java.lang.String name,
		java.lang.String[] parameterTypes, java.lang.Object[] arguments)
		throws java.lang.Throwable {
		return _songService.invokeMethod(name, parameterTypes, arguments);
	}

	@Override
	public org.liferay.jukebox.model.Song addSong(long albumId,
		java.lang.String name, java.lang.String songFileName,
		java.io.InputStream songInputStream, java.lang.String lyricsFileName,
		java.io.InputStream lyricsInputStream,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _songService.addSong(albumId, name, songFileName,
			songInputStream, lyricsFileName, lyricsInputStream, serviceContext);
	}

	@Override
	public org.liferay.jukebox.model.Song deleteSong(long songId,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _songService.deleteSong(songId, serviceContext);
	}

	@Override
	public java.util.List<org.liferay.jukebox.model.Song> getSongs(long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songService.getSongs(groupId);
	}

	@Override
	public java.util.List<org.liferay.jukebox.model.Song> getSongs(
		long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songService.getSongs(groupId, start, end);
	}

	@Override
	public java.util.List<org.liferay.jukebox.model.Song> getSongs(
		long groupId, java.lang.String keywords)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songService.getSongs(groupId, keywords);
	}

	@Override
	public java.util.List<org.liferay.jukebox.model.Song> getSongsByAlbumId(
		long groupId, long albumId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songService.getSongsByAlbumId(groupId, albumId);
	}

	@Override
	public java.util.List<org.liferay.jukebox.model.Song> getSongsByAlbumId(
		long groupId, long albumId, int status)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songService.getSongsByAlbumId(groupId, albumId, status);
	}

	@Override
	public int getSongsCount(long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songService.getSongsCount(groupId);
	}

	@Override
	public int getSongsCount(long groupId, java.lang.String keywords)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songService.getSongsCount(groupId, keywords);
	}

	@Override
	public int getSongsCountByAlbumId(long groupId, long albumId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songService.getSongsCountByAlbumId(groupId, albumId);
	}

	@Override
	public int getSongsCountByAlbumId(long groupId, long albumId, int status)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songService.getSongsCountByAlbumId(groupId, albumId, status);
	}

	@Override
	public org.liferay.jukebox.model.Song moveSongToTrash(long songId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _songService.moveSongToTrash(songId);
	}

	@Override
	public org.liferay.jukebox.model.Song restoreSongFromTrash(long songId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _songService.restoreSongFromTrash(songId);
	}

	@Override
	public org.liferay.jukebox.model.Song updateSong(long songId, long albumId,
		java.lang.String name, java.lang.String songFileName,
		java.io.InputStream songInputStream, java.lang.String lyricsFileName,
		java.io.InputStream lyricsInputStream,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _songService.updateSong(songId, albumId, name, songFileName,
			songInputStream, lyricsFileName, lyricsInputStream, serviceContext);
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	public SongService getWrappedSongService() {
		return _songService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	public void setWrappedSongService(SongService songService) {
		_songService = songService;
	}

	@Override
	public SongService getWrappedService() {
		return _songService;
	}

	@Override
	public void setWrappedService(SongService songService) {
		_songService = songService;
	}

	private SongService _songService;
}