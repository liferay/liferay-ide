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

import org.liferay.jukebox.model.Album;

/**
 * The persistence interface for the album service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Julio Camarero
 * @see AlbumPersistenceImpl
 * @see AlbumUtil
 * @generated
 */
public interface AlbumPersistence extends BasePersistence<Album> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link AlbumUtil} to access the album persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	* Returns all the albums where uuid = &#63;.
	*
	* @param uuid the uuid
	* @return the matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByUuid(
		java.lang.String uuid)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the albums where uuid = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param uuid the uuid
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @return the range of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByUuid(
		java.lang.String uuid, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the albums where uuid = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param uuid the uuid
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByUuid(
		java.lang.String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first album in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByUuid_First(
		java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the first album in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByUuid_First(
		java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last album in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByUuid_Last(
		java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the last album in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByUuid_Last(
		java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the albums before and after the current album in the ordered set where uuid = &#63;.
	*
	* @param albumId the primary key of the current album
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album[] findByUuid_PrevAndNext(
		long albumId, java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Removes all the albums where uuid = &#63; from the database.
	*
	* @param uuid the uuid
	* @throws SystemException if a system exception occurred
	*/
	public void removeByUuid(java.lang.String uuid)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of albums where uuid = &#63;.
	*
	* @param uuid the uuid
	* @return the number of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public int countByUuid(java.lang.String uuid)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the album where uuid = &#63; and groupId = &#63; or throws a {@link org.liferay.jukebox.NoSuchAlbumException} if it could not be found.
	*
	* @param uuid the uuid
	* @param groupId the group ID
	* @return the matching album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByUUID_G(java.lang.String uuid,
		long groupId)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the album where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param uuid the uuid
	* @param groupId the group ID
	* @return the matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByUUID_G(
		java.lang.String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the album where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param uuid the uuid
	* @param groupId the group ID
	* @param retrieveFromCache whether to use the finder cache
	* @return the matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByUUID_G(
		java.lang.String uuid, long groupId, boolean retrieveFromCache)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the album where uuid = &#63; and groupId = &#63; from the database.
	*
	* @param uuid the uuid
	* @param groupId the group ID
	* @return the album that was removed
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album removeByUUID_G(
		java.lang.String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the number of albums where uuid = &#63; and groupId = &#63;.
	*
	* @param uuid the uuid
	* @param groupId the group ID
	* @return the number of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public int countByUUID_G(java.lang.String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the albums where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @return the matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByUuid_C(
		java.lang.String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the albums where uuid = &#63; and companyId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @return the range of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByUuid_C(
		java.lang.String uuid, long companyId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the albums where uuid = &#63; and companyId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByUuid_C(
		java.lang.String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first album in the ordered set where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByUuid_C_First(
		java.lang.String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the first album in the ordered set where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByUuid_C_First(
		java.lang.String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last album in the ordered set where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByUuid_C_Last(
		java.lang.String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the last album in the ordered set where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByUuid_C_Last(
		java.lang.String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the albums before and after the current album in the ordered set where uuid = &#63; and companyId = &#63;.
	*
	* @param albumId the primary key of the current album
	* @param uuid the uuid
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album[] findByUuid_C_PrevAndNext(
		long albumId, java.lang.String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Removes all the albums where uuid = &#63; and companyId = &#63; from the database.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @throws SystemException if a system exception occurred
	*/
	public void removeByUuid_C(java.lang.String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of albums where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @return the number of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public int countByUuid_C(java.lang.String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the albums where groupId = &#63;.
	*
	* @param groupId the group ID
	* @return the matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByGroupId(
		long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the albums where groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @return the range of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByGroupId(
		long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the albums where groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first album in the ordered set where groupId = &#63;.
	*
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByGroupId_First(long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the first album in the ordered set where groupId = &#63;.
	*
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByGroupId_First(long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last album in the ordered set where groupId = &#63;.
	*
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByGroupId_Last(long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the last album in the ordered set where groupId = &#63;.
	*
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByGroupId_Last(long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the albums before and after the current album in the ordered set where groupId = &#63;.
	*
	* @param albumId the primary key of the current album
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album[] findByGroupId_PrevAndNext(
		long albumId, long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns all the albums that the user has permission to view where groupId = &#63;.
	*
	* @param groupId the group ID
	* @return the matching albums that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> filterFindByGroupId(
		long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the albums that the user has permission to view where groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @return the range of matching albums that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> filterFindByGroupId(
		long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the albums that the user has permissions to view where groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching albums that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> filterFindByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the albums before and after the current album in the ordered set of albums that the user has permission to view where groupId = &#63;.
	*
	* @param albumId the primary key of the current album
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album[] filterFindByGroupId_PrevAndNext(
		long albumId, long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Removes all the albums where groupId = &#63; from the database.
	*
	* @param groupId the group ID
	* @throws SystemException if a system exception occurred
	*/
	public void removeByGroupId(long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of albums where groupId = &#63;.
	*
	* @param groupId the group ID
	* @return the number of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public int countByGroupId(long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of albums that the user has permission to view where groupId = &#63;.
	*
	* @param groupId the group ID
	* @return the number of matching albums that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public int filterCountByGroupId(long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the albums where userId = &#63;.
	*
	* @param userId the user ID
	* @return the matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByUserId(
		long userId) throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the albums where userId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param userId the user ID
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @return the range of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByUserId(
		long userId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the albums where userId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param userId the user ID
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first album in the ordered set where userId = &#63;.
	*
	* @param userId the user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByUserId_First(long userId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the first album in the ordered set where userId = &#63;.
	*
	* @param userId the user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByUserId_First(long userId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last album in the ordered set where userId = &#63;.
	*
	* @param userId the user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByUserId_Last(long userId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the last album in the ordered set where userId = &#63;.
	*
	* @param userId the user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByUserId_Last(long userId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the albums before and after the current album in the ordered set where userId = &#63;.
	*
	* @param albumId the primary key of the current album
	* @param userId the user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album[] findByUserId_PrevAndNext(
		long albumId, long userId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Removes all the albums where userId = &#63; from the database.
	*
	* @param userId the user ID
	* @throws SystemException if a system exception occurred
	*/
	public void removeByUserId(long userId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of albums where userId = &#63;.
	*
	* @param userId the user ID
	* @return the number of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public int countByUserId(long userId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the albums where companyId = &#63;.
	*
	* @param companyId the company ID
	* @return the matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByCompanyId(
		long companyId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the albums where companyId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param companyId the company ID
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @return the range of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByCompanyId(
		long companyId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the albums where companyId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param companyId the company ID
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first album in the ordered set where companyId = &#63;.
	*
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the first album in the ordered set where companyId = &#63;.
	*
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last album in the ordered set where companyId = &#63;.
	*
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByCompanyId_Last(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the last album in the ordered set where companyId = &#63;.
	*
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByCompanyId_Last(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the albums before and after the current album in the ordered set where companyId = &#63;.
	*
	* @param albumId the primary key of the current album
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album[] findByCompanyId_PrevAndNext(
		long albumId, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Removes all the albums where companyId = &#63; from the database.
	*
	* @param companyId the company ID
	* @throws SystemException if a system exception occurred
	*/
	public void removeByCompanyId(long companyId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of albums where companyId = &#63;.
	*
	* @param companyId the company ID
	* @return the number of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public int countByCompanyId(long companyId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the albums where artistId = &#63;.
	*
	* @param artistId the artist ID
	* @return the matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByArtistId(
		long artistId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the albums where artistId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param artistId the artist ID
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @return the range of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByArtistId(
		long artistId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the albums where artistId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param artistId the artist ID
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByArtistId(
		long artistId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first album in the ordered set where artistId = &#63;.
	*
	* @param artistId the artist ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByArtistId_First(long artistId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the first album in the ordered set where artistId = &#63;.
	*
	* @param artistId the artist ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByArtistId_First(
		long artistId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last album in the ordered set where artistId = &#63;.
	*
	* @param artistId the artist ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByArtistId_Last(long artistId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the last album in the ordered set where artistId = &#63;.
	*
	* @param artistId the artist ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByArtistId_Last(long artistId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the albums before and after the current album in the ordered set where artistId = &#63;.
	*
	* @param albumId the primary key of the current album
	* @param artistId the artist ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album[] findByArtistId_PrevAndNext(
		long albumId, long artistId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Removes all the albums where artistId = &#63; from the database.
	*
	* @param artistId the artist ID
	* @throws SystemException if a system exception occurred
	*/
	public void removeByArtistId(long artistId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of albums where artistId = &#63;.
	*
	* @param artistId the artist ID
	* @return the number of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public int countByArtistId(long artistId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the albums where groupId = &#63; and userId = &#63;.
	*
	* @param groupId the group ID
	* @param userId the user ID
	* @return the matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByG_U(
		long groupId, long userId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the albums where groupId = &#63; and userId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param userId the user ID
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @return the range of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByG_U(
		long groupId, long userId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the albums where groupId = &#63; and userId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param userId the user ID
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByG_U(
		long groupId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first album in the ordered set where groupId = &#63; and userId = &#63;.
	*
	* @param groupId the group ID
	* @param userId the user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByG_U_First(long groupId,
		long userId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the first album in the ordered set where groupId = &#63; and userId = &#63;.
	*
	* @param groupId the group ID
	* @param userId the user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByG_U_First(long groupId,
		long userId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last album in the ordered set where groupId = &#63; and userId = &#63;.
	*
	* @param groupId the group ID
	* @param userId the user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByG_U_Last(long groupId,
		long userId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the last album in the ordered set where groupId = &#63; and userId = &#63;.
	*
	* @param groupId the group ID
	* @param userId the user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByG_U_Last(long groupId,
		long userId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the albums before and after the current album in the ordered set where groupId = &#63; and userId = &#63;.
	*
	* @param albumId the primary key of the current album
	* @param groupId the group ID
	* @param userId the user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album[] findByG_U_PrevAndNext(
		long albumId, long groupId, long userId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns all the albums that the user has permission to view where groupId = &#63; and userId = &#63;.
	*
	* @param groupId the group ID
	* @param userId the user ID
	* @return the matching albums that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> filterFindByG_U(
		long groupId, long userId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the albums that the user has permission to view where groupId = &#63; and userId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param userId the user ID
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @return the range of matching albums that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> filterFindByG_U(
		long groupId, long userId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the albums that the user has permissions to view where groupId = &#63; and userId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param userId the user ID
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching albums that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> filterFindByG_U(
		long groupId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the albums before and after the current album in the ordered set of albums that the user has permission to view where groupId = &#63; and userId = &#63;.
	*
	* @param albumId the primary key of the current album
	* @param groupId the group ID
	* @param userId the user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album[] filterFindByG_U_PrevAndNext(
		long albumId, long groupId, long userId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Removes all the albums where groupId = &#63; and userId = &#63; from the database.
	*
	* @param groupId the group ID
	* @param userId the user ID
	* @throws SystemException if a system exception occurred
	*/
	public void removeByG_U(long groupId, long userId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of albums where groupId = &#63; and userId = &#63;.
	*
	* @param groupId the group ID
	* @param userId the user ID
	* @return the number of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public int countByG_U(long groupId, long userId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of albums that the user has permission to view where groupId = &#63; and userId = &#63;.
	*
	* @param groupId the group ID
	* @param userId the user ID
	* @return the number of matching albums that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public int filterCountByG_U(long groupId, long userId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the albums where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @return the matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByG_S(
		long groupId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the albums where groupId = &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param status the status
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @return the range of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByG_S(
		long groupId, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the albums where groupId = &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param status the status
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByG_S(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first album in the ordered set where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByG_S_First(long groupId,
		int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the first album in the ordered set where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByG_S_First(long groupId,
		int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last album in the ordered set where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByG_S_Last(long groupId,
		int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the last album in the ordered set where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByG_S_Last(long groupId,
		int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the albums before and after the current album in the ordered set where groupId = &#63; and status = &#63;.
	*
	* @param albumId the primary key of the current album
	* @param groupId the group ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album[] findByG_S_PrevAndNext(
		long albumId, long groupId, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns all the albums that the user has permission to view where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @return the matching albums that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> filterFindByG_S(
		long groupId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the albums that the user has permission to view where groupId = &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param status the status
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @return the range of matching albums that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> filterFindByG_S(
		long groupId, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the albums that the user has permissions to view where groupId = &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param status the status
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching albums that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> filterFindByG_S(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the albums before and after the current album in the ordered set of albums that the user has permission to view where groupId = &#63; and status = &#63;.
	*
	* @param albumId the primary key of the current album
	* @param groupId the group ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album[] filterFindByG_S_PrevAndNext(
		long albumId, long groupId, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Removes all the albums where groupId = &#63; and status = &#63; from the database.
	*
	* @param groupId the group ID
	* @param status the status
	* @throws SystemException if a system exception occurred
	*/
	public void removeByG_S(long groupId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of albums where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @return the number of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public int countByG_S(long groupId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of albums that the user has permission to view where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @return the number of matching albums that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public int filterCountByG_S(long groupId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the albums where groupId = &#63; and artistId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param artistId the artist ID
	* @param status the status
	* @return the matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByG_A_S(
		long groupId, long artistId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the albums where groupId = &#63; and artistId = &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param artistId the artist ID
	* @param status the status
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @return the range of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByG_A_S(
		long groupId, long artistId, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the albums where groupId = &#63; and artistId = &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param artistId the artist ID
	* @param status the status
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByG_A_S(
		long groupId, long artistId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first album in the ordered set where groupId = &#63; and artistId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param artistId the artist ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByG_A_S_First(long groupId,
		long artistId, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the first album in the ordered set where groupId = &#63; and artistId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param artistId the artist ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByG_A_S_First(long groupId,
		long artistId, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last album in the ordered set where groupId = &#63; and artistId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param artistId the artist ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByG_A_S_Last(long groupId,
		long artistId, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the last album in the ordered set where groupId = &#63; and artistId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param artistId the artist ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByG_A_S_Last(long groupId,
		long artistId, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the albums before and after the current album in the ordered set where groupId = &#63; and artistId = &#63; and status = &#63;.
	*
	* @param albumId the primary key of the current album
	* @param groupId the group ID
	* @param artistId the artist ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album[] findByG_A_S_PrevAndNext(
		long albumId, long groupId, long artistId, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns all the albums that the user has permission to view where groupId = &#63; and artistId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param artistId the artist ID
	* @param status the status
	* @return the matching albums that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> filterFindByG_A_S(
		long groupId, long artistId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the albums that the user has permission to view where groupId = &#63; and artistId = &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param artistId the artist ID
	* @param status the status
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @return the range of matching albums that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> filterFindByG_A_S(
		long groupId, long artistId, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the albums that the user has permissions to view where groupId = &#63; and artistId = &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param artistId the artist ID
	* @param status the status
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching albums that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> filterFindByG_A_S(
		long groupId, long artistId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the albums before and after the current album in the ordered set of albums that the user has permission to view where groupId = &#63; and artistId = &#63; and status = &#63;.
	*
	* @param albumId the primary key of the current album
	* @param groupId the group ID
	* @param artistId the artist ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album[] filterFindByG_A_S_PrevAndNext(
		long albumId, long groupId, long artistId, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Removes all the albums where groupId = &#63; and artistId = &#63; and status = &#63; from the database.
	*
	* @param groupId the group ID
	* @param artistId the artist ID
	* @param status the status
	* @throws SystemException if a system exception occurred
	*/
	public void removeByG_A_S(long groupId, long artistId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of albums where groupId = &#63; and artistId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param artistId the artist ID
	* @param status the status
	* @return the number of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public int countByG_A_S(long groupId, long artistId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of albums that the user has permission to view where groupId = &#63; and artistId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param artistId the artist ID
	* @param status the status
	* @return the number of matching albums that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public int filterCountByG_A_S(long groupId, long artistId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the albums where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @return the matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByG_LikeN_S(
		long groupId, java.lang.String name, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the albums where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @return the range of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByG_LikeN_S(
		long groupId, java.lang.String name, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the albums where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findByG_LikeN_S(
		long groupId, java.lang.String name, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first album in the ordered set where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByG_LikeN_S_First(long groupId,
		java.lang.String name, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the first album in the ordered set where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByG_LikeN_S_First(
		long groupId, java.lang.String name, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last album in the ordered set where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByG_LikeN_S_Last(long groupId,
		java.lang.String name, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the last album in the ordered set where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching album, or <code>null</code> if a matching album could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByG_LikeN_S_Last(long groupId,
		java.lang.String name, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the albums before and after the current album in the ordered set where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param albumId the primary key of the current album
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album[] findByG_LikeN_S_PrevAndNext(
		long albumId, long groupId, java.lang.String name, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns all the albums that the user has permission to view where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @return the matching albums that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> filterFindByG_LikeN_S(
		long groupId, java.lang.String name, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the albums that the user has permission to view where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @return the range of matching albums that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> filterFindByG_LikeN_S(
		long groupId, java.lang.String name, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the albums that the user has permissions to view where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching albums that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> filterFindByG_LikeN_S(
		long groupId, java.lang.String name, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the albums before and after the current album in the ordered set of albums that the user has permission to view where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param albumId the primary key of the current album
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album[] filterFindByG_LikeN_S_PrevAndNext(
		long albumId, long groupId, java.lang.String name, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Removes all the albums where groupId = &#63; and name LIKE &#63; and status = &#63; from the database.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @throws SystemException if a system exception occurred
	*/
	public void removeByG_LikeN_S(long groupId, java.lang.String name,
		int status) throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of albums where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @return the number of matching albums
	* @throws SystemException if a system exception occurred
	*/
	public int countByG_LikeN_S(long groupId, java.lang.String name, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of albums that the user has permission to view where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @return the number of matching albums that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public int filterCountByG_LikeN_S(long groupId, java.lang.String name,
		int status) throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Caches the album in the entity cache if it is enabled.
	*
	* @param album the album
	*/
	public void cacheResult(org.liferay.jukebox.model.Album album);

	/**
	* Caches the albums in the entity cache if it is enabled.
	*
	* @param albums the albums
	*/
	public void cacheResult(
		java.util.List<org.liferay.jukebox.model.Album> albums);

	/**
	* Creates a new album with the primary key. Does not add the album to the database.
	*
	* @param albumId the primary key for the new album
	* @return the new album
	*/
	public org.liferay.jukebox.model.Album create(long albumId);

	/**
	* Removes the album with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param albumId the primary key of the album
	* @return the album that was removed
	* @throws org.liferay.jukebox.NoSuchAlbumException if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album remove(long albumId)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	public org.liferay.jukebox.model.Album updateImpl(
		org.liferay.jukebox.model.Album album)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the album with the primary key or throws a {@link org.liferay.jukebox.NoSuchAlbumException} if it could not be found.
	*
	* @param albumId the primary key of the album
	* @return the album
	* @throws org.liferay.jukebox.NoSuchAlbumException if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album findByPrimaryKey(long albumId)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchAlbumException;

	/**
	* Returns the album with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param albumId the primary key of the album
	* @return the album, or <code>null</code> if a album with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Album fetchByPrimaryKey(long albumId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the albums.
	*
	* @return the albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findAll()
		throws com.liferay.portal.kernel.exception.SystemException;

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
	public java.util.List<org.liferay.jukebox.model.Album> findAll(int start,
		int end) throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the albums.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.AlbumModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of albums
	* @param end the upper bound of the range of albums (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of albums
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Album> findAll(int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes all the albums from the database.
	*
	* @throws SystemException if a system exception occurred
	*/
	public void removeAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of albums.
	*
	* @return the number of albums
	* @throws SystemException if a system exception occurred
	*/
	public int countAll()
		throws com.liferay.portal.kernel.exception.SystemException;
}