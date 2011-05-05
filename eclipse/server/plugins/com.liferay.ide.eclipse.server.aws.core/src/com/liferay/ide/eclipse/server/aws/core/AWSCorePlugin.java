package com.liferay.ide.eclipse.server.aws.core;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.server.aws.core.util.BeanstalkUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class AWSCorePlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.liferay.ide.eclipse.server.aws.core"; //$NON-NLS-1$

	// The shared instance
	private static AWSCorePlugin plugin;
	
	/**
	 * The constructor
	 */
	public AWSCorePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static AWSCorePlugin getDefault() {
		return plugin;
	}

	public static IRuntime[] getBeanstalkRuntimes() {
		List<IRuntime> beanstalkRuntimes = new ArrayList<IRuntime>();
		IRuntime[] runtimes = ServerCore.getRuntimes();

		if (!CoreUtil.isNullOrEmpty(runtimes)) {
			for (IRuntime runtime : runtimes) {
				if (BeanstalkUtil.isBeanstalkRuntime(runtime)) {
					beanstalkRuntimes.add(runtime);
				}
			}
		}

		return beanstalkRuntimes.toArray(new IRuntime[0]);
	}

	public static IStatus createErrorStatus(Exception ex) {
		return new Status(IStatus.ERROR, PLUGIN_ID, ex.getMessage(), ex);
	}

	public static IStatus createErrorStatus(String msg) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg);
	}

	public static IStatus createErrorStatus(String msg, Exception ex) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, ex);
	}

	public static IStatus createInfoStatus(String message) {
		return new Status(IStatus.INFO, PLUGIN_ID, message);
	}

	public static IStatus createWarningStatus(String msg) {
		return new Status(IStatus.WARNING, PLUGIN_ID, msg);
	}

	public static void logError(String msg, Exception ex) {
		getDefault().getLog().log(createErrorStatus(msg, ex));
	}

	public static void updateConnectionSettings(IBeanstalkServer loadAdapter) {
		// TODO Implement updateConnectionSettings method on class AWSCorePlugin

	}

	public static URL getPluginEntry(String path) {
		return getDefault().getBundle().getEntry(path);
	}

	public static IEclipsePreferences getPreferences() {
		return new InstanceScope().getNode(PLUGIN_ID);
	}

	public static IPath getTempLocation(String prefix, String fileName) {
		return getDefault().getStateLocation().append("tmp").append(
			prefix + "/" + System.currentTimeMillis() + (CoreUtil.isNullOrEmpty(fileName) ? "" : "/" + fileName));
	}

	private static Map<String, IBeanstalkAdminService> services = null;

	public static IBeanstalkAdminService getBeanstalkAdminService(final IBeanstalkServer server) {
		if (services == null) {
			services = new HashMap<String, IBeanstalkAdminService>();

			ServerCore.addServerLifecycleListener(new IServerLifecycleListener() {

				public void serverAdded(IServer server) {
				}

				public void serverChanged(IServer server) {
				}

				public void serverRemoved(IServer s) {
					if (server.equals(s)) {
						IBeanstalkAdminService service = services.get(server.getId());

						if (service != null) {
							service = null;
							services.put(server.getId(), null);
						}
					}
				}
			});
		}

		IBeanstalkAdminService service = services.get(server.getId());

		if (service == null) {
			Class<?>[] interfaces = new Class<?>[] { IBeanstalkAdminService.class };

			service = new BeanstalkAdminServiceProxy(server);

			services.put(server.getId(), service);
		}

		return service;
	}

}
