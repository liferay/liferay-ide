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
 * Provides a wrapper for {@link SongLocalService}.
 *
 * @author Julio Camarero
 * @see SongLocalService
 * @generated
 */
public class SongLocalServiceWrapper implements SongLocalService,
	ServiceWrapper<SongLocalService> {
	public SongLocalServiceWrapper(SongLocalService songLocalService) {
		_songLocalService = songLocalService;
	}

	/**
	* Adds the song to the database. Also notifies the appropriate model listeners.
	*
	* @param song the song
	* @return the song that was added
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public org.liferay.jukebox.model.Song addSong(
		org.liferay.jukebox.model.Song song)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.addSong(song);
	}

	/**
	* Creates a new song with the primary key. Does not add the song to the database.
	*
	* @param songId the primary key for the new song
	* @return the new song
	*/
	@Override
	public org.liferay.jukebox.model.Song createSong(long songId) {
		return _songLocalService.createSong(songId);
	}

	/**
	* Deletes the song with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param songId the primary key of the song
	* @return the song that was removed
	* @throws PortalException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public org.liferay.jukebox.model.Song deleteSong(long songId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.deleteSong(songId);
	}

	/**
	* Deletes the song from the database. Also notifies the appropriate model listeners.
	*
	* @param song the song
	* @return the song that was removed
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public org.liferay.jukebox.model.Song deleteSong(
		org.liferay.jukebox.model.Song song)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.deleteSong(song);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _songLocalService.dynamicQuery();
	}

	/**
	* Performs a dynamic query on the database and returns the matching rows.
	*
	* @param dynamicQuery the dynamic query
	* @return the matching rows
	* @throws SystemException if a system exception occurred
	*/
	@Override
	@SuppressWarnings("rawtypes")
	public java.util.List dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	@SuppressWarnings("rawtypes")
	public java.util.List dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) throws com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	@SuppressWarnings("rawtypes")
	public java.util.List dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.dynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	* Returns the number of rows that match the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @return the number of rows that match the dynamic query
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	* Returns the number of rows that match the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @param projection the projection to apply to the query
	* @return the number of rows that match the dynamic query
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public org.liferay.jukebox.model.Song fetchSong(long songId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.fetchSong(songId);
	}

	/**
	* Returns the song with the matching UUID and company.
	*
	* @param uuid the song's UUID
	* @param companyId the primary key of the company
	* @return the matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public org.liferay.jukebox.model.Song fetchSongByUuidAndCompanyId(
		java.lang.String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.fetchSongByUuidAndCompanyId(uuid, companyId);
	}

	/**
	* Returns the song matching the UUID and group.
	*
	* @param uuid the song's UUID
	* @param groupId the primary key of the group
	* @return the matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public org.liferay.jukebox.model.Song fetchSongByUuidAndGroupId(
		java.lang.String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.fetchSongByUuidAndGroupId(uuid, groupId);
	}

	/**
	* Returns the song with the primary key.
	*
	* @param songId the primary key of the song
	* @return the song
	* @throws PortalException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public org.liferay.jukebox.model.Song getSong(long songId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.getSong(songId);
	}

	@Override
	public com.liferay.portal.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.getPersistedModel(primaryKeyObj);
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
	@Override
	public org.liferay.jukebox.model.Song getSongByUuidAndCompanyId(
		java.lang.String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.getSongByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public org.liferay.jukebox.model.Song getSongByUuidAndGroupId(
		java.lang.String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.getSongByUuidAndGroupId(uuid, groupId);
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
	@Override
	public java.util.List<org.liferay.jukebox.model.Song> getSongs(int start,
		int end) throws com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.getSongs(start, end);
	}

	/**
	* Returns the number of songs.
	*
	* @return the number of songs
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public int getSongsCount()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.getSongsCount();
	}

	/**
	* Updates the song in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param song the song
	* @return the song that was updated
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public org.liferay.jukebox.model.Song updateSong(
		org.liferay.jukebox.model.Song song)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.updateSong(song);
	}

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	@Override
	public java.lang.String getBeanIdentifier() {
		return _songLocalService.getBeanIdentifier();
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	@Override
	public void setBeanIdentifier(java.lang.String beanIdentifier) {
		_songLocalService.setBeanIdentifier(beanIdentifier);
	}

	@Override
	public java.lang.Object invokeMethod(java.lang.String name,
		java.lang.String[] parameterTypes, java.lang.Object[] arguments)
		throws java.lang.Throwable {
		return _songLocalService.invokeMethod(name, parameterTypes, arguments);
	}

	@Override
	public void addEntryResources(org.liferay.jukebox.model.Song song,
		boolean addGroupPermissions, boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		_songLocalService.addEntryResources(song, addGroupPermissions,
			addGuestPermissions);
	}

	@Override
	public void addEntryResources(org.liferay.jukebox.model.Song song,
		java.lang.String[] groupPermissions, java.lang.String[] guestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		_songLocalService.addEntryResources(song, groupPermissions,
			guestPermissions);
	}

	@Override
	public org.liferay.jukebox.model.Song addSong(long userId, long albumId,
		java.lang.String name, java.lang.String songFileName,
		java.io.InputStream songInputStream, java.lang.String lyricsFileName,
		java.io.InputStream lyricsInputStream,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.addSong(userId, albumId, name, songFileName,
			songInputStream, lyricsFileName, lyricsInputStream, serviceContext);
	}

	@Override
	public org.liferay.jukebox.model.Song getSong(long groupId, long artistId,
		long albumId, java.lang.String name)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.getSong(groupId, artistId, albumId, name);
	}

	@Override
	public java.util.List<org.liferay.jukebox.model.Song> getSongs(long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.getSongs(groupId);
	}

	@Override
	public java.util.List<org.liferay.jukebox.model.Song> getSongs(
		long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.getSongs(groupId, start, end);
	}

	@Override
	public java.util.List<org.liferay.jukebox.model.Song> getSongsByAlbumId(
		long albumId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.getSongsByAlbumId(albumId);
	}

	@Override
	public java.util.List<org.liferay.jukebox.model.Song> getSongsByAlbumId(
		long albumId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.getSongsByAlbumId(albumId, start, end);
	}

	@Override
	public java.util.List<org.liferay.jukebox.model.Song> getSongsByAlbumId(
		long groupId, long albumId, int status)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.getSongsByAlbumId(groupId, albumId, status);
	}

	@Override
	public int getSongsByAlbumIdCount(long albumId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.getSongsByAlbumIdCount(albumId);
	}

	@Override
	public int getSongsCount(long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.getSongsCount(groupId);
	}

	@Override
	public org.liferay.jukebox.model.Song moveSong(long songId, long albumId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.moveSong(songId, albumId);
	}

	@Override
	public org.liferay.jukebox.model.Song moveSongFromTrash(long userId,
		long songId, long albumId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.moveSongFromTrash(userId, songId, albumId);
	}

	@Override
	public org.liferay.jukebox.model.Song moveSongToTrash(long userId,
		org.liferay.jukebox.model.Song song)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.moveSongToTrash(userId, song);
	}

	@Override
	public org.liferay.jukebox.model.Song restoreSongFromTrash(long userId,
		long songId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.restoreSongFromTrash(userId, songId);
	}

	@Override
	public void updateAsset(long userId, org.liferay.jukebox.model.Song song,
		long[] assetCategoryIds, java.lang.String[] assetTagNames,
		long[] assetLinkEntryIds)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		_songLocalService.updateAsset(userId, song, assetCategoryIds,
			assetTagNames, assetLinkEntryIds);
	}

	@Override
	public org.liferay.jukebox.model.Song updateSong(long userId, long songId,
		long albumId, java.lang.String name, java.lang.String songFileName,
		java.io.InputStream songInputStream, java.lang.String lyricsFileName,
		java.io.InputStream lyricsInputStream,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _songLocalService.updateSong(userId, songId, albumId, name,
			songFileName, songInputStream, lyricsFileName, lyricsInputStream,
			serviceContext);
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	public SongLocalService getWrappedSongLocalService() {
		return _songLocalService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	public void setWrappedSongLocalService(SongLocalService songLocalService) {
		_songLocalService = songLocalService;
	}

	@Override
	public SongLocalService getWrappedService() {
		return _songLocalService;
	}

	@Override
	public void setWrappedService(SongLocalService songLocalService) {
		_songLocalService = songLocalService;
	}

	private SongLocalService _songLocalService;
}