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
import com.liferay.portal.service.InvokableLocalService;

/**
 * Provides the local service utility for Song. This utility wraps
 * {@link org.liferay.jukebox.service.impl.SongLocalServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Julio Camarero
 * @see SongLocalService
 * @see org.liferay.jukebox.service.base.SongLocalServiceBaseImpl
 * @see org.liferay.jukebox.service.impl.SongLocalServiceImpl
 * @generated
 */
public class SongLocalServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link org.liferay.jukebox.service.impl.SongLocalServiceImpl} and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	* Adds the song to the database. Also notifies the appropriate model listeners.
	*
	* @param song the song
	* @return the song that was added
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Song addSong(
		org.liferay.jukebox.model.Song song)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().addSong(song);
	}

	/**
	* Creates a new song with the primary key. Does not add the song to the database.
	*
	* @param songId the primary key for the new song
	* @return the new song
	*/
	public static org.liferay.jukebox.model.Song createSong(long songId) {
		return getService().createSong(songId);
	}

	/**
	* Deletes the song with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param songId the primary key of the song
	* @return the song that was removed
	* @throws PortalException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Song deleteSong(long songId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().deleteSong(songId);
	}

	/**
	* Deletes the song from the database. Also notifies the appropriate model listeners.
	*
	* @param song the song
	* @return the song that was removed
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Song deleteSong(
		org.liferay.jukebox.model.Song song)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().deleteSong(song);
	}

	public static com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	* Performs a dynamic query on the database and returns the matching rows.
	*
	* @param dynamicQuery the dynamic query
	* @return the matching rows
	* @throws SystemException if a system exception occurred
	*/
	@SuppressWarnings("rawtypes")
	public static java.util.List dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param dynamicQuery the dynamic query
	* @param start the lower bound of the range of model instances
	* @param end the upper bound of the range of model instances (not inclusive)
	* @return the range of matching rows
	* @throws SystemException if a system exception occurred
	*/
	@SuppressWarnings("rawtypes")
	public static java.util.List dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) throws com.liferay.portal.kernel.exception.SystemException {
		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param dynamicQuery the dynamic query
	* @param start the lower bound of the range of model instances
	* @param end the upper bound of the range of model instances (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching rows
	* @throws SystemException if a system exception occurred
	*/
	@SuppressWarnings("rawtypes")
	public static java.util.List dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .dynamicQuery(dynamicQuery, start, end, orderByComparator);
	}

	/**
	* Returns the number of rows that match the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @return the number of rows that match the dynamic query
	* @throws SystemException if a system exception occurred
	*/
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	* Returns the number of rows that match the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @param projection the projection to apply to the query
	* @return the number of rows that match the dynamic query
	* @throws SystemException if a system exception occurred
	*/
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static org.liferay.jukebox.model.Song fetchSong(long songId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().fetchSong(songId);
	}

	/**
	* Returns the song with the matching UUID and company.
	*
	* @param uuid the song's UUID
	* @param companyId the primary key of the company
	* @return the matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Song fetchSongByUuidAndCompanyId(
		java.lang.String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().fetchSongByUuidAndCompanyId(uuid, companyId);
	}

	/**
	* Returns the song matching the UUID and group.
	*
	* @param uuid the song's UUID
	* @param groupId the primary key of the group
	* @return the matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Song fetchSongByUuidAndGroupId(
		java.lang.String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().fetchSongByUuidAndGroupId(uuid, groupId);
	}

	/**
	* Returns the song with the primary key.
	*
	* @param songId the primary key of the song
	* @return the song
	* @throws PortalException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Song getSong(long songId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().getSong(songId);
	}

	public static com.liferay.portal.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	* Returns the song with the matching UUID and company.
	*
	* @param uuid the song's UUID
	* @param companyId the primary key of the company
	* @return the matching song
	* @throws PortalException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Song getSongByUuidAndCompanyId(
		java.lang.String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().getSongByUuidAndCompanyId(uuid, companyId);
	}

	/**
	* Returns the song matching the UUID and group.
	*
	* @param uuid the song's UUID
	* @param groupId the primary key of the group
	* @return the matching song
	* @throws PortalException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Song getSongByUuidAndGroupId(
		java.lang.String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().getSongByUuidAndGroupId(uuid, groupId);
	}

	/**
	* Returns a range of all the songs.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @return the range of songs
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<org.liferay.jukebox.model.Song> getSongs(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getSongs(start, end);
	}

	/**
	* Returns the number of songs.
	*
	* @return the number of songs
	* @throws SystemException if a system exception occurred
	*/
	public static int getSongsCount()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getSongsCount();
	}

	/**
	* Updates the song in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param song the song
	* @return the song that was updated
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Song updateSong(
		org.liferay.jukebox.model.Song song)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().updateSong(song);
	}

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

	public static void addEntryResources(org.liferay.jukebox.model.Song song,
		boolean addGroupPermissions, boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		getService()
			.addEntryResources(song, addGroupPermissions, addGuestPermissions);
	}

	public static void addEntryResources(org.liferay.jukebox.model.Song song,
		java.lang.String[] groupPermissions, java.lang.String[] guestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		getService().addEntryResources(song, groupPermissions, guestPermissions);
	}

	public static org.liferay.jukebox.model.Song addSong(long userId,
		long albumId, java.lang.String name, java.lang.String songFileName,
		java.io.InputStream songInputStream, java.lang.String lyricsFileName,
		java.io.InputStream lyricsInputStream,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .addSong(userId, albumId, name, songFileName,
			songInputStream, lyricsFileName, lyricsInputStream, serviceContext);
	}

	public static org.liferay.jukebox.model.Song getSong(long groupId,
		long artistId, long albumId, java.lang.String name)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getSong(groupId, artistId, albumId, name);
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

	public static java.util.List<org.liferay.jukebox.model.Song> getSongsByAlbumId(
		long albumId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getSongsByAlbumId(albumId);
	}

	public static java.util.List<org.liferay.jukebox.model.Song> getSongsByAlbumId(
		long albumId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getSongsByAlbumId(albumId, start, end);
	}

	public static java.util.List<org.liferay.jukebox.model.Song> getSongsByAlbumId(
		long groupId, long albumId, int status)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getSongsByAlbumId(groupId, albumId, status);
	}

	public static int getSongsByAlbumIdCount(long albumId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getSongsByAlbumIdCount(albumId);
	}

	public static int getSongsCount(long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getSongsCount(groupId);
	}

	public static org.liferay.jukebox.model.Song moveSong(long songId,
		long albumId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().moveSong(songId, albumId);
	}

	public static org.liferay.jukebox.model.Song moveSongFromTrash(
		long userId, long songId, long albumId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().moveSongFromTrash(userId, songId, albumId);
	}

	public static org.liferay.jukebox.model.Song moveSongToTrash(long userId,
		org.liferay.jukebox.model.Song song)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().moveSongToTrash(userId, song);
	}

	public static org.liferay.jukebox.model.Song restoreSongFromTrash(
		long userId, long songId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().restoreSongFromTrash(userId, songId);
	}

	public static void updateAsset(long userId,
		org.liferay.jukebox.model.Song song, long[] assetCategoryIds,
		java.lang.String[] assetTagNames, long[] assetLinkEntryIds)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		getService()
			.updateAsset(userId, song, assetCategoryIds, assetTagNames,
			assetLinkEntryIds);
	}

	public static org.liferay.jukebox.model.Song updateSong(long userId,
		long songId, long albumId, java.lang.String name,
		java.lang.String songFileName, java.io.InputStream songInputStream,
		java.lang.String lyricsFileName, java.io.InputStream lyricsInputStream,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .updateSong(userId, songId, albumId, name, songFileName,
			songInputStream, lyricsFileName, lyricsInputStream, serviceContext);
	}

	public static void clearService() {
		_service = null;
	}

	public static SongLocalService getService() {
		if (_service == null) {
			InvokableLocalService invokableLocalService = (InvokableLocalService)PortletBeanLocatorUtil.locate(ClpSerializer.getServletContextName(),
					SongLocalService.class.getName());

			if (invokableLocalService instanceof SongLocalService) {
				_service = (SongLocalService)invokableLocalService;
			}
			else {
				_service = new SongLocalServiceClp(invokableLocalService);
			}

			ReferenceRegistry.registerReference(SongLocalServiceUtil.class,
				"_service");
		}

		return _service;
	}

	/**
	 * @deprecated As of 6.2.0
	 */
	public void setService(SongLocalService service) {
	}

	private static SongLocalService _service;
}