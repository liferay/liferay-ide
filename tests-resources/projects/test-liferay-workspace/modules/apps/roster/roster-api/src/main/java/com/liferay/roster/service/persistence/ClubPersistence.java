/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.roster.service.persistence;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

import com.liferay.roster.exception.NoSuchClubException;
import com.liferay.roster.model.Club;

/**
 * The persistence interface for the club service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see com.liferay.roster.service.persistence.impl.ClubPersistenceImpl
 * @see ClubUtil
 * @generated
 */
@ProviderType
public interface ClubPersistence extends BasePersistence<Club> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ClubUtil} to access the club persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	* Returns all the clubs where uuid = &#63;.
	*
	* @param uuid the uuid
	* @return the matching clubs
	*/
	public java.util.List<Club> findByUuid(java.lang.String uuid);

	/**
	* Returns a range of all the clubs where uuid = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link ClubModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param uuid the uuid
	* @param start the lower bound of the range of clubs
	* @param end the upper bound of the range of clubs (not inclusive)
	* @return the range of matching clubs
	*/
	public java.util.List<Club> findByUuid(java.lang.String uuid, int start,
		int end);

	/**
	* Returns an ordered range of all the clubs where uuid = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link ClubModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param uuid the uuid
	* @param start the lower bound of the range of clubs
	* @param end the upper bound of the range of clubs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching clubs
	*/
	public java.util.List<Club> findByUuid(java.lang.String uuid, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Club> orderByComparator);

	/**
	* Returns an ordered range of all the clubs where uuid = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link ClubModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param uuid the uuid
	* @param start the lower bound of the range of clubs
	* @param end the upper bound of the range of clubs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of matching clubs
	*/
	public java.util.List<Club> findByUuid(java.lang.String uuid, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Club> orderByComparator,
		boolean retrieveFromCache);

	/**
	* Returns the first club in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching club
	* @throws NoSuchClubException if a matching club could not be found
	*/
	public Club findByUuid_First(java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<Club> orderByComparator)
		throws NoSuchClubException;

	/**
	* Returns the first club in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching club, or <code>null</code> if a matching club could not be found
	*/
	public Club fetchByUuid_First(java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<Club> orderByComparator);

	/**
	* Returns the last club in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching club
	* @throws NoSuchClubException if a matching club could not be found
	*/
	public Club findByUuid_Last(java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<Club> orderByComparator)
		throws NoSuchClubException;

	/**
	* Returns the last club in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching club, or <code>null</code> if a matching club could not be found
	*/
	public Club fetchByUuid_Last(java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<Club> orderByComparator);

	/**
	* Returns the clubs before and after the current club in the ordered set where uuid = &#63;.
	*
	* @param clubId the primary key of the current club
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next club
	* @throws NoSuchClubException if a club with the primary key could not be found
	*/
	public Club[] findByUuid_PrevAndNext(long clubId, java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<Club> orderByComparator)
		throws NoSuchClubException;

	/**
	* Removes all the clubs where uuid = &#63; from the database.
	*
	* @param uuid the uuid
	*/
	public void removeByUuid(java.lang.String uuid);

	/**
	* Returns the number of clubs where uuid = &#63;.
	*
	* @param uuid the uuid
	* @return the number of matching clubs
	*/
	public int countByUuid(java.lang.String uuid);

	/**
	* Caches the club in the entity cache if it is enabled.
	*
	* @param club the club
	*/
	public void cacheResult(Club club);

	/**
	* Caches the clubs in the entity cache if it is enabled.
	*
	* @param clubs the clubs
	*/
	public void cacheResult(java.util.List<Club> clubs);

	/**
	* Creates a new club with the primary key. Does not add the club to the database.
	*
	* @param clubId the primary key for the new club
	* @return the new club
	*/
	public Club create(long clubId);

	/**
	* Removes the club with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param clubId the primary key of the club
	* @return the club that was removed
	* @throws NoSuchClubException if a club with the primary key could not be found
	*/
	public Club remove(long clubId) throws NoSuchClubException;

	public Club updateImpl(Club club);

	/**
	* Returns the club with the primary key or throws a {@link NoSuchClubException} if it could not be found.
	*
	* @param clubId the primary key of the club
	* @return the club
	* @throws NoSuchClubException if a club with the primary key could not be found
	*/
	public Club findByPrimaryKey(long clubId) throws NoSuchClubException;

	/**
	* Returns the club with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param clubId the primary key of the club
	* @return the club, or <code>null</code> if a club with the primary key could not be found
	*/
	public Club fetchByPrimaryKey(long clubId);

	@Override
	public java.util.Map<java.io.Serializable, Club> fetchByPrimaryKeys(
		java.util.Set<java.io.Serializable> primaryKeys);

	/**
	* Returns all the clubs.
	*
	* @return the clubs
	*/
	public java.util.List<Club> findAll();

	/**
	* Returns a range of all the clubs.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link ClubModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of clubs
	* @param end the upper bound of the range of clubs (not inclusive)
	* @return the range of clubs
	*/
	public java.util.List<Club> findAll(int start, int end);

	/**
	* Returns an ordered range of all the clubs.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link ClubModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of clubs
	* @param end the upper bound of the range of clubs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of clubs
	*/
	public java.util.List<Club> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Club> orderByComparator);

	/**
	* Returns an ordered range of all the clubs.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link ClubModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of clubs
	* @param end the upper bound of the range of clubs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of clubs
	*/
	public java.util.List<Club> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Club> orderByComparator,
		boolean retrieveFromCache);

	/**
	* Removes all the clubs from the database.
	*/
	public void removeAll();

	/**
	* Returns the number of clubs.
	*
	* @return the number of clubs
	*/
	public int countAll();

	@Override
	public java.util.Set<java.lang.String> getBadColumnNames();
}