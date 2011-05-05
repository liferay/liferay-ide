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

package com.liferay.ide.eclipse.server.aws.core.util;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.server.aws.core.AWSCorePlugin;
import com.liferay.ide.eclipse.server.aws.core.IBeanstalkRuntime;
import com.liferay.ide.eclipse.server.aws.core.IBeanstalkServer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Greg Amerson
 */
public class BeanstalkUtil {

	public static IRuntime getRuntimeByName(String runtimeName) {
		IRuntime retval = null;
		IRuntime[] runtimes = AWSCorePlugin.getBeanstalkRuntimes();

		if (!CoreUtil.isNullOrEmpty(runtimes)) {
			for (IRuntime runtime : runtimes) {
				if (runtime.getName().equals(runtimeName)) {
					retval = runtime;
					break;
				}
			}
		}

		return retval;
	}

	public static IBeanstalkRuntime getBeanstalkRuntime(IRuntime runtime) {
		if (runtime != null) {
			Object BeanstalkRuntime = runtime.loadAdapter(IBeanstalkRuntime.class, null);

			if (BeanstalkRuntime instanceof IBeanstalkRuntime) {
				return (IBeanstalkRuntime) BeanstalkRuntime;
			}
		}

		return null;
	}

	public static String[] getBeanstalkRuntimeNames() {
		List<String> ids = new ArrayList<String>();

		IRuntime[] runtimes = AWSCorePlugin.getBeanstalkRuntimes();

		if (!CoreUtil.isNullOrEmpty(runtimes)) {
			for (IRuntime runtime : runtimes) {
				ids.add(runtime.getName());
			}
		}

		return ids.toArray(new String[0]);
	}

	public static IBeanstalkServer getBeanstalkServer(IServer server) {
		if (server != null) {
			return (IBeanstalkServer) server.loadAdapter(IBeanstalkServer.class, null);
		}

		return null;
	}

	public static boolean isBeanstalkRuntime(IRuntime runtime) {
		return runtime != null &&
			runtime.getRuntimeType().getId().contains("com.liferay.ide.eclipse.aws.beanstalk.runtime") &&
			getBeanstalkRuntime(runtime) != null;
	}

	public static void terminateLaunchesForConfig(ILaunchConfigurationWorkingCopy config)
		throws DebugException {
		ILaunch[] launches = DebugPlugin.getDefault().getLaunchManager().getLaunches();

		for (ILaunch launch : launches) {
			if (launch.getLaunchConfiguration().equals(config)) {
				launch.terminate();
			}
		}
	}

	public static String getAppName(IProject project) {
		return project.getName();
	}
}
