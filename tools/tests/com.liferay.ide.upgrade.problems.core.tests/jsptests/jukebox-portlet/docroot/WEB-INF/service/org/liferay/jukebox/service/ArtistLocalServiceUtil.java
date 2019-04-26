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
 * Provides the local service utility for Artist. This utility wraps
 * {@link org.liferay.jukebox.service.impl.ArtistLocalServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Julio Camarero
 * @see ArtistLocalService
 * @see org.liferay.jukebox.service.base.ArtistLocalServiceBaseImpl
 * @see org.liferay.jukebox.service.impl.ArtistLocalServiceImpl
 * @generated
 */
public class ArtistLocalServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link org.liferay.jukebox.service.impl.ArtistLocalServiceImpl} and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	* Adds the artist to the database. Also notifies the appropriate model listeners.
	*
	* @param artist the artist
	* @return the artist that was added
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Artist addArtist(
		org.liferay.jukebox.model.Artist artist)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().addArtist(artist);
	}

	/**
	* Creates a new artist with the primary key. Does not add the artist to the database.
	*
	* @param artistId the primary key for the new artist
	* @return the new artist
	*/
	public static org.liferay.jukebox.model.Artist createArtist(long artistId) {
		return getService().createArtist(artistId);
	}

	/**
	* Deletes the artist with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param artistId the primary key of the artist
	* @return the artist that was removed
	* @throws PortalException if a artist with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Artist deleteArtist(long artistId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().deleteArtist(artistId);
	}

	/**
	* Deletes the artist from the database. Also notifies the appropriate model listeners.
	*
	* @param artist the artist
	* @return the artist that was removed
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Artist deleteArtist(
		org.liferay.jukebox.model.Artist artist)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().deleteArtist(artist);
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
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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

	public static org.liferay.jukebox.model.Artist fetchArtist(long artistId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().fetchArtist(artistId);
	}

	/**
	* Returns the artist with the matching UUID and company.
	*
	* @param uuid the artist's UUID
	* @param companyId the primary key of the company
	* @return the matching artist, or <code>null</code> if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Artist fetchArtistByUuidAndCompanyId(
		java.lang.String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().fetchArtistByUuidAndCompanyId(uuid, companyId);
	}

	/**
	* Returns the artist matching the UUID and group.
	*
	* @param uuid the artist's UUID
	* @param groupId the primary key of the group
	* @return the matching artist, or <code>null</code> if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Artist fetchArtistByUuidAndGroupId(
		java.lang.String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().fetchArtistByUuidAndGroupId(uuid, groupId);
	}

	/**
	* Returns the artist with the primary key.
	*
	* @param artistId the primary key of the artist
	* @return the artist
	* @throws PortalException if a artist with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Artist getArtist(long artistId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().getArtist(artistId);
	}

	public static com.liferay.portal.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	* Returns the artist with the matching UUID and company.
	*
	* @param uuid the artist's UUID
	* @param companyId the primary key of the company
	* @return the matching artist
	* @throws PortalException if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Artist getArtistByUuidAndCompanyId(
		java.lang.String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().getArtistByUuidAndCompanyId(uuid, companyId);
	}

	/**
	* Returns the artist matching the UUID and group.
	*
	* @param uuid the artist's UUID
	* @param groupId the primary key of the group
	* @return the matching artist
	* @throws PortalException if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Artist getArtistByUuidAndGroupId(
		java.lang.String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().getArtistByUuidAndGroupId(uuid, groupId);
	}

	/**
	* Returns a range of all the artists.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @return the range of artists
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<org.liferay.jukebox.model.Artist> getArtists(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getArtists(start, end);
	}

	/**
	* Returns the number of artists.
	*
	* @return the number of artists
	* @throws SystemException if a system exception occurred
	*/
	public static int getArtistsCount()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getArtistsCount();
	}

	/**
	* Updates the artist in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param artist the artist
	* @return the artist that was updated
	* @throws SystemException if a system exception occurred
	*/
	public static org.liferay.jukebox.model.Artist updateArtist(
		org.liferay.jukebox.model.Artist artist)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().updateArtist(artist);
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

	public static org.liferay.jukebox.model.Artist addArtist(long userId,
		java.lang.String name, java.lang.String bio,
		java.io.InputStream inputStream,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .addArtist(userId, name, bio, inputStream, serviceContext);
	}

	public static void addEntryResources(
		org.liferay.jukebox.model.Artist artist, boolean addGroupPermissions,
		boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		getService()
			.addEntryResources(artist, addGroupPermissions, addGuestPermissions);
	}

	public static void addEntryResources(
		org.liferay.jukebox.model.Artist artist,
		java.lang.String[] groupPermissions, java.lang.String[] guestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		getService()
			.addEntryResources(artist, groupPermissions, guestPermissions);
	}

	public static void deleteArtists(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		getService().deleteArtists(groupId);
	}

	public static java.util.List<org.liferay.jukebox.model.Artist> getArtists(
		long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getArtists(groupId);
	}

	public static java.util.List<org.liferay.jukebox.model.Artist> getArtists(
		long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getArtists(groupId, start, end);
	}

	public static int getArtistsCount(long groupId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getArtistsCount(groupId);
	}

	public static org.liferay.jukebox.model.Artist updateArtist(long userId,
		long artistId, java.lang.String name, java.lang.String bio,
		java.io.InputStream inputStream,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .updateArtist(userId, artistId, name, bio, inputStream,
			serviceContext);
	}

	public static void updateAsset(long userId,
		org.liferay.jukebox.model.Artist artist, long[] assetCategoryIds,
		java.lang.String[] assetTagNames, long[] assetLinkEntryIds)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		getService()
			.updateAsset(userId, artist, assetCategoryIds, assetTagNames,
			assetLinkEntryIds);
	}

	public static void clearService() {
		_service = null;
	}

	public static ArtistLocalService getService() {
		if (_service == null) {
			InvokableLocalService invokableLocalService = (InvokableLocalService)PortletBeanLocatorUtil.locate(ClpSerializer.getServletContextName(),
					ArtistLocalService.class.getName());

			if (invokableLocalService instanceof ArtistLocalService) {
				_service = (ArtistLocalService)invokableLocalService;
			}
			else {
				_service = new ArtistLocalServiceClp(invokableLocalService);
			}

			ReferenceRegistry.registerReference(ArtistLocalServiceUtil.class,
				"_service");
		}

		return _service;
	}

	/**
	 * @deprecated As of 6.2.0
	 */
	public void setService(ArtistLocalService service) {
	}

	private static ArtistLocalService _service;
}