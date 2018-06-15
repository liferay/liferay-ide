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

package com.liferay.ide.core;

import java.util.List;

import org.eclipse.core.runtime.QualifiedName;

/**
 * @author Gregory Amerson
 */
public interface ILiferayProjectProvider {

	public <T> List<T> getData(String key, Class<T> type, Object... params);

	public String getDisplayName();

	public int getPriority();

	public String getProjectType();

	public String getShortName();

	public ILiferayProject provide(Object adaptable);

	public boolean provides(Class<?> type);

	public QualifiedName LIFERAY_PROJECT = new QualifiedName(LiferayCore.PLUGIN_ID, "LIFERAY_PROJECT");

}