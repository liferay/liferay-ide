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

package org.liferay.jukebox.service.persistence;

import com.liferay.portal.service.persistence.BasePersistence;

import org.liferay.jukebox.model.Artist;

/**
 * The persistence interface for the artist service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Julio Camarero
 * @see ArtistPersistenceImpl
 * @see ArtistUtil
 * @generated
 */
public interface ArtistPersistence extends BasePersistence<Artist> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ArtistUtil} to access the artist persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	* Returns all the artists where uuid = &#63;.
	*
	* @param uuid the uuid
	* @return the matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByUuid(
		java.lang.String uuid)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the artists where uuid = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param uuid the uuid
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @return the range of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByUuid(
		java.lang.String uuid, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the artists where uuid = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param uuid the uuid
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByUuid(
		java.lang.String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first artist in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist findByUuid_First(
		java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns the first artist in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching artist, or <code>null</code> if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist fetchByUuid_First(
		java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last artist in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist findByUuid_Last(
		java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns the last artist in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching artist, or <code>null</code> if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist fetchByUuid_Last(
		java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the artists before and after the current artist in the ordered set where uuid = &#63;.
	*
	* @param artistId the primary key of the current artist
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a artist with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist[] findByUuid_PrevAndNext(
		long artistId, java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Removes all the artists where uuid = &#63; from the database.
	*
	* @param uuid the uuid
	* @throws SystemException if a system exception occurred
	*/
	public void removeByUuid(java.lang.String uuid)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of artists where uuid = &#63;.
	*
	* @param uuid the uuid
	* @return the number of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public int countByUuid(java.lang.String uuid)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the artist where uuid = &#63; and groupId = &#63; or throws a {@link org.liferay.jukebox.NoSuchArtistException} if it could not be found.
	*
	* @param uuid the uuid
	* @param groupId the group ID
	* @return the matching artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist findByUUID_G(
		java.lang.String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns the artist where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param uuid the uuid
	* @param groupId the group ID
	* @return the matching artist, or <code>null</code> if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist fetchByUUID_G(
		java.lang.String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the artist where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param uuid the uuid
	* @param groupId the group ID
	* @param retrieveFromCache whether to use the finder cache
	* @return the matching artist, or <code>null</code> if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist fetchByUUID_G(
		java.lang.String uuid, long groupId, boolean retrieveFromCache)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the artist where uuid = &#63; and groupId = &#63; from the database.
	*
	* @param uuid the uuid
	* @param groupId the group ID
	* @return the artist that was removed
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist removeByUUID_G(
		java.lang.String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns the number of artists where uuid = &#63; and groupId = &#63;.
	*
	* @param uuid the uuid
	* @param groupId the group ID
	* @return the number of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public int countByUUID_G(java.lang.String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the artists where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @return the matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByUuid_C(
		java.lang.String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the artists where uuid = &#63; and companyId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @return the range of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByUuid_C(
		java.lang.String uuid, long companyId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the artists where uuid = &#63; and companyId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByUuid_C(
		java.lang.String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first artist in the ordered set where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist findByUuid_C_First(
		java.lang.String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns the first artist in the ordered set where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching artist, or <code>null</code> if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist fetchByUuid_C_First(
		java.lang.String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last artist in the ordered set where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist findByUuid_C_Last(
		java.lang.String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns the last artist in the ordered set where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching artist, or <code>null</code> if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist fetchByUuid_C_Last(
		java.lang.String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the artists before and after the current artist in the ordered set where uuid = &#63; and companyId = &#63;.
	*
	* @param artistId the primary key of the current artist
	* @param uuid the uuid
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a artist with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist[] findByUuid_C_PrevAndNext(
		long artistId, java.lang.String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Removes all the artists where uuid = &#63; and companyId = &#63; from the database.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @throws SystemException if a system exception occurred
	*/
	public void removeByUuid_C(java.lang.String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of artists where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @return the number of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public int countByUuid_C(java.lang.String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the artists where groupId = &#63;.
	*
	* @param groupId the group ID
	* @return the matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByGroupId(
		long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the artists where groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @return the range of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByGroupId(
		long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the artists where groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first artist in the ordered set where groupId = &#63;.
	*
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist findByGroupId_First(long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns the first artist in the ordered set where groupId = &#63;.
	*
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching artist, or <code>null</code> if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist fetchByGroupId_First(long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last artist in the ordered set where groupId = &#63;.
	*
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist findByGroupId_Last(long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns the last artist in the ordered set where groupId = &#63;.
	*
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching artist, or <code>null</code> if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist fetchByGroupId_Last(long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the artists before and after the current artist in the ordered set where groupId = &#63;.
	*
	* @param artistId the primary key of the current artist
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a artist with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist[] findByGroupId_PrevAndNext(
		long artistId, long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns all the artists that the user has permission to view where groupId = &#63;.
	*
	* @param groupId the group ID
	* @return the matching artists that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> filterFindByGroupId(
		long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the artists that the user has permission to view where groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @return the range of matching artists that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> filterFindByGroupId(
		long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the artists that the user has permissions to view where groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching artists that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> filterFindByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the artists before and after the current artist in the ordered set of artists that the user has permission to view where groupId = &#63;.
	*
	* @param artistId the primary key of the current artist
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a artist with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist[] filterFindByGroupId_PrevAndNext(
		long artistId, long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Removes all the artists where groupId = &#63; from the database.
	*
	* @param groupId the group ID
	* @throws SystemException if a system exception occurred
	*/
	public void removeByGroupId(long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of artists where groupId = &#63;.
	*
	* @param groupId the group ID
	* @return the number of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public int countByGroupId(long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of artists that the user has permission to view where groupId = &#63;.
	*
	* @param groupId the group ID
	* @return the number of matching artists that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public int filterCountByGroupId(long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the artists where userId = &#63;.
	*
	* @param userId the user ID
	* @return the matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByUserId(
		long userId) throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the artists where userId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param userId the user ID
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @return the range of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByUserId(
		long userId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the artists where userId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param userId the user ID
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first artist in the ordered set where userId = &#63;.
	*
	* @param userId the user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist findByUserId_First(long userId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns the first artist in the ordered set where userId = &#63;.
	*
	* @param userId the user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching artist, or <code>null</code> if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist fetchByUserId_First(long userId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last artist in the ordered set where userId = &#63;.
	*
	* @param userId the user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist findByUserId_Last(long userId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns the last artist in the ordered set where userId = &#63;.
	*
	* @param userId the user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching artist, or <code>null</code> if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist fetchByUserId_Last(long userId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the artists before and after the current artist in the ordered set where userId = &#63;.
	*
	* @param artistId the primary key of the current artist
	* @param userId the user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a artist with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist[] findByUserId_PrevAndNext(
		long artistId, long userId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Removes all the artists where userId = &#63; from the database.
	*
	* @param userId the user ID
	* @throws SystemException if a system exception occurred
	*/
	public void removeByUserId(long userId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of artists where userId = &#63;.
	*
	* @param userId the user ID
	* @return the number of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public int countByUserId(long userId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the artists where companyId = &#63;.
	*
	* @param companyId the company ID
	* @return the matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByCompanyId(
		long companyId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the artists where companyId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param companyId the company ID
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @return the range of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByCompanyId(
		long companyId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the artists where companyId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param companyId the company ID
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first artist in the ordered set where companyId = &#63;.
	*
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist findByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns the first artist in the ordered set where companyId = &#63;.
	*
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching artist, or <code>null</code> if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last artist in the ordered set where companyId = &#63;.
	*
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist findByCompanyId_Last(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns the last artist in the ordered set where companyId = &#63;.
	*
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching artist, or <code>null</code> if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist fetchByCompanyId_Last(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the artists before and after the current artist in the ordered set where companyId = &#63;.
	*
	* @param artistId the primary key of the current artist
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a artist with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist[] findByCompanyId_PrevAndNext(
		long artistId, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Removes all the artists where companyId = &#63; from the database.
	*
	* @param companyId the company ID
	* @throws SystemException if a system exception occurred
	*/
	public void removeByCompanyId(long companyId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of artists where companyId = &#63;.
	*
	* @param companyId the company ID
	* @return the number of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public int countByCompanyId(long companyId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the artists where userId = &#63; and groupId = &#63;.
	*
	* @param userId the user ID
	* @param groupId the group ID
	* @return the matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByU_G(
		long userId, long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the artists where userId = &#63; and groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param userId the user ID
	* @param groupId the group ID
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @return the range of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByU_G(
		long userId, long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the artists where userId = &#63; and groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param userId the user ID
	* @param groupId the group ID
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByU_G(
		long userId, long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first artist in the ordered set where userId = &#63; and groupId = &#63;.
	*
	* @param userId the user ID
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist findByU_G_First(long userId,
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns the first artist in the ordered set where userId = &#63; and groupId = &#63;.
	*
	* @param userId the user ID
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching artist, or <code>null</code> if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist fetchByU_G_First(long userId,
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last artist in the ordered set where userId = &#63; and groupId = &#63;.
	*
	* @param userId the user ID
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist findByU_G_Last(long userId,
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns the last artist in the ordered set where userId = &#63; and groupId = &#63;.
	*
	* @param userId the user ID
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching artist, or <code>null</code> if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist fetchByU_G_Last(long userId,
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the artists before and after the current artist in the ordered set where userId = &#63; and groupId = &#63;.
	*
	* @param artistId the primary key of the current artist
	* @param userId the user ID
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a artist with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist[] findByU_G_PrevAndNext(
		long artistId, long userId, long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns all the artists that the user has permission to view where userId = &#63; and groupId = &#63;.
	*
	* @param userId the user ID
	* @param groupId the group ID
	* @return the matching artists that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> filterFindByU_G(
		long userId, long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the artists that the user has permission to view where userId = &#63; and groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param userId the user ID
	* @param groupId the group ID
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @return the range of matching artists that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> filterFindByU_G(
		long userId, long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the artists that the user has permissions to view where userId = &#63; and groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param userId the user ID
	* @param groupId the group ID
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching artists that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> filterFindByU_G(
		long userId, long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the artists before and after the current artist in the ordered set of artists that the user has permission to view where userId = &#63; and groupId = &#63;.
	*
	* @param artistId the primary key of the current artist
	* @param userId the user ID
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a artist with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist[] filterFindByU_G_PrevAndNext(
		long artistId, long userId, long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Removes all the artists where userId = &#63; and groupId = &#63; from the database.
	*
	* @param userId the user ID
	* @param groupId the group ID
	* @throws SystemException if a system exception occurred
	*/
	public void removeByU_G(long userId, long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of artists where userId = &#63; and groupId = &#63;.
	*
	* @param userId the user ID
	* @param groupId the group ID
	* @return the number of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public int countByU_G(long userId, long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of artists that the user has permission to view where userId = &#63; and groupId = &#63;.
	*
	* @param userId the user ID
	* @param groupId the group ID
	* @return the number of matching artists that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public int filterCountByU_G(long userId, long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the artists where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @return the matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByG_S(
		long groupId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the artists where groupId = &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param status the status
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @return the range of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByG_S(
		long groupId, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the artists where groupId = &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param status the status
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByG_S(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first artist in the ordered set where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist findByG_S_First(long groupId,
		int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns the first artist in the ordered set where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching artist, or <code>null</code> if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist fetchByG_S_First(long groupId,
		int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last artist in the ordered set where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist findByG_S_Last(long groupId,
		int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns the last artist in the ordered set where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching artist, or <code>null</code> if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist fetchByG_S_Last(long groupId,
		int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the artists before and after the current artist in the ordered set where groupId = &#63; and status = &#63;.
	*
	* @param artistId the primary key of the current artist
	* @param groupId the group ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a artist with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist[] findByG_S_PrevAndNext(
		long artistId, long groupId, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns all the artists that the user has permission to view where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @return the matching artists that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> filterFindByG_S(
		long groupId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the artists that the user has permission to view where groupId = &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param status the status
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @return the range of matching artists that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> filterFindByG_S(
		long groupId, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the artists that the user has permissions to view where groupId = &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param status the status
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching artists that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> filterFindByG_S(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the artists before and after the current artist in the ordered set of artists that the user has permission to view where groupId = &#63; and status = &#63;.
	*
	* @param artistId the primary key of the current artist
	* @param groupId the group ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a artist with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist[] filterFindByG_S_PrevAndNext(
		long artistId, long groupId, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Removes all the artists where groupId = &#63; and status = &#63; from the database.
	*
	* @param groupId the group ID
	* @param status the status
	* @throws SystemException if a system exception occurred
	*/
	public void removeByG_S(long groupId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of artists where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @return the number of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public int countByG_S(long groupId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of artists that the user has permission to view where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @return the number of matching artists that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public int filterCountByG_S(long groupId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the artists where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @return the matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByG_LikeN_S(
		long groupId, java.lang.String name, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the artists where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @return the range of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByG_LikeN_S(
		long groupId, java.lang.String name, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the artists where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findByG_LikeN_S(
		long groupId, java.lang.String name, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first artist in the ordered set where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist findByG_LikeN_S_First(
		long groupId, java.lang.String name, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns the first artist in the ordered set where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching artist, or <code>null</code> if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist fetchByG_LikeN_S_First(
		long groupId, java.lang.String name, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last artist in the ordered set where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist findByG_LikeN_S_Last(long groupId,
		java.lang.String name, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns the last artist in the ordered set where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching artist, or <code>null</code> if a matching artist could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist fetchByG_LikeN_S_Last(
		long groupId, java.lang.String name, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the artists before and after the current artist in the ordered set where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param artistId the primary key of the current artist
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a artist with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist[] findByG_LikeN_S_PrevAndNext(
		long artistId, long groupId, java.lang.String name, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns all the artists that the user has permission to view where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @return the matching artists that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> filterFindByG_LikeN_S(
		long groupId, java.lang.String name, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the artists that the user has permission to view where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @return the range of matching artists that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> filterFindByG_LikeN_S(
		long groupId, java.lang.String name, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the artists that the user has permissions to view where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching artists that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> filterFindByG_LikeN_S(
		long groupId, java.lang.String name, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the artists before and after the current artist in the ordered set of artists that the user has permission to view where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param artistId the primary key of the current artist
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a artist with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist[] filterFindByG_LikeN_S_PrevAndNext(
		long artistId, long groupId, java.lang.String name, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Removes all the artists where groupId = &#63; and name LIKE &#63; and status = &#63; from the database.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @throws SystemException if a system exception occurred
	*/
	public void removeByG_LikeN_S(long groupId, java.lang.String name,
		int status) throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of artists where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @return the number of matching artists
	* @throws SystemException if a system exception occurred
	*/
	public int countByG_LikeN_S(long groupId, java.lang.String name, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of artists that the user has permission to view where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @return the number of matching artists that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public int filterCountByG_LikeN_S(long groupId, java.lang.String name,
		int status) throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Caches the artist in the entity cache if it is enabled.
	*
	* @param artist the artist
	*/
	public void cacheResult(org.liferay.jukebox.model.Artist artist);

	/**
	* Caches the artists in the entity cache if it is enabled.
	*
	* @param artists the artists
	*/
	public void cacheResult(
		java.util.List<org.liferay.jukebox.model.Artist> artists);

	/**
	* Creates a new artist with the primary key. Does not add the artist to the database.
	*
	* @param artistId the primary key for the new artist
	* @return the new artist
	*/
	public org.liferay.jukebox.model.Artist create(long artistId);

	/**
	* Removes the artist with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param artistId the primary key of the artist
	* @return the artist that was removed
	* @throws org.liferay.jukebox.NoSuchArtistException if a artist with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist remove(long artistId)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	public org.liferay.jukebox.model.Artist updateImpl(
		org.liferay.jukebox.model.Artist artist)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the artist with the primary key or throws a {@link org.liferay.jukebox.NoSuchArtistException} if it could not be found.
	*
	* @param artistId the primary key of the artist
	* @return the artist
	* @throws org.liferay.jukebox.NoSuchArtistException if a artist with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist findByPrimaryKey(long artistId)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchArtistException;

	/**
	* Returns the artist with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param artistId the primary key of the artist
	* @return the artist, or <code>null</code> if a artist with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Artist fetchByPrimaryKey(long artistId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the artists.
	*
	* @return the artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findAll()
		throws com.liferay.portal.kernel.exception.SystemException;

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
	public java.util.List<org.liferay.jukebox.model.Artist> findAll(int start,
		int end) throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the artists.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.ArtistModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of artists
	* @param end the upper bound of the range of artists (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of artists
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Artist> findAll(int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes all the artists from the database.
	*
	* @throws SystemException if a system exception occurred
	*/
	public void removeAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of artists.
	*
	* @return the number of artists
	* @throws SystemException if a system exception occurred
	*/
	public int countAll()
		throws com.liferay.portal.kernel.exception.SystemException;
}