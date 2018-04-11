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

import com.liferay.roster.model.RosterMember;

import org.osgi.util.tracker.ServiceTracker;

import java.util.List;

/**
 * The persistence utility for the roster member service. This utility wraps {@link com.liferay.roster.service.persistence.impl.RosterMemberPersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see RosterMemberPersistence
 * @see com.liferay.roster.service.persistence.impl.RosterMemberPersistenceImpl
 * @generated
 */
@ProviderType
public class RosterMemberUtil {
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
	public static void clearCache(RosterMember rosterMember) {
		getPersistence().clearCache(rosterMember);
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
	public static List<RosterMember> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<RosterMember> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<RosterMember> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<RosterMember> orderByComparator) {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static RosterMember update(RosterMember rosterMember) {
		return getPersistence().update(rosterMember);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static RosterMember update(RosterMember rosterMember,
		ServiceContext serviceContext) {
		return getPersistence().update(rosterMember, serviceContext);
	}

	/**
	* Returns all the roster members where uuid = &#63;.
	*
	* @param uuid the uuid
	* @return the matching roster members
	*/
	public static List<RosterMember> findByUuid(java.lang.String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	* Returns a range of all the roster members where uuid = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterMemberModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param uuid the uuid
	* @param start the lower bound of the range of roster members
	* @param end the upper bound of the range of roster members (not inclusive)
	* @return the range of matching roster members
	*/
	public static List<RosterMember> findByUuid(java.lang.String uuid,
		int start, int end) {
		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	* Returns an ordered range of all the roster members where uuid = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterMemberModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param uuid the uuid
	* @param start the lower bound of the range of roster members
	* @param end the upper bound of the range of roster members (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching roster members
	*/
	public static List<RosterMember> findByUuid(java.lang.String uuid,
		int start, int end, OrderByComparator<RosterMember> orderByComparator) {
		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	* Returns an ordered range of all the roster members where uuid = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterMemberModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param uuid the uuid
	* @param start the lower bound of the range of roster members
	* @param end the upper bound of the range of roster members (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of matching roster members
	*/
	public static List<RosterMember> findByUuid(java.lang.String uuid,
		int start, int end, OrderByComparator<RosterMember> orderByComparator,
		boolean retrieveFromCache) {
		return getPersistence()
				   .findByUuid(uuid, start, end, orderByComparator,
			retrieveFromCache);
	}

	/**
	* Returns the first roster member in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching roster member
	* @throws NoSuchRosterMemberException if a matching roster member could not be found
	*/
	public static RosterMember findByUuid_First(java.lang.String uuid,
		OrderByComparator<RosterMember> orderByComparator)
		throws com.liferay.roster.exception.NoSuchRosterMemberException {
		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	* Returns the first roster member in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching roster member, or <code>null</code> if a matching roster member could not be found
	*/
	public static RosterMember fetchByUuid_First(java.lang.String uuid,
		OrderByComparator<RosterMember> orderByComparator) {
		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	* Returns the last roster member in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching roster member
	* @throws NoSuchRosterMemberException if a matching roster member could not be found
	*/
	public static RosterMember findByUuid_Last(java.lang.String uuid,
		OrderByComparator<RosterMember> orderByComparator)
		throws com.liferay.roster.exception.NoSuchRosterMemberException {
		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	* Returns the last roster member in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching roster member, or <code>null</code> if a matching roster member could not be found
	*/
	public static RosterMember fetchByUuid_Last(java.lang.String uuid,
		OrderByComparator<RosterMember> orderByComparator) {
		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	* Returns the roster members before and after the current roster member in the ordered set where uuid = &#63;.
	*
	* @param rosterMemberId the primary key of the current roster member
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next roster member
	* @throws NoSuchRosterMemberException if a roster member with the primary key could not be found
	*/
	public static RosterMember[] findByUuid_PrevAndNext(long rosterMemberId,
		java.lang.String uuid, OrderByComparator<RosterMember> orderByComparator)
		throws com.liferay.roster.exception.NoSuchRosterMemberException {
		return getPersistence()
				   .findByUuid_PrevAndNext(rosterMemberId, uuid,
			orderByComparator);
	}

	/**
	* Removes all the roster members where uuid = &#63; from the database.
	*
	* @param uuid the uuid
	*/
	public static void removeByUuid(java.lang.String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	* Returns the number of roster members where uuid = &#63;.
	*
	* @param uuid the uuid
	* @return the number of matching roster members
	*/
	public static int countByUuid(java.lang.String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	* Returns all the roster members where rosterId = &#63;.
	*
	* @param rosterId the roster ID
	* @return the matching roster members
	*/
	public static List<RosterMember> findByRosterId(long rosterId) {
		return getPersistence().findByRosterId(rosterId);
	}

	/**
	* Returns a range of all the roster members where rosterId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterMemberModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param rosterId the roster ID
	* @param start the lower bound of the range of roster members
	* @param end the upper bound of the range of roster members (not inclusive)
	* @return the range of matching roster members
	*/
	public static List<RosterMember> findByRosterId(long rosterId, int start,
		int end) {
		return getPersistence().findByRosterId(rosterId, start, end);
	}

	/**
	* Returns an ordered range of all the roster members where rosterId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterMemberModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param rosterId the roster ID
	* @param start the lower bound of the range of roster members
	* @param end the upper bound of the range of roster members (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching roster members
	*/
	public static List<RosterMember> findByRosterId(long rosterId, int start,
		int end, OrderByComparator<RosterMember> orderByComparator) {
		return getPersistence()
				   .findByRosterId(rosterId, start, end, orderByComparator);
	}

	/**
	* Returns an ordered range of all the roster members where rosterId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterMemberModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param rosterId the roster ID
	* @param start the lower bound of the range of roster members
	* @param end the upper bound of the range of roster members (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of matching roster members
	*/
	public static List<RosterMember> findByRosterId(long rosterId, int start,
		int end, OrderByComparator<RosterMember> orderByComparator,
		boolean retrieveFromCache) {
		return getPersistence()
				   .findByRosterId(rosterId, start, end, orderByComparator,
			retrieveFromCache);
	}

	/**
	* Returns the first roster member in the ordered set where rosterId = &#63;.
	*
	* @param rosterId the roster ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching roster member
	* @throws NoSuchRosterMemberException if a matching roster member could not be found
	*/
	public static RosterMember findByRosterId_First(long rosterId,
		OrderByComparator<RosterMember> orderByComparator)
		throws com.liferay.roster.exception.NoSuchRosterMemberException {
		return getPersistence().findByRosterId_First(rosterId, orderByComparator);
	}

	/**
	* Returns the first roster member in the ordered set where rosterId = &#63;.
	*
	* @param rosterId the roster ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching roster member, or <code>null</code> if a matching roster member could not be found
	*/
	public static RosterMember fetchByRosterId_First(long rosterId,
		OrderByComparator<RosterMember> orderByComparator) {
		return getPersistence()
				   .fetchByRosterId_First(rosterId, orderByComparator);
	}

	/**
	* Returns the last roster member in the ordered set where rosterId = &#63;.
	*
	* @param rosterId the roster ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching roster member
	* @throws NoSuchRosterMemberException if a matching roster member could not be found
	*/
	public static RosterMember findByRosterId_Last(long rosterId,
		OrderByComparator<RosterMember> orderByComparator)
		throws com.liferay.roster.exception.NoSuchRosterMemberException {
		return getPersistence().findByRosterId_Last(rosterId, orderByComparator);
	}

	/**
	* Returns the last roster member in the ordered set where rosterId = &#63;.
	*
	* @param rosterId the roster ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching roster member, or <code>null</code> if a matching roster member could not be found
	*/
	public static RosterMember fetchByRosterId_Last(long rosterId,
		OrderByComparator<RosterMember> orderByComparator) {
		return getPersistence().fetchByRosterId_Last(rosterId, orderByComparator);
	}

	/**
	* Returns the roster members before and after the current roster member in the ordered set where rosterId = &#63;.
	*
	* @param rosterMemberId the primary key of the current roster member
	* @param rosterId the roster ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next roster member
	* @throws NoSuchRosterMemberException if a roster member with the primary key could not be found
	*/
	public static RosterMember[] findByRosterId_PrevAndNext(
		long rosterMemberId, long rosterId,
		OrderByComparator<RosterMember> orderByComparator)
		throws com.liferay.roster.exception.NoSuchRosterMemberException {
		return getPersistence()
				   .findByRosterId_PrevAndNext(rosterMemberId, rosterId,
			orderByComparator);
	}

	/**
	* Removes all the roster members where rosterId = &#63; from the database.
	*
	* @param rosterId the roster ID
	*/
	public static void removeByRosterId(long rosterId) {
		getPersistence().removeByRosterId(rosterId);
	}

	/**
	* Returns the number of roster members where rosterId = &#63;.
	*
	* @param rosterId the roster ID
	* @return the number of matching roster members
	*/
	public static int countByRosterId(long rosterId) {
		return getPersistence().countByRosterId(rosterId);
	}

	/**
	* Caches the roster member in the entity cache if it is enabled.
	*
	* @param rosterMember the roster member
	*/
	public static void cacheResult(RosterMember rosterMember) {
		getPersistence().cacheResult(rosterMember);
	}

	/**
	* Caches the roster members in the entity cache if it is enabled.
	*
	* @param rosterMembers the roster members
	*/
	public static void cacheResult(List<RosterMember> rosterMembers) {
		getPersistence().cacheResult(rosterMembers);
	}

	/**
	* Creates a new roster member with the primary key. Does not add the roster member to the database.
	*
	* @param rosterMemberId the primary key for the new roster member
	* @return the new roster member
	*/
	public static RosterMember create(long rosterMemberId) {
		return getPersistence().create(rosterMemberId);
	}

	/**
	* Removes the roster member with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param rosterMemberId the primary key of the roster member
	* @return the roster member that was removed
	* @throws NoSuchRosterMemberException if a roster member with the primary key could not be found
	*/
	public static RosterMember remove(long rosterMemberId)
		throws com.liferay.roster.exception.NoSuchRosterMemberException {
		return getPersistence().remove(rosterMemberId);
	}

	public static RosterMember updateImpl(RosterMember rosterMember) {
		return getPersistence().updateImpl(rosterMember);
	}

	/**
	* Returns the roster member with the primary key or throws a {@link NoSuchRosterMemberException} if it could not be found.
	*
	* @param rosterMemberId the primary key of the roster member
	* @return the roster member
	* @throws NoSuchRosterMemberException if a roster member with the primary key could not be found
	*/
	public static RosterMember findByPrimaryKey(long rosterMemberId)
		throws com.liferay.roster.exception.NoSuchRosterMemberException {
		return getPersistence().findByPrimaryKey(rosterMemberId);
	}

	/**
	* Returns the roster member with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param rosterMemberId the primary key of the roster member
	* @return the roster member, or <code>null</code> if a roster member with the primary key could not be found
	*/
	public static RosterMember fetchByPrimaryKey(long rosterMemberId) {
		return getPersistence().fetchByPrimaryKey(rosterMemberId);
	}

	public static java.util.Map<java.io.Serializable, RosterMember> fetchByPrimaryKeys(
		java.util.Set<java.io.Serializable> primaryKeys) {
		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	* Returns all the roster members.
	*
	* @return the roster members
	*/
	public static List<RosterMember> findAll() {
		return getPersistence().findAll();
	}

	/**
	* Returns a range of all the roster members.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterMemberModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of roster members
	* @param end the upper bound of the range of roster members (not inclusive)
	* @return the range of roster members
	*/
	public static List<RosterMember> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	* Returns an ordered range of all the roster members.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterMemberModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of roster members
	* @param end the upper bound of the range of roster members (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of roster members
	*/
	public static List<RosterMember> findAll(int start, int end,
		OrderByComparator<RosterMember> orderByComparator) {
		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	* Returns an ordered range of all the roster members.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterMemberModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of roster members
	* @param end the upper bound of the range of roster members (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of roster members
	*/
	public static List<RosterMember> findAll(int start, int end,
		OrderByComparator<RosterMember> orderByComparator,
		boolean retrieveFromCache) {
		return getPersistence()
				   .findAll(start, end, orderByComparator, retrieveFromCache);
	}

	/**
	* Removes all the roster members from the database.
	*/
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of roster members.
	*
	* @return the number of roster members
	*/
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static java.util.Set<java.lang.String> getBadColumnNames() {
		return getPersistence().getBadColumnNames();
	}

	public static RosterMemberPersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<RosterMemberPersistence, RosterMemberPersistence> _serviceTracker =
		ServiceTrackerFactory.open(RosterMemberPersistence.class);
}