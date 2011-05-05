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

import java.io.File;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("rawtypes")
public interface IBeanstalkAdminService {

	String getHost();

	String getServerState();

	boolean isAlive();

	boolean isAppInstalled(String appName);

	boolean isAppStarted(String appName);

	Vector listApplications();

	void setOptions(Map options);

	Object startApplication(File scriptFile, String appName);

	Object startApplication(String appName);

	Object stopApplication(String appName);

	Object uninstallApplication(File scriptFile, String appName, Object monitor);

	Object updateApplication(String appName, String pathToContents, Object monitor);

	Map getDebugOptions();

	Object installApplication(String env, String absolutePath, String appName, IProgressMonitor submon);

}
