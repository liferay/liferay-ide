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

package com.liferay.ide.studio.ui;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * @author Gregory Amerson
 */
public interface IProductPreferences {

	public static final IEclipsePreferences defaultPrefs = DefaultScope.INSTANCE.getNode(StudioPlugin.PLUGIN_ID);

	public static final String BUNDLED_PORTAL_DIR_NAME = defaultPrefs.get("bundled.portal.dir.name", null);

	public static final String BUNDLED_PORTAL_PATH_ZIP = defaultPrefs.get("bundled.portal.path.zip", null);

	public static final String SNIPPETS_IMPORT_PATH = defaultPrefs.get("snippets.import.path", null);

}