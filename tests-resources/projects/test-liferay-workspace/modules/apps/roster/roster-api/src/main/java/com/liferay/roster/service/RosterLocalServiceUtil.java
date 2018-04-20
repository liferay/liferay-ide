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
 * Provides the local service utility for Roster. This utility wraps
 * {@link com.liferay.roster.service.impl.RosterLocalServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see RosterLocalService
 * @see com.liferay.roster.service.base.RosterLocalServiceBaseImpl
 * @see com.liferay.roster.service.impl.RosterLocalServiceImpl
 * @generated
 */
@ProviderType
public class RosterLocalServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link com.liferay.roster.service.impl.RosterLocalServiceImpl} and rerun ServiceBuilder to regenerate this class.
	 */
	public static com.liferay.roster.model.Roster addRoster(long clubId,
		java.lang.String name,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {
		return getService().addRoster(clubId, name, serviceContext);
	}

	/**
	* Adds the roster to the database. Also notifies the appropriate model listeners.
	*
	* @param roster the roster
	* @return the roster that was added
	*/
	public static com.liferay.roster.model.Roster addRoster(
		com.liferay.roster.model.Roster roster) {
		return getService().addRoster(roster);
	}

	/**
	* Creates a new roster with the primary key. Does not add the roster to the database.
	*
	* @param rosterId the primary key for the new roster
	* @return the new roster
	*/
	public static com.liferay.roster.model.Roster createRoster(long rosterId) {
		return getService().createRoster(rosterId);
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
	* Deletes the roster from the database. Also notifies the appropriate model listeners.
	*
	* @param roster the roster
	* @return the roster that was removed
	*/
	public static com.liferay.roster.model.Roster deleteRoster(
		com.liferay.roster.model.Roster roster) {
		return getService().deleteRoster(roster);
	}

	/**
	* Deletes the roster with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param rosterId the primary key of the roster
	* @return the roster that was removed
	* @throws PortalException if a roster with the primary key could not be found
	*/
	public static com.liferay.roster.model.Roster deleteRoster(long rosterId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().deleteRoster(rosterId);
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
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.roster.model.impl.RosterModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.roster.model.impl.RosterModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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

	public static com.liferay.roster.model.Roster fetchRoster(long rosterId) {
		return getService().fetchRoster(rosterId);
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
	* Returns the roster with the primary key.
	*
	* @param rosterId the primary key of the roster
	* @return the roster
	* @throws PortalException if a roster with the primary key could not be found
	*/
	public static com.liferay.roster.model.Roster getRoster(long rosterId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().getRoster(rosterId);
	}

	/**
	* Returns a range of all the rosters.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.roster.model.impl.RosterModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of rosters
	* @param end the upper bound of the range of rosters (not inclusive)
	* @return the range of rosters
	*/
	public static java.util.List<com.liferay.roster.model.Roster> getRosters(
		int start, int end) {
		return getService().getRosters(start, end);
	}

	/**
	* Returns the number of rosters.
	*
	* @return the number of rosters
	*/
	public static int getRostersCount() {
		return getService().getRostersCount();
	}

	/**
	* Updates the roster in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param roster the roster
	* @return the roster that was updated
	*/
	public static com.liferay.roster.model.Roster updateRoster(
		com.liferay.roster.model.Roster roster) {
		return getService().updateRoster(roster);
	}

	public static com.liferay.roster.model.Roster updateRoster(long rosterId,
		long clubId, java.lang.String name,
		com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().updateRoster(rosterId, clubId, name, serviceContext);
	}

	public static RosterLocalService getService() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<RosterLocalService, RosterLocalService> _serviceTracker =
		ServiceTrackerFactory.open(RosterLocalService.class);
}