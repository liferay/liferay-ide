/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.eclipse.server.aws.core;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.model.IURLProvider;

/**
 * @author Greg Amerson
 */
public interface IBeanstalkServer extends IURLProvider {

	public static final IEclipsePreferences defaultPrefs = new DefaultScope().getNode(AWSCorePlugin.PLUGIN_ID);

	String ATTR_HOSTNAME = "hostname";

	String ATTR_PASSWORD = "password";

	String ATTR_USERNAME = "username";

	long SERVERS_VIEW_UPDATE_DELAY = defaultPrefs.getLong("servers.view.update.delay", 5000);

	String getHost();

	String getId();

	String getPassword();

	IRuntime getRuntime();

	String getUsername();

	boolean isLocal();

}
