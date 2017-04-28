/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.studio.ui;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * @author Gregory Amerson
 */
public interface IProductPreferences
{

    public static final IEclipsePreferences _defaultPrefs = DefaultScope.INSTANCE.getNode( StudioPlugin.PLUGIN_ID );

    public static final String BUNDLED_PORTAL_PATH_ZIP = _defaultPrefs.get( "bundled.portal.path.zip", null );

    public static final String BUNDLED_PORTAL_DIR_NAME = _defaultPrefs.get( "bundled.portal.dir.name", null );

    public static final String SNIPPETS_IMPORT_PATH = _defaultPrefs.get( "snippets.import.path", null );

}
