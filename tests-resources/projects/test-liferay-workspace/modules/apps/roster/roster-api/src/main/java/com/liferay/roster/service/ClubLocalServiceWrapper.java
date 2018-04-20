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
 * Provides a wrapper for {@link ClubLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ClubLocalService
 * @generated
 */
@ProviderType
public class ClubLocalServiceWrapper implements ClubLocalService,
	ServiceWrapper<ClubLocalService> {
	public ClubLocalServiceWrapper(ClubLocalService clubLocalService) {
		_clubLocalService = clubLocalService;
	}

	/**
	* Adds the club to the database. Also notifies the appropriate model listeners.
	*
	* @param club the club
	* @return the club that was added
	*/
	@Override
	public com.liferay.roster.model.Club addClub(
		com.liferay.roster.model.Club club) {
		return _clubLocalService.addClub(club);
	}

	/**
	* Creates a new club with the primary key. Does not add the club to the database.
	*
	* @param clubId the primary key for the new club
	* @return the new club
	*/
	@Override
	public com.liferay.roster.model.Club createClub(long clubId) {
		return _clubLocalService.createClub(clubId);
	}

	/**
	* Deletes the club from the database. Also notifies the appropriate model listeners.
	*
	* @param club the club
	* @return the club that was removed
	*/
	@Override
	public com.liferay.roster.model.Club deleteClub(
		com.liferay.roster.model.Club club) {
		return _clubLocalService.deleteClub(club);
	}

	/**
	* Deletes the club with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param clubId the primary key of the club
	* @return the club that was removed
	* @throws PortalException if a club with the primary key could not be found
	*/
	@Override
	public com.liferay.roster.model.Club deleteClub(long clubId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _clubLocalService.deleteClub(clubId);
	}

	/**
	* @throws PortalException
	*/
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
		com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _clubLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _clubLocalService.dynamicQuery();
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
		return _clubLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.roster.model.impl.ClubModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _clubLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.roster.model.impl.ClubModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _clubLocalService.dynamicQuery(dynamicQuery, start, end,
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
		return _clubLocalService.dynamicQueryCount(dynamicQuery);
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
		return _clubLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public com.liferay.roster.model.Club fetchClub(long clubId) {
		return _clubLocalService.fetchClub(clubId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery getActionableDynamicQuery() {
		return _clubLocalService.getActionableDynamicQuery();
	}

	/**
	* Returns the club with the primary key.
	*
	* @param clubId the primary key of the club
	* @return the club
	* @throws PortalException if a club with the primary key could not be found
	*/
	@Override
	public com.liferay.roster.model.Club getClub(long clubId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _clubLocalService.getClub(clubId);
	}

	/**
	* Returns a range of all the clubs.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.roster.model.impl.ClubModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of clubs
	* @param end the upper bound of the range of clubs (not inclusive)
	* @return the range of clubs
	*/
	@Override
	public java.util.List<com.liferay.roster.model.Club> getClubs(int start,
		int end) {
		return _clubLocalService.getClubs(start, end);
	}

	/**
	* Returns the number of clubs.
	*
	* @return the number of clubs
	*/
	@Override
	public int getClubsCount() {
		return _clubLocalService.getClubsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery getIndexableActionableDynamicQuery() {
		return _clubLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	* Returns the OSGi service identifier.
	*
	* @return the OSGi service identifier
	*/
	@Override
	public java.lang.String getOSGiServiceIdentifier() {
		return _clubLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _clubLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	* Updates the club in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param club the club
	* @return the club that was updated
	*/
	@Override
	public com.liferay.roster.model.Club updateClub(
		com.liferay.roster.model.Club club) {
		return _clubLocalService.updateClub(club);
	}

	@Override
	public ClubLocalService getWrappedService() {
		return _clubLocalService;
	}

	@Override
	public void setWrappedService(ClubLocalService clubLocalService) {
		_clubLocalService = clubLocalService;
	}

	private ClubLocalService _clubLocalService;
}