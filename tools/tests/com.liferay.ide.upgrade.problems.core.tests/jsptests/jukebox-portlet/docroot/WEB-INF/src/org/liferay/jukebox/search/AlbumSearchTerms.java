/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package org.liferay.jukebox.search;

import com.liferay.portal.kernel.dao.search.DAOParamUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import javax.portlet.PortletRequest;

/**
 * @author Eudaldo Alonso
 */
public class AlbumSearchTerms extends AlbumDisplayTerms {

	public AlbumSearchTerms(PortletRequest portletRequest) {
		super(portletRequest);

		artist = ParamUtil.getString(portletRequest, ARTIST);
		groupId = ParamUtil.getLong(portletRequest, GROUP_ID);
		title = DAOParamUtil.getString(portletRequest, TITLE);
		year = ParamUtil.getInteger(portletRequest, YEAR);
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

}