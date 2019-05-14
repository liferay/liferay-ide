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
 * Provides the remote service utility for Song. This utility wraps
 * {@link org.liferay.jukebox.service.impl.SongServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on a remote server. Methods of this service are expected to have security
 * checks based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Julio Camarero
 * @see SongService
 * @see org.liferay.jukebox.service.base.SongServiceBaseImpl
 * @see org.liferay.jukebox.service.impl.SongServiceImpl
 * @generated
 */
public class SongServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link org.liferay.jukebox.service.impl.SongServiceImpl} and rerun ServiceBuilder to regenerate this class.
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

	public static org.liferay.jukebox.model.Song addSong(long albumId,
		java.lang.String name, java.lang.String songFileName,
		java.io.InputStream songInputStream, java.lang.String lyricsFileName,
		java.io.InputStream lyricsInputStream,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .addSong(albumId, name, songFileName, songInputStream,
			lyricsFileName, lyricsInputStream, serviceContext);
	}

	public static org.liferay.jukebox.model.Song deleteSong(long songId,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().deleteSong(songId, serviceContext);
	}

	public static java.util.List<org.liferay.jukebox.model.Song> getSongs(
		long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getSongs(groupId);
	}

	public static java.util.List<org.liferay.jukebox.model.Song> getSongs(
		long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getSongs(groupId, start, end);
	}

	public static java.util.List<org.liferay.jukebox.model.Song> getSongs(
		long groupId, java.lang.String keywords)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getSongs(groupId, keywords);
	}

	public static java.util.List<org.liferay.jukebox.model.Song> getSongsByAlbumId(
		long groupId, long albumId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getSongsByAlbumId(groupId, albumId);
	}

	public static java.util.List<org.liferay.jukebox.model.Song> getSongsByAlbumId(
		long groupId, long albumId, int status)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getSongsByAlbumId(groupId, albumId, status);
	}

	public static int getSongsCount(long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getSongsCount(groupId);
	}

	public static int getSongsCount(long groupId, java.lang.String keywords)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getSongsCount(groupId, keywords);
	}

	public static int getSongsCountByAlbumId(long groupId, long albumId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getSongsCountByAlbumId(groupId, albumId);
	}

	public static int getSongsCountByAlbumId(long groupId, long albumId,
		int status) throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getSongsCountByAlbumId(groupId, albumId, status);
	}

	public static org.liferay.jukebox.model.Song moveSongToTrash(long songId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().moveSongToTrash(songId);
	}

	public static org.liferay.jukebox.model.Song restoreSongFromTrash(
		long songId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().restoreSongFromTrash(songId);
	}

	public static org.liferay.jukebox.model.Song updateSong(long songId,
		long albumId, java.lang.String name, java.lang.String songFileName,
		java.io.InputStream songInputStream, java.lang.String lyricsFileName,
		java.io.InputStream lyricsInputStream,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .updateSong(songId, albumId, name, songFileName,
			songInputStream, lyricsFileName, lyricsInputStream, serviceContext);
	}

	public static void clearService() {
		_service = null;
	}

	public static SongService getService() {
		if (_service == null) {
			InvokableService invokableService = (InvokableService)PortletBeanLocatorUtil.locate(ClpSerializer.getServletContextName(),
					SongService.class.getName());

			if (invokableService instanceof SongService) {
				_service = (SongService)invokableService;
			}
			else {
				_service = new SongServiceClp(invokableService);
			}

			ReferenceRegistry.registerReference(SongServiceUtil.class,
				"_service");
		}

		return _service;
	}

	/**
	 * @deprecated As of 6.2.0
	 */
	public void setService(SongService service) {
	}

	private static SongService _service;
}