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

import com.liferay.osgi.util.ServiceTrackerFactory;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import com.liferay.roster.model.Roster;

import org.osgi.util.tracker.ServiceTracker;

import java.util.List;

/**
 * The persistence utility for the roster service. This utility wraps {@link com.liferay.roster.service.persistence.impl.RosterPersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see RosterPersistence
 * @see com.liferay.roster.service.persistence.impl.RosterPersistenceImpl
 * @generated
 */
@ProviderType
public class RosterUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(Roster roster) {
		getPersistence().clearCache(roster);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<Roster> findWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<Roster> findWithDynamicQuery(DynamicQuery dynamicQuery,
		int start, int end) {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<Roster> findWithDynamicQuery(DynamicQuery dynamicQuery,
		int start, int end, OrderByComparator<Roster> orderByComparator) {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static Roster update(Roster roster) {
		return getPersistence().update(roster);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static Roster update(Roster roster, ServiceContext serviceContext) {
		return getPersistence().update(roster, serviceContext);
	}

	/**
	* Returns all the rosters where uuid = &#63;.
	*
	* @param uuid the uuid
	* @return the matching rosters
	*/
	public static List<Roster> findByUuid(java.lang.String uuid) {
		return getPersistence().findByUuid(uuid);
	}

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
	public static List<Roster> findByUuid(java.lang.String uuid, int start,
		int end) {
		return getPersistence().findByUuid(uuid, start, end);
	}

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
	public static List<Roster> findByUuid(java.lang.String uuid, int start,
		int end, OrderByComparator<Roster> orderByComparator) {
		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

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
	public static List<Roster> findByUuid(java.lang.String uuid, int start,
		int end, OrderByComparator<Roster> orderByComparator,
		boolean retrieveFromCache) {
		return getPersistence()
				   .findByUuid(uuid, start, end, orderByComparator,
			retrieveFromCache);
	}

	/**
	* Returns the first roster in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching roster
	* @throws NoSuchRosterException if a matching roster could not be found
	*/
	public static Roster findByUuid_First(java.lang.String uuid,
		OrderByComparator<Roster> orderByComparator)
		throws com.liferay.roster.exception.NoSuchRosterException {
		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	* Returns the first roster in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching roster, or <code>null</code> if a matching roster could not be found
	*/
	public static Roster fetchByUuid_First(java.lang.String uuid,
		OrderByComparator<Roster> orderByComparator) {
		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	* Returns the last roster in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching roster
	* @throws NoSuchRosterException if a matching roster could not be found
	*/
	public static Roster findByUuid_Last(java.lang.String uuid,
		OrderByComparator<Roster> orderByComparator)
		throws com.liferay.roster.exception.NoSuchRosterException {
		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	* Returns the last roster in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching roster, or <code>null</code> if a matching roster could not be found
	*/
	public static Roster fetchByUuid_Last(java.lang.String uuid,
		OrderByComparator<Roster> orderByComparator) {
		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	* Returns the rosters before and after the current roster in the ordered set where uuid = &#63;.
	*
	* @param rosterId the primary key of the current roster
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next roster
	* @throws NoSuchRosterException if a roster with the primary key could not be found
	*/
	public static Roster[] findByUuid_PrevAndNext(long rosterId,
		java.lang.String uuid, OrderByComparator<Roster> orderByComparator)
		throws com.liferay.roster.exception.NoSuchRosterException {
		return getPersistence()
				   .findByUuid_PrevAndNext(rosterId, uuid, orderByComparator);
	}

	/**
	* Removes all the rosters where uuid = &#63; from the database.
	*
	* @param uuid the uuid
	*/
	public static void removeByUuid(java.lang.String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	* Returns the number of rosters where uuid = &#63;.
	*
	* @param uuid the uuid
	* @return the number of matching rosters
	*/
	public static int countByUuid(java.lang.String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	* Returns all the rosters where clubId = &#63;.
	*
	* @param clubId the club ID
	* @return the matching rosters
	*/
	public static List<Roster> findByClubId(long clubId) {
		return getPersistence().findByClubId(clubId);
	}

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
	public static List<Roster> findByClubId(long clubId, int start, int end) {
		return getPersistence().findByClubId(clubId, start, end);
	}

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
	public static List<Roster> findByClubId(long clubId, int start, int end,
		OrderByComparator<Roster> orderByComparator) {
		return getPersistence()
				   .findByClubId(clubId, start, end, orderByComparator);
	}

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
	public static List<Roster> findByClubId(long clubId, int start, int end,
		OrderByComparator<Roster> orderByComparator, boolean retrieveFromCache) {
		return getPersistence()
				   .findByClubId(clubId, start, end, orderByComparator,
			retrieveFromCache);
	}

	/**
	* Returns the first roster in the ordered set where clubId = &#63;.
	*
	* @param clubId the club ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching roster
	* @throws NoSuchRosterException if a matching roster could not be found
	*/
	public static Roster findByClubId_First(long clubId,
		OrderByComparator<Roster> orderByComparator)
		throws com.liferay.roster.exception.NoSuchRosterException {
		return getPersistence().findByClubId_First(clubId, orderByComparator);
	}

	/**
	* Returns the first roster in the ordered set where clubId = &#63;.
	*
	* @param clubId the club ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching roster, or <code>null</code> if a matching roster could not be found
	*/
	public static Roster fetchByClubId_First(long clubId,
		OrderByComparator<Roster> orderByComparator) {
		return getPersistence().fetchByClubId_First(clubId, orderByComparator);
	}

	/**
	* Returns the last roster in the ordered set where clubId = &#63;.
	*
	* @param clubId the club ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching roster
	* @throws NoSuchRosterException if a matching roster could not be found
	*/
	public static Roster findByClubId_Last(long clubId,
		OrderByComparator<Roster> orderByComparator)
		throws com.liferay.roster.exception.NoSuchRosterException {
		return getPersistence().findByClubId_Last(clubId, orderByComparator);
	}

	/**
	* Returns the last roster in the ordered set where clubId = &#63;.
	*
	* @param clubId the club ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching roster, or <code>null</code> if a matching roster could not be found
	*/
	public static Roster fetchByClubId_Last(long clubId,
		OrderByComparator<Roster> orderByComparator) {
		return getPersistence().fetchByClubId_Last(clubId, orderByComparator);
	}

	/**
	* Returns the rosters before and after the current roster in the ordered set where clubId = &#63;.
	*
	* @param rosterId the primary key of the current roster
	* @param clubId the club ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next roster
	* @throws NoSuchRosterException if a roster with the primary key could not be found
	*/
	public static Roster[] findByClubId_PrevAndNext(long rosterId, long clubId,
		OrderByComparator<Roster> orderByComparator)
		throws com.liferay.roster.exception.NoSuchRosterException {
		return getPersistence()
				   .findByClubId_PrevAndNext(rosterId, clubId, orderByComparator);
	}

	/**
	* Removes all the rosters where clubId = &#63; from the database.
	*
	* @param clubId the club ID
	*/
	public static void removeByClubId(long clubId) {
		getPersistence().removeByClubId(clubId);
	}

	/**
	* Returns the number of rosters where clubId = &#63;.
	*
	* @param clubId the club ID
	* @return the number of matching rosters
	*/
	public static int countByClubId(long clubId) {
		return getPersistence().countByClubId(clubId);
	}

	/**
	* Caches the roster in the entity cache if it is enabled.
	*
	* @param roster the roster
	*/
	public static void cacheResult(Roster roster) {
		getPersistence().cacheResult(roster);
	}

	/**
	* Caches the rosters in the entity cache if it is enabled.
	*
	* @param rosters the rosters
	*/
	public static void cacheResult(List<Roster> rosters) {
		getPersistence().cacheResult(rosters);
	}

	/**
	* Creates a new roster with the primary key. Does not add the roster to the database.
	*
	* @param rosterId the primary key for the new roster
	* @return the new roster
	*/
	public static Roster create(long rosterId) {
		return getPersistence().create(rosterId);
	}

	/**
	* Removes the roster with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param rosterId the primary key of the roster
	* @return the roster that was removed
	* @throws NoSuchRosterException if a roster with the primary key could not be found
	*/
	public static Roster remove(long rosterId)
		throws com.liferay.roster.exception.NoSuchRosterException {
		return getPersistence().remove(rosterId);
	}

	public static Roster updateImpl(Roster roster) {
		return getPersistence().updateImpl(roster);
	}

	/**
	* Returns the roster with the primary key or throws a {@link NoSuchRosterException} if it could not be found.
	*
	* @param rosterId the primary key of the roster
	* @return the roster
	* @throws NoSuchRosterException if a roster with the primary key could not be found
	*/
	public static Roster findByPrimaryKey(long rosterId)
		throws com.liferay.roster.exception.NoSuchRosterException {
		return getPersistence().findByPrimaryKey(rosterId);
	}

	/**
	* Returns the roster with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param rosterId the primary key of the roster
	* @return the roster, or <code>null</code> if a roster with the primary key could not be found
	*/
	public static Roster fetchByPrimaryKey(long rosterId) {
		return getPersistence().fetchByPrimaryKey(rosterId);
	}

	public static java.util.Map<java.io.Serializable, Roster> fetchByPrimaryKeys(
		java.util.Set<java.io.Serializable> primaryKeys) {
		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	* Returns all the rosters.
	*
	* @return the rosters
	*/
	public static List<Roster> findAll() {
		return getPersistence().findAll();
	}

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
	public static List<Roster> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

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
	public static List<Roster> findAll(int start, int end,
		OrderByComparator<Roster> orderByComparator) {
		return getPersistence().findAll(start, end, orderByComparator);
	}

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
	public static List<Roster> findAll(int start, int end,
		OrderByComparator<Roster> orderByComparator, boolean retrieveFromCache) {
		return getPersistence()
				   .findAll(start, end, orderByComparator, retrieveFromCache);
	}

	/**
	* Removes all the rosters from the database.
	*/
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of rosters.
	*
	* @return the number of rosters
	*/
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static java.util.Set<java.lang.String> getBadColumnNames() {
		return getPersistence().getBadColumnNames();
	}

	public static RosterPersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<RosterPersistence, RosterPersistence> _serviceTracker =
		ServiceTrackerFactory.open(RosterPersistence.class);
}