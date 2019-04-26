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
 * Provides a wrapper for {@link AlbumLocalService}.
 *
 * @author Julio Camarero
 * @see AlbumLocalService
 * @generated
 */
public class AlbumLocalServiceWrapper implements AlbumLocalService,
	ServiceWrapper<AlbumLocalService> {
	public AlbumLocalServiceWrapper(AlbumLocalService albumLocalService) {
		_albumLocalService = albumLocalService;
	}

	/**
	* Adds the album to the database. Also notifies the appropriate model listeners.
	*
	* @param album the album
	* @return the album that was added
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public org.liferay.jukebox.model.Album addAlbum(
		org.liferay.jukebox.model.Album album)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _albumLocalService.addAlbum(album);
	}

	/**
	* Creates a new album with the primary key. Does not add the album to the database.
	*
	* @param albumId the primary key for the new album
	* @return the new album
	*/
	@Override
	public org.liferay.jukebox.model.Album createAlbum(long albumId) {
		return _albumLocalService.createAlbum(albumId);
	}

	/**
	* Deletes the album with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param albumId the primary key of the album
	* @return the album that was removed
	* @throws PortalException if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public org.liferay.jukebox.model.Album deleteAlbum(long albumId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _albumLocalService.deleteAlbum(albumId);
	}

	/**
	* Deletes the album from the database. Also notifies the appropriate model listeners.
	*
	* @param album the album
	* @return the album that was removed
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public org.liferay.jukebox.model.Album deleteAlbum(
		org.liferay.jukebox.model.Album album)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _albumLocalService.deleteAlbum(album);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _albumLocalService.dynamicQuery();
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
		return _albumLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _albumLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _albumLocalService.dynamicQuery(dynamicQuery, start, end,
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
		return _albumLocalService.dynamicQueryCount(dynamicQuery);
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
		return _albumLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public org.liferay.jukebox.model.Album fetchAlbum(long albumId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _albumLocalService.fetchAlbum(albumId);
	}

	/**
	* Returns the album with the matching UUID and company.
	*
	* @param uuid the album's UUID
	* @param companyId the primary key of the company
	* @return the matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public org.liferay.jukebox.model.Album fetchAlbumByUuidAndCompanyId(
		java.lang.String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _albumLocalService.fetchAlbumByUuidAndCompanyId(uuid, companyId);
	}

	/**
	* Returns the album matching the UUID and group.
	*
	* @param uuid the album's UUID
	* @param groupId the primary key of the group
	* @return the matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public org.liferay.jukebox.model.Album fetchAlbumByUuidAndGroupId(
		java.lang.String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _albumLocalService.fetchAlbumByUuidAndGroupId(uuid, groupId);
	}

	/**
	* Returns the album with the primary key.
	*
	* @param albumId the primary key of the album
	* @return the album
	* @throws PortalException if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public org.liferay.jukebox.model.Album getAlbum(long albumId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _albumLocalService.getAlbum(albumId);
	}

	@Override
	public com.liferay.portal.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _albumLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	* Returns the album with the matching UUID and company.
	*
	* @param uuid the album's UUID
	* @param companyId the primary key of the company
	* @return the matching album
	* @throws PortalException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public org.liferay.jukebox.model.Album getAlbumByUuidAndCompanyId(
		java.lang.String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _albumLocalService.getAlbumByUuidAndCompanyId(uuid, companyId);
	}

	/**
	* Returns the album matching the UUID and group.
	*
	* @param uuid the album's UUID
	* @param groupId the primary key of the group
	* @return the matching album
	* @throws PortalException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public org.liferay.jukebox.model.Album getAlbumByUuidAndGroupId(
		java.lang.String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _albumLocalService.getAlbumByUuidAndGroupId(uuid, groupId);
	}

	/**
	* Returns a range of all the albums.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @return the range of albums
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<org.liferay.jukebox.model.Album> getAlbums(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _albumLocalService.getAlbums(start, end);
	}

	/**
	* Returns the number of albums.
	*
	* @return the number of albums
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public int getAlbumsCount()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _albumLocalService.getAlbumsCount();
	}

	/**
	* Updates the album in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param album the album
	* @return the album that was updated
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public org.liferay.jukebox.model.Album updateAlbum(
		org.liferay.jukebox.model.Album album)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _albumLocalService.updateAlbum(album);
	}

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	@Override
	public java.lang.String getBeanIdentifier() {
		return _albumLocalService.getBeanIdentifier();
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	@Override
	public void setBeanIdentifier(java.lang.String beanIdentifier) {
		_albumLocalService.setBeanIdentifier(beanIdentifier);
	}

	@Override
	public java.lang.Object invokeMethod(java.lang.String name,
		java.lang.String[] parameterTypes, java.lang.Object[] arguments)
		throws java.lang.Throwable {
		return _albumLocalService.invokeMethod(name, parameterTypes, arguments);
	}

	@Override
	public org.liferay.jukebox.model.Album addAlbum(long userId, long artistId,
		java.lang.String name, int year, java.io.InputStream inputStream,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _albumLocalService.addAlbum(userId, artistId, name, year,
			inputStream, serviceContext);
	}

	@Override
	public void addEntryResources(org.liferay.jukebox.model.Album album,
		boolean addGroupPermissions, boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		_albumLocalService.addEntryResources(album, addGroupPermissions,
			addGuestPermissions);
	}

	@Override
	public void addEntryResources(org.liferay.jukebox.model.Album album,
		java.lang.String[] groupPermissions, java.lang.String[] guestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		_albumLocalService.addEntryResources(album, groupPermissions,
			guestPermissions);
	}

	@Override
	public void deleteAlbums(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		_albumLocalService.deleteAlbums(groupId);
	}

	@Override
	public java.util.List<org.liferay.jukebox.model.Album> getAlbums(
		long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _albumLocalService.getAlbums(groupId);
	}

	@Override
	public java.util.List<org.liferay.jukebox.model.Album> getAlbums(
		long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _albumLocalService.getAlbums(groupId, start, end);
	}

	@Override
	public java.util.List<org.liferay.jukebox.model.Album> getAlbumsByArtistId(
		long artistId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _albumLocalService.getAlbumsByArtistId(artistId);
	}

	@Override
	public int getAlbumsCount(long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _albumLocalService.getAlbumsCount(groupId);
	}

	@Override
	public org.liferay.jukebox.model.Album moveAlbumToTrash(long userId,
		long albumId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _albumLocalService.moveAlbumToTrash(userId, albumId);
	}

	@Override
	public org.liferay.jukebox.model.Album restoreAlbumFromTrash(long userId,
		long albumId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _albumLocalService.restoreAlbumFromTrash(userId, albumId);
	}

	@Override
	public org.liferay.jukebox.model.Album updateAlbum(long userId,
		long albumId, long artistId, java.lang.String name, int year,
		java.io.InputStream inputStream,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _albumLocalService.updateAlbum(userId, albumId, artistId, name,
			year, inputStream, serviceContext);
	}

	@Override
	public void updateAsset(long userId, org.liferay.jukebox.model.Album album,
		long[] assetCategoryIds, java.lang.String[] assetTagNames,
		long[] assetLinkEntryIds)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		_albumLocalService.updateAsset(userId, album, assetCategoryIds,
			assetTagNames, assetLinkEntryIds);
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	public AlbumLocalService getWrappedAlbumLocalService() {
		return _albumLocalService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	public void setWrappedAlbumLocalService(AlbumLocalService albumLocalService) {
		_albumLocalService = albumLocalService;
	}

	@Override
	public AlbumLocalService getWrappedService() {
		return _albumLocalService;
	}

	@Override
	public void setWrappedService(AlbumLocalService albumLocalService) {
		_albumLocalService = albumLocalService;
	}

	private AlbumLocalService _albumLocalService;
}