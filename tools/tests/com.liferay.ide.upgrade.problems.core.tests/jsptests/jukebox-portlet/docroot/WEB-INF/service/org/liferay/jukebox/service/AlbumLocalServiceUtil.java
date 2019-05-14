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
 * Provides the local service utility for Album. This utility wraps
 * {@link org.liferay.jukebox.service.impl.AlbumLocalServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Julio Camarero
 * @see AlbumLocalService
 * @see org.liferay.jukebox.service.base.AlbumLocalServiceBaseImpl
 * @see org.liferay.jukebox.service.impl.AlbumLocalServiceImpl
 * @generated
 */
public class AlbumLocalServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link org.liferay.jukebox.service.impl.AlbumLocalServiceImpl} and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	* Adds the album to the database. Also notifies the appropriate model listeners.
	*
	* @param album the album
	* @return the album that was added
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Album addAlbum(
		org.liferay.jukebox.model.Album album)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().addAlbum(album);
	}

	/**
	* Creates a new album with the primary key. Does not add the album to the database.
	*
	* @param albumId the primary key for the new album
	* @return the new album
	*/
	public static org.liferay.jukebox.model.Album createAlbum(long albumId) {
		return getService().createAlbum(albumId);
	}

	/**
	* Deletes the album with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param albumId the primary key of the album
	* @return the album that was removed
	* @throws PortalException if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Album deleteAlbum(long albumId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().deleteAlbum(albumId);
	}

	/**
	* Deletes the album from the database. Also notifies the appropriate model listeners.
	*
	* @param album the album
	* @return the album that was removed
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Album deleteAlbum(
		org.liferay.jukebox.model.Album album)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().deleteAlbum(album);
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
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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

	public static org.liferay.jukebox.model.Album fetchAlbum(long albumId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().fetchAlbum(albumId);
	}

	/**
	* Returns the album with the matching UUID and company.
	*
	* @param uuid the album's UUID
	* @param companyId the primary key of the company
	* @return the matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Album fetchAlbumByUuidAndCompanyId(
		java.lang.String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().fetchAlbumByUuidAndCompanyId(uuid, companyId);
	}

	/**
	* Returns the album matching the UUID and group.
	*
	* @param uuid the album's UUID
	* @param groupId the primary key of the group
	* @return the matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Album fetchAlbumByUuidAndGroupId(
		java.lang.String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().fetchAlbumByUuidAndGroupId(uuid, groupId);
	}

	/**
	* Returns the album with the primary key.
	*
	* @param albumId the primary key of the album
	* @return the album
	* @throws PortalException if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Album getAlbum(long albumId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().getAlbum(albumId);
	}

	public static com.liferay.portal.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().getPersistedModel(primaryKeyObj);
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
	public static org.liferay.jukebox.model.Album getAlbumByUuidAndCompanyId(
		java.lang.String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().getAlbumByUuidAndCompanyId(uuid, companyId);
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
	public static org.liferay.jukebox.model.Album getAlbumByUuidAndGroupId(
		java.lang.String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().getAlbumByUuidAndGroupId(uuid, groupId);
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
	public static java.util.List<org.liferay.jukebox.model.Album> getAlbums(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getAlbums(start, end);
	}

	/**
	* Returns the number of albums.
	*
	* @return the number of albums
	* @throws SystemException if a system exception occurred
	*/
	public static int getAlbumsCount()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getAlbumsCount();
	}

	/**
	* Updates the album in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param album the album
	* @return the album that was updated
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Album updateAlbum(
		org.liferay.jukebox.model.Album album)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().updateAlbum(album);
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

	public static org.liferay.jukebox.model.Album addAlbum(long userId,
		long artistId, java.lang.String name, int year,
		java.io.InputStream inputStream,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .addAlbum(userId, artistId, name, year, inputStream,
			serviceContext);
	}

	public static void addEntryResources(
		org.liferay.jukebox.model.Album album, boolean addGroupPermissions,
		boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		getService()
			.addEntryResources(album, addGroupPermissions, addGuestPermissions);
	}

	public static void addEntryResources(
		org.liferay.jukebox.model.Album album,
		java.lang.String[] groupPermissions, java.lang.String[] guestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		getService().addEntryResources(album, groupPermissions, guestPermissions);
	}

	public static void deleteAlbums(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		getService().deleteAlbums(groupId);
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

	public static java.util.List<org.liferay.jukebox.model.Album> getAlbumsByArtistId(
		long artistId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getAlbumsByArtistId(artistId);
	}

	public static int getAlbumsCount(long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getAlbumsCount(groupId);
	}

	public static org.liferay.jukebox.model.Album moveAlbumToTrash(
		long userId, long albumId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().moveAlbumToTrash(userId, albumId);
	}

	public static org.liferay.jukebox.model.Album restoreAlbumFromTrash(
		long userId, long albumId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().restoreAlbumFromTrash(userId, albumId);
	}

	public static org.liferay.jukebox.model.Album updateAlbum(long userId,
		long albumId, long artistId, java.lang.String name, int year,
		java.io.InputStream inputStream,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .updateAlbum(userId, albumId, artistId, name, year,
			inputStream, serviceContext);
	}

	public static void updateAsset(long userId,
		org.liferay.jukebox.model.Album album, long[] assetCategoryIds,
		java.lang.String[] assetTagNames, long[] assetLinkEntryIds)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		getService()
			.updateAsset(userId, album, assetCategoryIds, assetTagNames,
			assetLinkEntryIds);
	}

	public static void clearService() {
		_service = null;
	}

	public static AlbumLocalService getService() {
		if (_service == null) {
			InvokableLocalService invokableLocalService = (InvokableLocalService)PortletBeanLocatorUtil.locate(ClpSerializer.getServletContextName(),
					AlbumLocalService.class.getName());

			if (invokableLocalService instanceof AlbumLocalService) {
				_service = (AlbumLocalService)invokableLocalService;
			}
			else {
				_service = new AlbumLocalServiceClp(invokableLocalService);
			}

			ReferenceRegistry.registerReference(AlbumLocalServiceUtil.class,
				"_service");
		}

		return _service;
	}

	/**
	 * @deprecated As of 6.2.0
	 */
	public void setService(AlbumLocalService service) {
	}

	private static AlbumLocalService _service;
}