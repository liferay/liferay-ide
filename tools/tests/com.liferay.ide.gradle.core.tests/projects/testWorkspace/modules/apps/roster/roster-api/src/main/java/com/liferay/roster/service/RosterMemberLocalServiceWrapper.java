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

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link RosterMemberLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see RosterMemberLocalService
 * @generated
 */
@ProviderType
public class RosterMemberLocalServiceWrapper implements RosterMemberLocalService,
	ServiceWrapper<RosterMemberLocalService> {
	public RosterMemberLocalServiceWrapper(
		RosterMemberLocalService rosterMemberLocalService) {
		_rosterMemberLocalService = rosterMemberLocalService;
	}

	/**
	* Adds the roster member to the database. Also notifies the appropriate model listeners.
	*
	* @param rosterMember the roster member
	* @return the roster member that was added
	*/
	@Override
	public com.liferay.roster.model.RosterMember addRosterMember(
		com.liferay.roster.model.RosterMember rosterMember) {
		return _rosterMemberLocalService.addRosterMember(rosterMember);
	}

	/**
	* Creates a new roster member with the primary key. Does not add the roster member to the database.
	*
	* @param rosterMemberId the primary key for the new roster member
	* @return the new roster member
	*/
	@Override
	public com.liferay.roster.model.RosterMember createRosterMember(
		long rosterMemberId) {
		return _rosterMemberLocalService.createRosterMember(rosterMemberId);
	}

	/**
	* @throws PortalException
	*/
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
		com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _rosterMemberLocalService.deletePersistedModel(persistedModel);
	}

	/**
	* Deletes the roster member from the database. Also notifies the appropriate model listeners.
	*
	* @param rosterMember the roster member
	* @return the roster member that was removed
	*/
	@Override
	public com.liferay.roster.model.RosterMember deleteRosterMember(
		com.liferay.roster.model.RosterMember rosterMember) {
		return _rosterMemberLocalService.deleteRosterMember(rosterMember);
	}

	/**
	* Deletes the roster member with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param rosterMemberId the primary key of the roster member
	* @return the roster member that was removed
	* @throws PortalException if a roster member with the primary key could not be found
	*/
	@Override
	public com.liferay.roster.model.RosterMember deleteRosterMember(
		long rosterMemberId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _rosterMemberLocalService.deleteRosterMember(rosterMemberId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _rosterMemberLocalService.dynamicQuery();
	}

	/**
	* Performs a dynamic query on the database and returns the matching rows.
	*
	* @param dynamicQuery the dynamic query
	* @return the matching rows
	*/
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {
		return _rosterMemberLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {
		return _rosterMemberLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {
		return _rosterMemberLocalService.dynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	* Returns the number of rows matching the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @return the number of rows matching the dynamic query
	*/
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {
		return _rosterMemberLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	* Returns the number of rows matching the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @param projection the projection to apply to the query
	* @return the number of rows matching the dynamic query
	*/
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {
		return _rosterMemberLocalService.dynamicQueryCount(dynamicQuery,
			projection);
	}

	@Override
	public com.liferay.roster.model.RosterMember fetchRosterMember(
		long rosterMemberId) {
		return _rosterMemberLocalService.fetchRosterMember(rosterMemberId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery getActionableDynamicQuery() {
		return _rosterMemberLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery getIndexableActionableDynamicQuery() {
		return _rosterMemberLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	* Returns the OSGi service identifier.
	*
	* @return the OSGi service identifier
	*/
	@Override
	public java.lang.String getOSGiServiceIdentifier() {
		return _rosterMemberLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _rosterMemberLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	* Returns the roster member with the primary key.
	*
	* @param rosterMemberId the primary key of the roster member
	* @return the roster member
	* @throws PortalException if a roster member with the primary key could not be found
	*/
	@Override
	public com.liferay.roster.model.RosterMember getRosterMember(
		long rosterMemberId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _rosterMemberLocalService.getRosterMember(rosterMemberId);
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
	@Override
	public java.util.List<com.liferay.roster.model.RosterMember> getRosterMembers(
		int start, int end) {
		return _rosterMemberLocalService.getRosterMembers(start, end);
	}

	/**
	* Returns the number of roster members.
	*
	* @return the number of roster members
	*/
	@Override
	public int getRosterMembersCount() {
		return _rosterMemberLocalService.getRosterMembersCount();
	}

	/**
	* Updates the roster member in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param rosterMember the roster member
	* @return the roster member that was updated
	*/
	@Override
	public com.liferay.roster.model.RosterMember updateRosterMember(
		com.liferay.roster.model.RosterMember rosterMember) {
		return _rosterMemberLocalService.updateRosterMember(rosterMember);
	}

	@Override
	public RosterMemberLocalService getWrappedService() {
		return _rosterMemberLocalService;
	}

	@Override
	public void setWrappedService(
		RosterMemberLocalService rosterMemberLocalService) {
		_rosterMemberLocalService = rosterMemberLocalService;
	}

	private RosterMemberLocalService _rosterMemberLocalService;
}