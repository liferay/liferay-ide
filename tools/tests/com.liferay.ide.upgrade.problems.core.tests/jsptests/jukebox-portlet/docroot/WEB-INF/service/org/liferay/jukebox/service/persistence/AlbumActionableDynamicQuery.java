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

package org.liferay.jukebox.service.persistence;

import com.liferay.portal.kernel.dao.orm.BaseActionableDynamicQuery;
import com.liferay.portal.kernel.exception.SystemException;

import org.liferay.jukebox.model.Album;
import org.liferay.jukebox.service.AlbumLocalServiceUtil;

/**
 * @author Julio Camarero
 * @generated
 */
public abstract class AlbumActionableDynamicQuery
	extends BaseActionableDynamicQuery {
	public AlbumActionableDynamicQuery() throws SystemException {
		setBaseLocalService(AlbumLocalServiceUtil.getService());
		setClass(Album.class);

		setClassLoader(org.liferay.jukebox.service.ClpSerializer.class.getClassLoader());

		setPrimaryKeyPropertyName("albumId");
	}
}