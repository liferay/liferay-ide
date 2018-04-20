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

import com.liferay.roster.exception.NoSuchRosterException;
import com.liferay.roster.model.Roster;

/**
 * The persistence interface for the roster service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see com.liferay.roster.service.persistence.impl.RosterPersistenceImpl
 * @see RosterUtil
 * @generated
 */
@ProviderType
public interface RosterPersistence extends BasePersistence<Roster> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link RosterUtil} to access the roster persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	* Returns all the rosters where uuid = &#63;.
	*
	* @param uuid the uuid
	* @return the matching rosters
	*/
	public java.util.List<Roster> findByUuid(java.lang.String uuid);

	/**
	* Returns a range of all the rosters where uuid = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param uuid the uuid
	* @param start the lower bound of the range of rosters
	* @param end the upper bound of the range of rosters (not inclusive)
	* @return the range of matching rosters
	*/
	public java.util.List<Roster> findByUuid(java.lang.String uuid, int start,
		int end);

	/**
	* Returns an ordered range of all the rosters where uuid = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param uuid the uuid
	* @param start the lower bound of the range of rosters
	* @param end the upper bound of the range of rosters (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching rosters
	*/
	public java.util.List<Roster> findByUuid(java.lang.String uuid, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Roster> orderByComparator);

	/**
	* Returns an ordered range of all the rosters where uuid = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param uuid the uuid
	* @param start the lower bound of the range of rosters
	* @param end the upper bound of the range of rosters (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of matching rosters
	*/
	public java.util.List<Roster> findByUuid(java.lang.String uuid, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Roster> orderByComparator,
		boolean retrieveFromCache);

	/**
	* Returns the first roster in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching roster
	* @throws NoSuchRosterException if a matching roster could not be found
	*/
	public Roster findByUuid_First(java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<Roster> orderByComparator)
		throws NoSuchRosterException;

	/**
	* Returns the first roster in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching roster, or <code>null</code> if a matching roster could not be found
	*/
	public Roster fetchByUuid_First(java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<Roster> orderByComparator);

	/**
	* Returns the last roster in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching roster
	* @throws NoSuchRosterException if a matching roster could not be found
	*/
	public Roster findByUuid_Last(java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<Roster> orderByComparator)
		throws NoSuchRosterException;

	/**
	* Returns the last roster in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching roster, or <code>null</code> if a matching roster could not be found
	*/
	public Roster fetchByUuid_Last(java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<Roster> orderByComparator);

	/**
	* Returns the rosters before and after the current roster in the ordered set where uuid = &#63;.
	*
	* @param rosterId the primary key of the current roster
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next roster
	* @throws NoSuchRosterException if a roster with the primary key could not be found
	*/
	public Roster[] findByUuid_PrevAndNext(long rosterId,
		java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<Roster> orderByComparator)
		throws NoSuchRosterException;

	/**
	* Removes all the rosters where uuid = &#63; from the database.
	*
	* @param uuid the uuid
	*/
	public void removeByUuid(java.lang.String uuid);

	/**
	* Returns the number of rosters where uuid = &#63;.
	*
	* @param uuid the uuid
	* @return the number of matching rosters
	*/
	public int countByUuid(java.lang.String uuid);

	/**
	* Returns all the rosters where clubId = &#63;.
	*
	* @param clubId the club ID
	* @return the matching rosters
	*/
	public java.util.List<Roster> findByClubId(long clubId);

	/**
	* Returns a range of all the rosters where clubId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param clubId the club ID
	* @param start the lower bound of the range of rosters
	* @param end the upper bound of the range of rosters (not inclusive)
	* @return the range of matching rosters
	*/
	public java.util.List<Roster> findByClubId(long clubId, int start, int end);

	/**
	* Returns an ordered range of all the rosters where clubId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param clubId the club ID
	* @param start the lower bound of the range of rosters
	* @param end the upper bound of the range of rosters (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching rosters
	*/
	public java.util.List<Roster> findByClubId(long clubId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Roster> orderByComparator);

	/**
	* Returns an ordered range of all the rosters where clubId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param clubId the club ID
	* @param start the lower bound of the range of rosters
	* @param end the upper bound of the range of rosters (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of matching rosters
	*/
	public java.util.List<Roster> findByClubId(long clubId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Roster> orderByComparator,
		boolean retrieveFromCache);

	/**
	* Returns the first roster in the ordered set where clubId = &#63;.
	*
	* @param clubId the club ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching roster
	* @throws NoSuchRosterException if a matching roster could not be found
	*/
	public Roster findByClubId_First(long clubId,
		com.liferay.portal.kernel.util.OrderByComparator<Roster> orderByComparator)
		throws NoSuchRosterException;

	/**
	* Returns the first roster in the ordered set where clubId = &#63;.
	*
	* @param clubId the club ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching roster, or <code>null</code> if a matching roster could not be found
	*/
	public Roster fetchByClubId_First(long clubId,
		com.liferay.portal.kernel.util.OrderByComparator<Roster> orderByComparator);

	/**
	* Returns the last roster in the ordered set where clubId = &#63;.
	*
	* @param clubId the club ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching roster
	* @throws NoSuchRosterException if a matching roster could not be found
	*/
	public Roster findByClubId_Last(long clubId,
		com.liferay.portal.kernel.util.OrderByComparator<Roster> orderByComparator)
		throws NoSuchRosterException;

	/**
	* Returns the last roster in the ordered set where clubId = &#63;.
	*
	* @param clubId the club ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching roster, or <code>null</code> if a matching roster could not be found
	*/
	public Roster fetchByClubId_Last(long clubId,
		com.liferay.portal.kernel.util.OrderByComparator<Roster> orderByComparator);

	/**
	* Returns the rosters before and after the current roster in the ordered set where clubId = &#63;.
	*
	* @param rosterId the primary key of the current roster
	* @param clubId the club ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next roster
	* @throws NoSuchRosterException if a roster with the primary key could not be found
	*/
	public Roster[] findByClubId_PrevAndNext(long rosterId, long clubId,
		com.liferay.portal.kernel.util.OrderByComparator<Roster> orderByComparator)
		throws NoSuchRosterException;

	/**
	* Removes all the rosters where clubId = &#63; from the database.
	*
	* @param clubId the club ID
	*/
	public void removeByClubId(long clubId);

	/**
	* Returns the number of rosters where clubId = &#63;.
	*
	* @param clubId the club ID
	* @return the number of matching rosters
	*/
	public int countByClubId(long clubId);

	/**
	* Caches the roster in the entity cache if it is enabled.
	*
	* @param roster the roster
	*/
	public void cacheResult(Roster roster);

	/**
	* Caches the rosters in the entity cache if it is enabled.
	*
	* @param rosters the rosters
	*/
	public void cacheResult(java.util.List<Roster> rosters);

	/**
	* Creates a new roster with the primary key. Does not add the roster to the database.
	*
	* @param rosterId the primary key for the new roster
	* @return the new roster
	*/
	public Roster create(long rosterId);

	/**
	* Removes the roster with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param rosterId the primary key of the roster
	* @return the roster that was removed
	* @throws NoSuchRosterException if a roster with the primary key could not be found
	*/
	public Roster remove(long rosterId) throws NoSuchRosterException;

	public Roster updateImpl(Roster roster);

	/**
	* Returns the roster with the primary key or throws a {@link NoSuchRosterException} if it could not be found.
	*
	* @param rosterId the primary key of the roster
	* @return the roster
	* @throws NoSuchRosterException if a roster with the primary key could not be found
	*/
	public Roster findByPrimaryKey(long rosterId) throws NoSuchRosterException;

	/**
	* Returns the roster with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param rosterId the primary key of the roster
	* @return the roster, or <code>null</code> if a roster with the primary key could not be found
	*/
	public Roster fetchByPrimaryKey(long rosterId);

	@Override
	public java.util.Map<java.io.Serializable, Roster> fetchByPrimaryKeys(
		java.util.Set<java.io.Serializable> primaryKeys);

	/**
	* Returns all the rosters.
	*
	* @return the rosters
	*/
	public java.util.List<Roster> findAll();

	/**
	* Returns a range of all the rosters.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of rosters
	* @param end the upper bound of the range of rosters (not inclusive)
	* @return the range of rosters
	*/
	public java.util.List<Roster> findAll(int start, int end);

	/**
	* Returns an ordered range of all the rosters.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of rosters
	* @param end the upper bound of the range of rosters (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of rosters
	*/
	public java.util.List<Roster> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Roster> orderByComparator);

	/**
	* Returns an ordered range of all the rosters.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of rosters
	* @param end the upper bound of the range of rosters (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of rosters
	*/
	public java.util.List<Roster> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Roster> orderByComparator,
		boolean retrieveFromCache);

	/**
	* Removes all the rosters from the database.
	*/
	public void removeAll();

	/**
	* Returns the number of rosters.
	*
	* @return the number of rosters
	*/
	public int countAll();

	@Override
	public java.util.Set<java.lang.String> getBadColumnNames();
}