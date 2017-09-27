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

import org.liferay.jukebox.model.Song;

/**
 * The persistence interface for the song service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Julio Camarero
 * @see SongPersistenceImpl
 * @see SongUtil
 * @generated
 */
public interface SongPersistence extends BasePersistence<Song> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link SongUtil} to access the song persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	* Returns all the songs where uuid = &#63;.
	*
	* @param uuid the uuid
	* @return the matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByUuid(
		java.lang.String uuid)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the songs where uuid = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param uuid the uuid
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @return the range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByUuid(
		java.lang.String uuid, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the songs where uuid = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param uuid the uuid
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByUuid(
		java.lang.String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first song in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByUuid_First(
		java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the first song in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByUuid_First(
		java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last song in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByUuid_Last(
		java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the last song in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByUuid_Last(
		java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the songs before and after the current song in the ordered set where uuid = &#63;.
	*
	* @param songId the primary key of the current song
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next song
	* @throws org.liferay.jukebox.NoSuchSongException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song[] findByUuid_PrevAndNext(
		long songId, java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Removes all the songs where uuid = &#63; from the database.
	*
	* @param uuid the uuid
	* @throws SystemException if a system exception occurred
	*/
	public void removeByUuid(java.lang.String uuid)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of songs where uuid = &#63;.
	*
	* @param uuid the uuid
	* @return the number of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public int countByUuid(java.lang.String uuid)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the song where uuid = &#63; and groupId = &#63; or throws a {@link org.liferay.jukebox.NoSuchSongException} if it could not be found.
	*
	* @param uuid the uuid
	* @param groupId the group ID
	* @return the matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByUUID_G(java.lang.String uuid,
		long groupId)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the song where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param uuid the uuid
	* @param groupId the group ID
	* @return the matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByUUID_G(java.lang.String uuid,
		long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the song where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param uuid the uuid
	* @param groupId the group ID
	* @param retrieveFromCache whether to use the finder cache
	* @return the matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByUUID_G(java.lang.String uuid,
		long groupId, boolean retrieveFromCache)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the song where uuid = &#63; and groupId = &#63; from the database.
	*
	* @param uuid the uuid
	* @param groupId the group ID
	* @return the song that was removed
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song removeByUUID_G(
		java.lang.String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the number of songs where uuid = &#63; and groupId = &#63;.
	*
	* @param uuid the uuid
	* @param groupId the group ID
	* @return the number of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public int countByUUID_G(java.lang.String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the songs where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @return the matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByUuid_C(
		java.lang.String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the songs where uuid = &#63; and companyId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @return the range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByUuid_C(
		java.lang.String uuid, long companyId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the songs where uuid = &#63; and companyId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByUuid_C(
		java.lang.String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first song in the ordered set where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByUuid_C_First(
		java.lang.String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the first song in the ordered set where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByUuid_C_First(
		java.lang.String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last song in the ordered set where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByUuid_C_Last(
		java.lang.String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the last song in the ordered set where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByUuid_C_Last(
		java.lang.String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the songs before and after the current song in the ordered set where uuid = &#63; and companyId = &#63;.
	*
	* @param songId the primary key of the current song
	* @param uuid the uuid
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next song
	* @throws org.liferay.jukebox.NoSuchSongException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song[] findByUuid_C_PrevAndNext(
		long songId, java.lang.String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Removes all the songs where uuid = &#63; and companyId = &#63; from the database.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @throws SystemException if a system exception occurred
	*/
	public void removeByUuid_C(java.lang.String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of songs where uuid = &#63; and companyId = &#63;.
	*
	* @param uuid the uuid
	* @param companyId the company ID
	* @return the number of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public int countByUuid_C(java.lang.String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the songs where groupId = &#63;.
	*
	* @param groupId the group ID
	* @return the matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByGroupId(
		long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the songs where groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @return the range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByGroupId(
		long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the songs where groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first song in the ordered set where groupId = &#63;.
	*
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByGroupId_First(long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the first song in the ordered set where groupId = &#63;.
	*
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByGroupId_First(long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last song in the ordered set where groupId = &#63;.
	*
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByGroupId_Last(long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the last song in the ordered set where groupId = &#63;.
	*
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByGroupId_Last(long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the songs before and after the current song in the ordered set where groupId = &#63;.
	*
	* @param songId the primary key of the current song
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next song
	* @throws org.liferay.jukebox.NoSuchSongException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song[] findByGroupId_PrevAndNext(
		long songId, long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns all the songs that the user has permission to view where groupId = &#63;.
	*
	* @param groupId the group ID
	* @return the matching songs that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> filterFindByGroupId(
		long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the songs that the user has permission to view where groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @return the range of matching songs that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> filterFindByGroupId(
		long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the songs that the user has permissions to view where groupId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching songs that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> filterFindByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the songs before and after the current song in the ordered set of songs that the user has permission to view where groupId = &#63;.
	*
	* @param songId the primary key of the current song
	* @param groupId the group ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next song
	* @throws org.liferay.jukebox.NoSuchSongException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song[] filterFindByGroupId_PrevAndNext(
		long songId, long groupId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Removes all the songs where groupId = &#63; from the database.
	*
	* @param groupId the group ID
	* @throws SystemException if a system exception occurred
	*/
	public void removeByGroupId(long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of songs where groupId = &#63;.
	*
	* @param groupId the group ID
	* @return the number of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public int countByGroupId(long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of songs that the user has permission to view where groupId = &#63;.
	*
	* @param groupId the group ID
	* @return the number of matching songs that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public int filterCountByGroupId(long groupId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the songs where userId = &#63;.
	*
	* @param userId the user ID
	* @return the matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByUserId(
		long userId) throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the songs where userId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param userId the user ID
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @return the range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByUserId(
		long userId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the songs where userId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param userId the user ID
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first song in the ordered set where userId = &#63;.
	*
	* @param userId the user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByUserId_First(long userId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the first song in the ordered set where userId = &#63;.
	*
	* @param userId the user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByUserId_First(long userId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last song in the ordered set where userId = &#63;.
	*
	* @param userId the user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByUserId_Last(long userId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the last song in the ordered set where userId = &#63;.
	*
	* @param userId the user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByUserId_Last(long userId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the songs before and after the current song in the ordered set where userId = &#63;.
	*
	* @param songId the primary key of the current song
	* @param userId the user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next song
	* @throws org.liferay.jukebox.NoSuchSongException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song[] findByUserId_PrevAndNext(
		long songId, long userId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Removes all the songs where userId = &#63; from the database.
	*
	* @param userId the user ID
	* @throws SystemException if a system exception occurred
	*/
	public void removeByUserId(long userId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of songs where userId = &#63;.
	*
	* @param userId the user ID
	* @return the number of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public int countByUserId(long userId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the songs where companyId = &#63;.
	*
	* @param companyId the company ID
	* @return the matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByCompanyId(
		long companyId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the songs where companyId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param companyId the company ID
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @return the range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByCompanyId(
		long companyId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the songs where companyId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param companyId the company ID
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first song in the ordered set where companyId = &#63;.
	*
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the first song in the ordered set where companyId = &#63;.
	*
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last song in the ordered set where companyId = &#63;.
	*
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByCompanyId_Last(long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the last song in the ordered set where companyId = &#63;.
	*
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByCompanyId_Last(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the songs before and after the current song in the ordered set where companyId = &#63;.
	*
	* @param songId the primary key of the current song
	* @param companyId the company ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next song
	* @throws org.liferay.jukebox.NoSuchSongException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song[] findByCompanyId_PrevAndNext(
		long songId, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Removes all the songs where companyId = &#63; from the database.
	*
	* @param companyId the company ID
	* @throws SystemException if a system exception occurred
	*/
	public void removeByCompanyId(long companyId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of songs where companyId = &#63;.
	*
	* @param companyId the company ID
	* @return the number of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public int countByCompanyId(long companyId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the songs where artistId = &#63;.
	*
	* @param artistId the artist ID
	* @return the matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByArtistId(
		long artistId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the songs where artistId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param artistId the artist ID
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @return the range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByArtistId(
		long artistId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the songs where artistId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param artistId the artist ID
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByArtistId(
		long artistId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first song in the ordered set where artistId = &#63;.
	*
	* @param artistId the artist ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByArtistId_First(long artistId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the first song in the ordered set where artistId = &#63;.
	*
	* @param artistId the artist ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByArtistId_First(long artistId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last song in the ordered set where artistId = &#63;.
	*
	* @param artistId the artist ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByArtistId_Last(long artistId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the last song in the ordered set where artistId = &#63;.
	*
	* @param artistId the artist ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByArtistId_Last(long artistId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the songs before and after the current song in the ordered set where artistId = &#63;.
	*
	* @param songId the primary key of the current song
	* @param artistId the artist ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next song
	* @throws org.liferay.jukebox.NoSuchSongException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song[] findByArtistId_PrevAndNext(
		long songId, long artistId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Removes all the songs where artistId = &#63; from the database.
	*
	* @param artistId the artist ID
	* @throws SystemException if a system exception occurred
	*/
	public void removeByArtistId(long artistId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of songs where artistId = &#63;.
	*
	* @param artistId the artist ID
	* @return the number of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public int countByArtistId(long artistId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the songs where albumId = &#63;.
	*
	* @param albumId the album ID
	* @return the matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByAlbumId(
		long albumId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the songs where albumId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param albumId the album ID
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @return the range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByAlbumId(
		long albumId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the songs where albumId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param albumId the album ID
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByAlbumId(
		long albumId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first song in the ordered set where albumId = &#63;.
	*
	* @param albumId the album ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByAlbumId_First(long albumId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the first song in the ordered set where albumId = &#63;.
	*
	* @param albumId the album ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByAlbumId_First(long albumId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last song in the ordered set where albumId = &#63;.
	*
	* @param albumId the album ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByAlbumId_Last(long albumId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the last song in the ordered set where albumId = &#63;.
	*
	* @param albumId the album ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByAlbumId_Last(long albumId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the songs before and after the current song in the ordered set where albumId = &#63;.
	*
	* @param songId the primary key of the current song
	* @param albumId the album ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next song
	* @throws org.liferay.jukebox.NoSuchSongException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song[] findByAlbumId_PrevAndNext(
		long songId, long albumId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Removes all the songs where albumId = &#63; from the database.
	*
	* @param albumId the album ID
	* @throws SystemException if a system exception occurred
	*/
	public void removeByAlbumId(long albumId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of songs where albumId = &#63;.
	*
	* @param albumId the album ID
	* @return the number of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public int countByAlbumId(long albumId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the songs where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @return the matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByG_S(
		long groupId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the songs where groupId = &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param status the status
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @return the range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByG_S(
		long groupId, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the songs where groupId = &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param status the status
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByG_S(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first song in the ordered set where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByG_S_First(long groupId,
		int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the first song in the ordered set where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByG_S_First(long groupId,
		int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last song in the ordered set where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByG_S_Last(long groupId,
		int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the last song in the ordered set where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByG_S_Last(long groupId,
		int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the songs before and after the current song in the ordered set where groupId = &#63; and status = &#63;.
	*
	* @param songId the primary key of the current song
	* @param groupId the group ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next song
	* @throws org.liferay.jukebox.NoSuchSongException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song[] findByG_S_PrevAndNext(long songId,
		long groupId, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns all the songs that the user has permission to view where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @return the matching songs that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> filterFindByG_S(
		long groupId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the songs that the user has permission to view where groupId = &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param status the status
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @return the range of matching songs that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> filterFindByG_S(
		long groupId, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the songs that the user has permissions to view where groupId = &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param status the status
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching songs that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> filterFindByG_S(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the songs before and after the current song in the ordered set of songs that the user has permission to view where groupId = &#63; and status = &#63;.
	*
	* @param songId the primary key of the current song
	* @param groupId the group ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next song
	* @throws org.liferay.jukebox.NoSuchSongException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song[] filterFindByG_S_PrevAndNext(
		long songId, long groupId, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Removes all the songs where groupId = &#63; and status = &#63; from the database.
	*
	* @param groupId the group ID
	* @param status the status
	* @throws SystemException if a system exception occurred
	*/
	public void removeByG_S(long groupId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of songs where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @return the number of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public int countByG_S(long groupId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of songs that the user has permission to view where groupId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param status the status
	* @return the number of matching songs that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public int filterCountByG_S(long groupId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the songs where groupId = &#63; and albumId = &#63;.
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @return the matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByG_A(
		long groupId, long albumId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the songs where groupId = &#63; and albumId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @return the range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByG_A(
		long groupId, long albumId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the songs where groupId = &#63; and albumId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByG_A(
		long groupId, long albumId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first song in the ordered set where groupId = &#63; and albumId = &#63;.
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByG_A_First(long groupId,
		long albumId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the first song in the ordered set where groupId = &#63; and albumId = &#63;.
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByG_A_First(long groupId,
		long albumId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last song in the ordered set where groupId = &#63; and albumId = &#63;.
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByG_A_Last(long groupId,
		long albumId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the last song in the ordered set where groupId = &#63; and albumId = &#63;.
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByG_A_Last(long groupId,
		long albumId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the songs before and after the current song in the ordered set where groupId = &#63; and albumId = &#63;.
	*
	* @param songId the primary key of the current song
	* @param groupId the group ID
	* @param albumId the album ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next song
	* @throws org.liferay.jukebox.NoSuchSongException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song[] findByG_A_PrevAndNext(long songId,
		long groupId, long albumId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns all the songs that the user has permission to view where groupId = &#63; and albumId = &#63;.
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @return the matching songs that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> filterFindByG_A(
		long groupId, long albumId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the songs that the user has permission to view where groupId = &#63; and albumId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @return the range of matching songs that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> filterFindByG_A(
		long groupId, long albumId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the songs that the user has permissions to view where groupId = &#63; and albumId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching songs that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> filterFindByG_A(
		long groupId, long albumId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the songs before and after the current song in the ordered set of songs that the user has permission to view where groupId = &#63; and albumId = &#63;.
	*
	* @param songId the primary key of the current song
	* @param groupId the group ID
	* @param albumId the album ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next song
	* @throws org.liferay.jukebox.NoSuchSongException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song[] filterFindByG_A_PrevAndNext(
		long songId, long groupId, long albumId,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Removes all the songs where groupId = &#63; and albumId = &#63; from the database.
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @throws SystemException if a system exception occurred
	*/
	public void removeByG_A(long groupId, long albumId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of songs where groupId = &#63; and albumId = &#63;.
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @return the number of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public int countByG_A(long groupId, long albumId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of songs that the user has permission to view where groupId = &#63; and albumId = &#63;.
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @return the number of matching songs that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public int filterCountByG_A(long groupId, long albumId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the songs where groupId = &#63; and albumId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @param status the status
	* @return the matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByG_A_S(
		long groupId, long albumId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the songs where groupId = &#63; and albumId = &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @param status the status
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @return the range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByG_A_S(
		long groupId, long albumId, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the songs where groupId = &#63; and albumId = &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @param status the status
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByG_A_S(
		long groupId, long albumId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first song in the ordered set where groupId = &#63; and albumId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByG_A_S_First(long groupId,
		long albumId, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the first song in the ordered set where groupId = &#63; and albumId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByG_A_S_First(long groupId,
		long albumId, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last song in the ordered set where groupId = &#63; and albumId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByG_A_S_Last(long groupId,
		long albumId, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the last song in the ordered set where groupId = &#63; and albumId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByG_A_S_Last(long groupId,
		long albumId, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the songs before and after the current song in the ordered set where groupId = &#63; and albumId = &#63; and status = &#63;.
	*
	* @param songId the primary key of the current song
	* @param groupId the group ID
	* @param albumId the album ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next song
	* @throws org.liferay.jukebox.NoSuchSongException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song[] findByG_A_S_PrevAndNext(
		long songId, long groupId, long albumId, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns all the songs that the user has permission to view where groupId = &#63; and albumId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @param status the status
	* @return the matching songs that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> filterFindByG_A_S(
		long groupId, long albumId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the songs that the user has permission to view where groupId = &#63; and albumId = &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @param status the status
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @return the range of matching songs that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> filterFindByG_A_S(
		long groupId, long albumId, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the songs that the user has permissions to view where groupId = &#63; and albumId = &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @param status the status
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching songs that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> filterFindByG_A_S(
		long groupId, long albumId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the songs before and after the current song in the ordered set of songs that the user has permission to view where groupId = &#63; and albumId = &#63; and status = &#63;.
	*
	* @param songId the primary key of the current song
	* @param groupId the group ID
	* @param albumId the album ID
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next song
	* @throws org.liferay.jukebox.NoSuchSongException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song[] filterFindByG_A_S_PrevAndNext(
		long songId, long groupId, long albumId, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Removes all the songs where groupId = &#63; and albumId = &#63; and status = &#63; from the database.
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @param status the status
	* @throws SystemException if a system exception occurred
	*/
	public void removeByG_A_S(long groupId, long albumId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of songs where groupId = &#63; and albumId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @param status the status
	* @return the number of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public int countByG_A_S(long groupId, long albumId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of songs that the user has permission to view where groupId = &#63; and albumId = &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param albumId the album ID
	* @param status the status
	* @return the number of matching songs that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public int filterCountByG_A_S(long groupId, long albumId, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the songs where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @return the matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByG_LikeN_S(
		long groupId, java.lang.String name, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the songs where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @return the range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByG_LikeN_S(
		long groupId, java.lang.String name, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the songs where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findByG_LikeN_S(
		long groupId, java.lang.String name, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the first song in the ordered set where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByG_LikeN_S_First(long groupId,
		java.lang.String name, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the first song in the ordered set where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByG_LikeN_S_First(long groupId,
		java.lang.String name, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the last song in the ordered set where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByG_LikeN_S_Last(long groupId,
		java.lang.String name, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the last song in the ordered set where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByG_LikeN_S_Last(long groupId,
		java.lang.String name, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the songs before and after the current song in the ordered set where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param songId the primary key of the current song
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next song
	* @throws org.liferay.jukebox.NoSuchSongException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song[] findByG_LikeN_S_PrevAndNext(
		long songId, long groupId, java.lang.String name, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns all the songs that the user has permission to view where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @return the matching songs that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> filterFindByG_LikeN_S(
		long groupId, java.lang.String name, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the songs that the user has permission to view where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @return the range of matching songs that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> filterFindByG_LikeN_S(
		long groupId, java.lang.String name, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the songs that the user has permissions to view where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching songs that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> filterFindByG_LikeN_S(
		long groupId, java.lang.String name, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the songs before and after the current song in the ordered set of songs that the user has permission to view where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param songId the primary key of the current song
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next song
	* @throws org.liferay.jukebox.NoSuchSongException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song[] filterFindByG_LikeN_S_PrevAndNext(
		long songId, long groupId, java.lang.String name, int status,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Removes all the songs where groupId = &#63; and name LIKE &#63; and status = &#63; from the database.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @throws SystemException if a system exception occurred
	*/
	public void removeByG_LikeN_S(long groupId, java.lang.String name,
		int status) throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of songs where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @return the number of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public int countByG_LikeN_S(long groupId, java.lang.String name, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of songs that the user has permission to view where groupId = &#63; and name LIKE &#63; and status = &#63;.
	*
	* @param groupId the group ID
	* @param name the name
	* @param status the status
	* @return the number of matching songs that the user has permission to view
	* @throws SystemException if a system exception occurred
	*/
	public int filterCountByG_LikeN_S(long groupId, java.lang.String name,
		int status) throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the song where groupId = &#63; and artistId = &#63; and albumId = &#63; and name = &#63; or throws a {@link org.liferay.jukebox.NoSuchSongException} if it could not be found.
	*
	* @param groupId the group ID
	* @param artistId the artist ID
	* @param albumId the album ID
	* @param name the name
	* @return the matching song
	* @throws org.liferay.jukebox.NoSuchSongException if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByG_A_A_N(long groupId,
		long artistId, long albumId, java.lang.String name)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the song where groupId = &#63; and artistId = &#63; and albumId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param groupId the group ID
	* @param artistId the artist ID
	* @param albumId the album ID
	* @param name the name
	* @return the matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByG_A_A_N(long groupId,
		long artistId, long albumId, java.lang.String name)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the song where groupId = &#63; and artistId = &#63; and albumId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param groupId the group ID
	* @param artistId the artist ID
	* @param albumId the album ID
	* @param name the name
	* @param retrieveFromCache whether to use the finder cache
	* @return the matching song, or <code>null</code> if a matching song could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByG_A_A_N(long groupId,
		long artistId, long albumId, java.lang.String name,
		boolean retrieveFromCache)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the song where groupId = &#63; and artistId = &#63; and albumId = &#63; and name = &#63; from the database.
	*
	* @param groupId the group ID
	* @param artistId the artist ID
	* @param albumId the album ID
	* @param name the name
	* @return the song that was removed
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song removeByG_A_A_N(long groupId,
		long artistId, long albumId, java.lang.String name)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the number of songs where groupId = &#63; and artistId = &#63; and albumId = &#63; and name = &#63;.
	*
	* @param groupId the group ID
	* @param artistId the artist ID
	* @param albumId the album ID
	* @param name the name
	* @return the number of matching songs
	* @throws SystemException if a system exception occurred
	*/
	public int countByG_A_A_N(long groupId, long artistId, long albumId,
		java.lang.String name)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Caches the song in the entity cache if it is enabled.
	*
	* @param song the song
	*/
	public void cacheResult(org.liferay.jukebox.model.Song song);

	/**
	* Caches the songs in the entity cache if it is enabled.
	*
	* @param songs the songs
	*/
	public void cacheResult(
		java.util.List<org.liferay.jukebox.model.Song> songs);

	/**
	* Creates a new song with the primary key. Does not add the song to the database.
	*
	* @param songId the primary key for the new song
	* @return the new song
	*/
	public org.liferay.jukebox.model.Song create(long songId);

	/**
	* Removes the song with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param songId the primary key of the song
	* @return the song that was removed
	* @throws org.liferay.jukebox.NoSuchSongException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song remove(long songId)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	public org.liferay.jukebox.model.Song updateImpl(
		org.liferay.jukebox.model.Song song)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the song with the primary key or throws a {@link org.liferay.jukebox.NoSuchSongException} if it could not be found.
	*
	* @param songId the primary key of the song
	* @return the song
	* @throws org.liferay.jukebox.NoSuchSongException if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song findByPrimaryKey(long songId)
		throws com.liferay.portal.kernel.exception.SystemException,
			org.liferay.jukebox.NoSuchSongException;

	/**
	* Returns the song with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param songId the primary key of the song
	* @return the song, or <code>null</code> if a song with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public org.liferay.jukebox.model.Song fetchByPrimaryKey(long songId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the songs.
	*
	* @return the songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findAll()
		throws com.liferay.portal.kernel.exception.SystemException;

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
	public java.util.List<org.liferay.jukebox.model.Song> findAll(int start,
		int end) throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the songs.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.liferay.jukebox.model.impl.SongModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of songs
	* @param end the upper bound of the range of songs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of songs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<org.liferay.jukebox.model.Song> findAll(int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes all the songs from the database.
	*
	* @throws SystemException if a system exception occurred
	*/
	public void removeAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of songs.
	*
	* @return the number of songs
	* @throws SystemException if a system exception occurred
	*/
	public int countAll()
		throws com.liferay.portal.kernel.exception.SystemException;
}