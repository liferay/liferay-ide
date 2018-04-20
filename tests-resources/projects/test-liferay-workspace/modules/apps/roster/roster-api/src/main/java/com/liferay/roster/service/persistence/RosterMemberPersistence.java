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

import com.liferay.roster.exception.NoSuchRosterMemberException;
import com.liferay.roster.model.RosterMember;

/**
 * The persistence interface for the roster member service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see com.liferay.roster.service.persistence.impl.RosterMemberPersistenceImpl
 * @see RosterMemberUtil
 * @generated
 */
@ProviderType
public interface RosterMemberPersistence extends BasePersistence<RosterMember> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link RosterMemberUtil} to access the roster member persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	* Returns all the roster members where uuid = &#63;.
	*
	* @param uuid the uuid
	* @return the matching roster members
	*/
	public java.util.List<RosterMember> findByUuid(java.lang.String uuid);

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
	public java.util.List<RosterMember> findByUuid(java.lang.String uuid,
		int start, int end);

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
	public java.util.List<RosterMember> findByUuid(java.lang.String uuid,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<RosterMember> orderByComparator);

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
	public java.util.List<RosterMember> findByUuid(java.lang.String uuid,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<RosterMember> orderByComparator,
		boolean retrieveFromCache);

	/**
	* Returns the first roster member in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching roster member
	* @throws NoSuchRosterMemberException if a matching roster member could not be found
	*/
	public RosterMember findByUuid_First(java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<RosterMember> orderByComparator)
		throws NoSuchRosterMemberException;

	/**
	* Returns the first roster member in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching roster member, or <code>null</code> if a matching roster member could not be found
	*/
	public RosterMember fetchByUuid_First(java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<RosterMember> orderByComparator);

	/**
	* Returns the last roster member in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching roster member
	* @throws NoSuchRosterMemberException if a matching roster member could not be found
	*/
	public RosterMember findByUuid_Last(java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<RosterMember> orderByComparator)
		throws NoSuchRosterMemberException;

	/**
	* Returns the last roster member in the ordered set where uuid = &#63;.
	*
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching roster member, or <code>null</code> if a matching roster member could not be found
	*/
	public RosterMember fetchByUuid_Last(java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<RosterMember> orderByComparator);

	/**
	* Returns the roster members before and after the current roster member in the ordered set where uuid = &#63;.
	*
	* @param rosterMemberId the primary key of the current roster member
	* @param uuid the uuid
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next roster member
	* @throws NoSuchRosterMemberException if a roster member with the primary key could not be found
	*/
	public RosterMember[] findByUuid_PrevAndNext(long rosterMemberId,
		java.lang.String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<RosterMember> orderByComparator)
		throws NoSuchRosterMemberException;

	/**
	* Removes all the roster members where uuid = &#63; from the database.
	*
	* @param uuid the uuid
	*/
	public void removeByUuid(java.lang.String uuid);

	/**
	* Returns the number of roster members where uuid = &#63;.
	*
	* @param uuid the uuid
	* @return the number of matching roster members
	*/
	public int countByUuid(java.lang.String uuid);

	/**
	* Returns all the roster members where rosterId = &#63;.
	*
	* @param rosterId the roster ID
	* @return the matching roster members
	*/
	public java.util.List<RosterMember> findByRosterId(long rosterId);

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
	public java.util.List<RosterMember> findByRosterId(long rosterId,
		int start, int end);

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
	public java.util.List<RosterMember> findByRosterId(long rosterId,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<RosterMember> orderByComparator);

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
	public java.util.List<RosterMember> findByRosterId(long rosterId,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<RosterMember> orderByComparator,
		boolean retrieveFromCache);

	/**
	* Returns the first roster member in the ordered set where rosterId = &#63;.
	*
	* @param rosterId the roster ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching roster member
	* @throws NoSuchRosterMemberException if a matching roster member could not be found
	*/
	public RosterMember findByRosterId_First(long rosterId,
		com.liferay.portal.kernel.util.OrderByComparator<RosterMember> orderByComparator)
		throws NoSuchRosterMemberException;

	/**
	* Returns the first roster member in the ordered set where rosterId = &#63;.
	*
	* @param rosterId the roster ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching roster member, or <code>null</code> if a matching roster member could not be found
	*/
	public RosterMember fetchByRosterId_First(long rosterId,
		com.liferay.portal.kernel.util.OrderByComparator<RosterMember> orderByComparator);

	/**
	* Returns the last roster member in the ordered set where rosterId = &#63;.
	*
	* @param rosterId the roster ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching roster member
	* @throws NoSuchRosterMemberException if a matching roster member could not be found
	*/
	public RosterMember findByRosterId_Last(long rosterId,
		com.liferay.portal.kernel.util.OrderByComparator<RosterMember> orderByComparator)
		throws NoSuchRosterMemberException;

	/**
	* Returns the last roster member in the ordered set where rosterId = &#63;.
	*
	* @param rosterId the roster ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching roster member, or <code>null</code> if a matching roster member could not be found
	*/
	public RosterMember fetchByRosterId_Last(long rosterId,
		com.liferay.portal.kernel.util.OrderByComparator<RosterMember> orderByComparator);

	/**
	* Returns the roster members before and after the current roster member in the ordered set where rosterId = &#63;.
	*
	* @param rosterMemberId the primary key of the current roster member
	* @param rosterId the roster ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next roster member
	* @throws NoSuchRosterMemberException if a roster member with the primary key could not be found
	*/
	public RosterMember[] findByRosterId_PrevAndNext(long rosterMemberId,
		long rosterId,
		com.liferay.portal.kernel.util.OrderByComparator<RosterMember> orderByComparator)
		throws NoSuchRosterMemberException;

	/**
	* Removes all the roster members where rosterId = &#63; from the database.
	*
	* @param rosterId the roster ID
	*/
	public void removeByRosterId(long rosterId);

	/**
	* Returns the number of roster members where rosterId = &#63;.
	*
	* @param rosterId the roster ID
	* @return the number of matching roster members
	*/
	public int countByRosterId(long rosterId);

	/**
	* Caches the roster member in the entity cache if it is enabled.
	*
	* @param rosterMember the roster member
	*/
	public void cacheResult(RosterMember rosterMember);

	/**
	* Caches the roster members in the entity cache if it is enabled.
	*
	* @param rosterMembers the roster members
	*/
	public void cacheResult(java.util.List<RosterMember> rosterMembers);

	/**
	* Creates a new roster member with the primary key. Does not add the roster member to the database.
	*
	* @param rosterMemberId the primary key for the new roster member
	* @return the new roster member
	*/
	public RosterMember create(long rosterMemberId);

	/**
	* Removes the roster member with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param rosterMemberId the primary key of the roster member
	* @return the roster member that was removed
	* @throws NoSuchRosterMemberException if a roster member with the primary key could not be found
	*/
	public RosterMember remove(long rosterMemberId)
		throws NoSuchRosterMemberException;

	public RosterMember updateImpl(RosterMember rosterMember);

	/**
	* Returns the roster member with the primary key or throws a {@link NoSuchRosterMemberException} if it could not be found.
	*
	* @param rosterMemberId the primary key of the roster member
	* @return the roster member
	* @throws NoSuchRosterMemberException if a roster member with the primary key could not be found
	*/
	public RosterMember findByPrimaryKey(long rosterMemberId)
		throws NoSuchRosterMemberException;

	/**
	* Returns the roster member with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param rosterMemberId the primary key of the roster member
	* @return the roster member, or <code>null</code> if a roster member with the primary key could not be found
	*/
	public RosterMember fetchByPrimaryKey(long rosterMemberId);

	@Override
	public java.util.Map<java.io.Serializable, RosterMember> fetchByPrimaryKeys(
		java.util.Set<java.io.Serializable> primaryKeys);

	/**
	* Returns all the roster members.
	*
	* @return the roster members
	*/
	public java.util.List<RosterMember> findAll();

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
	public java.util.List<RosterMember> findAll(int start, int end);

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
	public java.util.List<RosterMember> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<RosterMember> orderByComparator);

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
	public java.util.List<RosterMember> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<RosterMember> orderByComparator,
		boolean retrieveFromCache);

	/**
	* Removes all the roster members from the database.
	*/
	public void removeAll();

	/**
	* Returns the number of roster members.
	*
	* @return the number of roster members
	*/
	public int countAll();

	@Override
	public java.util.Set<java.lang.String> getBadColumnNames();
}