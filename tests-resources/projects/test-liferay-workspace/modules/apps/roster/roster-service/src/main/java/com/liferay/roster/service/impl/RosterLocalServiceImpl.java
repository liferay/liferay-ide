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

package com.liferay.roster.service.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.roster.model.Roster;
import com.liferay.roster.service.base.RosterLocalServiceBaseImpl;

/**
 * The implementation of the roster local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.liferay.roster.service.RosterLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see RosterLocalServiceBaseImpl
 * @see com.liferay.roster.service.RosterLocalServiceUtil
 */
@ProviderType
public class RosterLocalServiceImpl extends RosterLocalServiceBaseImpl {

	public Roster addRoster(
		long clubId, String name, ServiceContext serviceContext) {

		Roster roster = rosterLocalService.createRoster(
			counterLocalService.increment());

		roster.setClubId(clubId);
		roster.setName(name);
		roster.setCreateDate(serviceContext.getCreateDate());
		roster.setModifiedDate(serviceContext.getModifiedDate());

		return rosterLocalService.addRoster(roster);
	}

	public Roster updateRoster(
			long rosterId, long clubId, String name,
			ServiceContext serviceContext)
		throws PortalException {

		Roster roster = rosterLocalService.getRoster(rosterId);

		roster.setClubId(clubId);
		roster.setName(name);
		roster.setCreateDate(serviceContext.getCreateDate());
		roster.setModifiedDate(serviceContext.getModifiedDate());

		return rosterLocalService.updateRoster(roster);
	}

}