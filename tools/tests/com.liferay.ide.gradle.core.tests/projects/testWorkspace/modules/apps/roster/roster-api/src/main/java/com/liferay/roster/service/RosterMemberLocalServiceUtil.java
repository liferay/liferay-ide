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

package com.liferay.roster.service;

import aQute.bnd.annotation.ProviderType;

import com.liferay.osgi.util.ServiceTrackerFactory;

import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides the local service utility for RosterMember. This utility wraps
 * {@link com.liferay.roster.service.impl.RosterMemberLocalServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see RosterMemberLocalService
 * @see com.liferay.roster.service.base.RosterMemberLocalServiceBaseImpl
 * @see com.liferay.roster.service.impl.RosterMemberLocalServiceImpl
 * @generated
 */
@ProviderType
public class RosterMemberLocalServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link com.liferay.roster.service.impl.RosterMemberLocalServiceImpl} and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	* Adds the roster member to the database. Also notifies the appropriate model listeners.
	*
	* @param rosterMember the roster member
	* @return the roster member that was added
	*/
	public static com.liferay.roster.model.RosterMember addRosterMember(
		com.liferay.roster.model.RosterMember rosterMember) {
		return getService().addRosterMember(rosterMember);
	}

	/**
	* Creates a new roster member with the primary key. Does not add the roster member to the database.
	*
	* @param rosterMemberId the primary key for the new roster member
	* @return the new roster member
	*/
	public static com.liferay.roster.model.RosterMember createRosterMember(
		long rosterMemberId) {
		return getService().createRosterMember(rosterMemberId);
	}

	/**
	* @throws PortalException
	*/
	public static com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
		com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().deletePersistedModel(persistedModel);
	}

	/**
	* Deletes the roster member from the database. Also notifies the appropriate model listeners.
	*
	* @param rosterMember the roster member
	* @return the roster member that was removed
	*/
	public static com.liferay.roster.model.RosterMember deleteRosterMember(
		com.liferay.roster.model.RosterMember rosterMember) {
		return getService().deleteRosterMember(rosterMember);
	}

	/**
	* Deletes the roster member with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param rosterMemberId the primary key of the roster member
	* @return the roster member that was removed
	* @throws PortalException if a roster member with the primary key could not be found
	*/
	public static com.liferay.roster.model.RosterMember deleteRosterMember(
		long rosterMemberId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().deleteRosterMember(rosterMemberId);
	}

	public static com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	* Performs a dynamic query on the database and returns the matching rows.
	*
	* @param dynamicQuery the dynamic query
	* @return the matching rows
	*/
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.roster.model.impl.RosterMemberModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param dynamicQuery the dynamic query
	* @param start the lower bound of the range of model instances
	* @param end the upper bound of the range of model instances (not inclusive)
	* @return the range of matching rows
	*/
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {
		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.roster.model.impl.RosterMemberModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param dynamicQuery the dynamic query
	* @param start the lower bound of the range of model instances
	* @param end the upper bound of the range of model instances (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching rows
	*/
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {
		return getService()
				   .dynamicQuery(dynamicQuery, start, end, orderByComparator);
	}

	/**
	* Returns the number of rows matching the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @return the number of rows matching the dynamic query
	*/
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	* Returns the number of rows matching the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @param projection the projection to apply to the query
	* @return the number of rows matching the dynamic query
	*/
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {
		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static com.liferay.roster.model.RosterMember fetchRosterMember(
		long rosterMemberId) {
		return getService().fetchRosterMember(rosterMemberId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery getActionableDynamicQuery() {
		return getService().getActionableDynamicQuery();
	}

	public static com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery getIndexableActionableDynamicQuery() {
		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	* Returns the OSGi service identifier.
	*
	* @return the OSGi service identifier
	*/
	public static java.lang.String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	* Returns the roster member with the primary key.
	*
	* @param rosterMemberId the primary key of the roster member
	* @return the roster member
	* @throws PortalException if a roster member with the primary key could not be found
	*/
	public static com.liferay.roster.model.RosterMember getRosterMember(
		long rosterMemberId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().getRosterMember(rosterMemberId);
	}

	/**
	* Returns a range of all the roster members.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.roster.model.impl.RosterMemberModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of roster members
	* @param end the upper bound of the range of roster members (not inclusive)
	* @return the range of roster members
	*/
	public static java.util.List<com.liferay.roster.model.RosterMember> getRosterMembers(
		int start, int end) {
		return getService().getRosterMembers(start, end);
	}

	/**
	* Returns the number of roster members.
	*
	* @return the number of roster members
	*/
	public static int getRosterMembersCount() {
		return getService().getRosterMembersCount();
	}

	/**
	* Updates the roster member in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param rosterMember the roster member
	* @return the roster member that was updated
	*/
	public static com.liferay.roster.model.RosterMember updateRosterMember(
		com.liferay.roster.model.RosterMember rosterMember) {
		return getService().updateRosterMember(rosterMember);
	}

	public static RosterMemberLocalService getService() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<RosterMemberLocalService, RosterMemberLocalService> _serviceTracker =
		ServiceTrackerFactory.open(RosterMemberLocalService.class);
}