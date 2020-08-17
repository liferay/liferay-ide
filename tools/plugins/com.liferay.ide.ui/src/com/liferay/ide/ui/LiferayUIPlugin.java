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

package com.liferay.ide.ui;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.ui.templates.ServiceClassNameResolver;
import com.liferay.ide.ui.util.UIUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import java.lang.management.ManagementFactory;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.RepositoryTracker;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;
import org.eclipse.mylyn.commons.notifications.core.INotificationService;
import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.mylyn.commons.notifications.ui.NotificationsUi;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TaskBar;
import org.eclipse.swt.widgets.TaskItem;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.IDocumentProvider;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.prefs.BackingStoreException;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Gregory Amerson
 */
@SuppressWarnings({"restriction", "deprecation"})
public class LiferayUIPlugin extends AbstractUIPlugin implements IStartup {

	public static final String FIRST_STARTUP_COMPLETE = "FIRST_STARTUP_COMPLETE";

	public static final String IMG_LIFERAY_ICON_SMALL = "IMG_LIFERAY_ICON_SMALL";

	// The plugin ID

	public static final String PLUGIN_ID = "com.liferay.ide.ui";

	public static final String SHOULD_SHOW_NOTIFICATIONS = "SHOULD_SHOW_NOTIFICATIONS";

	// The shared instance

	public static void clearAllPersistentSettings() throws BackingStoreException {
		IEclipsePreferences tomcatCorePrefs = new InstanceScope().getNode("com.liferay.ide.server.tomcat.core");

		tomcatCorePrefs.remove("ADDED_EXT_PLUGIN_TOGGLE_KEY");
		tomcatCorePrefs.remove("ADDED_EXT_PLUGIN_WITHOUT_ZIP_TOGGLE_KEY");
		tomcatCorePrefs.remove("REMOVE_EXT_PLUGIN_TOGGLE_KEY");

		tomcatCorePrefs.flush();
	}

	public static IStatus createErrorStatus(String string) {
		return new Status(IStatus.ERROR, PLUGIN_ID, string);
	}

	public static IWorkbenchPage getActivePage() {
		IWorkbench workbench = PlatformUI.getWorkbench();

		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();

		return workbenchWindow.getActivePage();
	}

	public static LiferayUIPlugin getDefault() {
		return _plugin;
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public static void logError(Exception e) {
		logError(e.getMessage(), e);
	}

	public static void logError(String msg, Exception e) {
		ILog log = getDefault().getLog();

		log.log(new Status(IStatus.ERROR, PLUGIN_ID, msg, e));
	}

	public LiferayUIPlugin() {
	}

	@Override
	public void earlyStartup() {
		_registerMBeans();

		_lookupLiferay7SDKDir();

		_applyWorkspaceBadge();

		_registerResolvers();

		if (_shouldShowNotifications() && !_matchRequiredJavaVersion()) {
			INotificationService notificationService = NotificationsUi.getService();

			notificationService.notify(Collections.singletonList(_createJava8RequiredNotification()));
		}
	}

	public Image getImage(String key) {
		return getImageRegistry().get(key);
	}

	public ImageDescriptor getImageDescriptor(String key) {
		getImageRegistry();

		return imageDescriptors.get(key);
	}

	public synchronized IDocumentProvider getTextFileDocumentProvider() {
		if (fTextFileDocumentProvider == null) {
			fTextFileDocumentProvider = new TextFileDocumentProvider();
		}

		return fTextFileDocumentProvider;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		_addRepository();

		_enableGradleAutoSync();

		_plugin = this;

		_serviceListener = new ServiceListener() {

			@Override
			public void serviceChanged(ServiceEvent event) {
				ServiceReference<?> serviceReference = event.getServiceReference();

				String[] objectClass = (String[])serviceReference.getProperty("objectClass");

				if ((event.getType() == ServiceEvent.UNREGISTERING) &&
					objectClass[0].equals("org.eclipse.e4.ui.workbench.IWorkbench")) {

					IPath location = Platform.getLocation();

					File file = new File(
						FileUtil.toOSString(location.append(".metadata/.plugins/org.eclipse.e4.workbench")),
						"workbench.xmi");

					String content = FileUtil.readContents(file, true);

					if ((content != null) && (content.indexOf("label=\"Liferay\"") != -1)) {
						content = content.replaceFirst("label=\"Liferay\"", "label=\"Liferay Plugins\"");

						try (InputStream ins = new ByteArrayInputStream(content.getBytes())) {
							FileUtil.writeFile(file, ins);
						}
						catch (Exception e) {
						}
					}
				}
			}

		};

		context.addServiceListener(_serviceListener);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		_plugin = null;

		if (_serviceListener != null) {
			context.removeServiceListener(_serviceListener);
		}

		super.stop(context);
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		registerImage(reg, IMG_LIFERAY_ICON_SMALL, "/icons/liferay_logo_16.png");
	}

	protected void registerImage(ImageRegistry registry, String key, String path) {
		try {
			ImageDescriptor id = ImageDescriptor.createFromURL(getBundle().getEntry(path));

			imageDescriptors.put(key, id);

			registry.put(key, id);
		}
		catch (Exception e) {
		}
	}

	protected TextFileDocumentProvider fTextFileDocumentProvider;
	protected Map<String, ImageDescriptor> imageDescriptors = new HashMap<>();

	private void _addRepository() throws URISyntaxException {
		ProvisioningUI provisioningUI = ProvisioningUI.getDefaultUI();

		ProvisioningSession provisioningSession = provisioningUI.getSession();

		RepositoryTracker repositoryTracker = provisioningUI.getRepositoryTracker();

		URI[] knownRepositories = repositoryTracker.getKnownRepositories(provisioningSession);

		URI liferayUri = new URI("https://releases.liferay.com/tools/ide/latest/stable/");

		boolean containLiferayUri = Stream.of(
			knownRepositories
		).anyMatch(
			uri -> Objects.equals(uri, liferayUri)
		);

		if (!containLiferayUri) {
			repositoryTracker.addRepository(liferayUri, "Liferay IDE Stable releases", provisioningSession);
		}
	}

	private void _applyWorkspaceBadge() {
		IWorkspaceRoot workspaceRoot = CoreUtil.getWorkspaceRoot();

		IPath location = workspaceRoot.getLocation();

		String workspaceName = location.lastSegment();

		UIUtil.async(
			new Runnable() {

				@Override
				public void run() {
					try {
						Display display = Display.getDefault();

						Shell shell = display.getActiveShell();
						TaskBar taskBar = display.getSystemTaskBar();

						TaskItem taskItem = taskBar.getItem(shell);

						if (taskItem == null) {
							taskItem = taskBar.getItem(null);
						}

						taskItem.setOverlayText(workspaceName);
					}
					catch (Exception e) {

						// ignore

					}
				}

			});
	}

	private AbstractNotification _createJava8RequiredNotification() {
		Date date = new Date();

		return new AbstractUiNotification("com.liferay.ide.ui.notifications.java8required") {

			@SuppressWarnings({"rawtypes", "unchecked"})
			public Object getAdapter(Class adapter) {
				return null;
			}

			@Override
			public Date getDate() {
				return date;
			}

			@Override
			public String getDescription() {
				String javaVersion = System.getProperty("java.specification.version");

				return "This Eclipse instance is running on java " + javaVersion + "\n" +
					"Liferay IDE needs at least Java 1.8 to run, please launch Eclipse with 1.8 and try again.";
			}

			@Override
			public String getLabel() {
				return "Java 8 Required";
			}

			@Override
			public Image getNotificationImage() {
				return null;
			}

			@Override
			public Image getNotificationKindImage() {
				return null;
			}

			@Override
			public void open() {
			}

		};
	}

	private void _enableGradleAutoSync() {
		IEclipsePreferences gradlePrefs = InstanceScope.INSTANCE.getNode("org.eclipse.buildship.core");

		String autoSync = "auto.sync";

		try {
			String[] gradlePrefsKeys = gradlePrefs.keys();

			boolean exist = Stream.of(
				gradlePrefsKeys
			).anyMatch(
				gradlePrefsKey -> autoSync.equals(gradlePrefsKey)
			);

			if (!exist) {
				gradlePrefs.putBoolean(autoSync, true);

				gradlePrefs.flush();
			}
		}
		catch (BackingStoreException bse) {
		}
	}

	private void _lookupLiferay7SDKDir() {
		String liferay7SDKdir = System.getProperty("liferay7.sdk.dir");

		if ((liferay7SDKdir != null) && liferay7SDKdir.startsWith("\"")) {
			liferay7SDKdir = liferay7SDKdir.substring(1);
		}

		if ((liferay7SDKdir != null) && liferay7SDKdir.endsWith("\"")) {
			liferay7SDKdir = liferay7SDKdir.substring(0, liferay7SDKdir.length() - 1);
		}

		if (liferay7SDKdir != null) {
			SDK sdk = SDKUtil.createSDKFromLocation(new Path(liferay7SDKdir));

			if (sdk != null) {
				new WorkspaceJob(
					"Opening Liferay 7 Plugins SDK Project"
				) {

					@Override
					public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
						SDKUtil.openAsProject(sdk);

						return Status.OK_STATUS;
					}

				}.schedule();
			}
		}
	}

	private boolean _matchRequiredJavaVersion() {
		String javaVersion = System.getProperty("java.specification.version");

		Version currentVersion = new Version(javaVersion);

		Version requiredVersion = new Version("1.8");

		if (currentVersion.compareTo(requiredVersion) >= 0) {
			return true;
		}

		return false;
	}

	private void _registerMBeans() {
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName name = new ObjectName("com.liferay.ide.ui:type=WorkspaceHelper");
			WorkspaceHelper mbean = new WorkspaceHelper();

			mbs.registerMBean(mbean, name);
		}
		catch (Exception e) {
			logError("Unable to start workspaceHelper MBean", e);
		}
	}

	private void _registerResolvers() {
		JavaPlugin javaPlugin = JavaPlugin.getDefault();

		ContextTypeRegistry templateContextRegistry = javaPlugin.getTemplateContextRegistry();

		Iterator<?> ctIter = templateContextRegistry.contextTypes();

		while (ctIter.hasNext()) {
			Object next = ctIter.next();

			if (next instanceof TemplateContextType) {
				TemplateContextType contextType = (TemplateContextType)next;

				if (Objects.equals("java", contextType.getId())) {
					contextType.addResolver(new ServiceClassNameResolver());
				}
			}
		}
	}

	private boolean _shouldShowNotifications() {
		IScopeContext[] scopes = {ConfigurationScope.INSTANCE, InstanceScope.INSTANCE};

		IPreferencesService preferencesService = Platform.getPreferencesService();

		return preferencesService.getBoolean(PLUGIN_ID, SHOULD_SHOW_NOTIFICATIONS, true, scopes);
	}

	private static LiferayUIPlugin _plugin;

	private ServiceListener _serviceListener;

}