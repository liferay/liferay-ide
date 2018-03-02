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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.ui.snippets.util.SnippetsUtil;

import java.io.File;
import java.io.FilenameFilter;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.BundleContext;

/**
 * @author Gregory Amerson
 * @author Lovett Li
 */
public class StudioPlugin extends AbstractUIPlugin implements IStartup {

	public static final String ACCEPTED_EULA = "ACCEPTED_EULA";

	public static final String FIRST_LAUNCH_COMPLETE = "FIRST_LAUNCH_COMPLETE";

	public static final String IMPORTED_SNIPPET_FILES = "IMPORTED_SNIPPET_FILES";

	// The shared instance

	public static final String PLUGIN_ID = "com.liferay.ide.studio.ui";

	public static final String PRODUCT_ID = PLUGIN_ID + ".product";

	// The plugin ID

	public static boolean canInitLiferayWorkspace() {
		boolean retVal = false;

		try {
			File bundledPortalFile = new File(
				FileLocator.toFileURL(new URL(IProductPreferences.BUNDLED_PORTAL_PATH_ZIP)).getFile());

			retVal = bundledPortalFile.exists();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return retVal;
	}

	public static IStatus createErrorStatus(Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e);
	}

	public static IStatus createErrorStatus(String msg) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg);
	}

	public static IStatus createErrorStatus(String msg, Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
	}

	public static IStatus createInfoStatus(String msg) {
		return new Status(IStatus.INFO, PLUGIN_ID, msg);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static StudioPlugin getDefault() {
		return _plugin;
	}

	public static boolean isFirstLaunch() {
		IScopeContext[] scopes = {ConfigurationScope.INSTANCE, InstanceScope.INSTANCE};

		return !(Platform.getPreferencesService().getBoolean(PLUGIN_ID, FIRST_LAUNCH_COMPLETE, false, scopes));
	}

	public static boolean isProductRunning() {
		boolean productRunning = false;

		IProduct product = Platform.getProduct();

		if (product != null) {
			String id = product.getId();

			productRunning = PRODUCT_ID.equals(id);
		}

		return productRunning;
	}

	public static void logError(Exception e) {
		logError(e.getMessage(), e);
	}

	public static void logError(IStatus status) {
		StudioPlugin plugin = getDefault();

		plugin.getLog().log(status);
	}

	public static void logError(String msg, Exception ex) {
		logError(createErrorStatus(msg, ex));
	}

	public static void logInfo(String msg) {
		StudioPlugin plugin = getDefault();

		plugin.getLog().log(createInfoStatus(msg));
	}

	/**
	 * The constructor
	 */
	public StudioPlugin() {
	}

	public void earlyStartup() {
		if (!isProductRunning()) {
			return;
		}

		// try
		// {
		// if( isFirstLaunch() )
		// {
		// new InitLiferayWorkspaceHandler().execute( null );
		// }
		// }
		// catch( ExecutionException e )
		// {
		// e.printStackTrace();
		// }

		_importSnippets();
	}

	public IEclipsePreferences getPreferences() {
		return InstanceScope.INSTANCE.getNode(PLUGIN_ID);
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see AbstractUIPlugin#start(org.osgi.framework. BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		_plugin = this;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see AbstractUIPlugin#stop(org.osgi.framework. BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		_plugin = null;
		super.stop(context);
	}

	private void _importSnippets() {
		try {
			URL snippetsImportPath = FileLocator.resolve(new URL(IProductPreferences.SNIPPETS_IMPORT_PATH));

			if (snippetsImportPath != null) {
				File snippetsImportDir = new File(snippetsImportPath.getFile());

				if (snippetsImportDir.exists() && snippetsImportDir.isDirectory()) {
					File[] snippetFiles = snippetsImportDir.listFiles(
						new FilenameFilter() {

							public boolean accept(File dir, String name) {
								if ((name != null) && name.endsWith(".xml")) {
									return true;
								}

								return false;
							}

						});

					if (ListUtil.isNotEmpty(snippetFiles)) {
						for (File snippetFile : snippetFiles) {
							if (_shouldImportFile(snippetFile)) {
								try {
									SnippetsUtil.importSnippetsFromFile(snippetFile);
								}
								catch (Exception ex) {
									logError(
										"Failed to import snippet file: " +
											snippetFile.getName(),
										ex);
								}

								_storeFileImported(snippetFile);
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
		}
	}

	private boolean _shouldImportFile(File importFile) {
		if ((importFile == null) || !importFile.exists() || !importFile.isFile()) {
			return false;
		}

		IScopeContext[] scopes = {InstanceScope.INSTANCE};

		String importedSnippetFiles = Platform.getPreferencesService().getString(
			PLUGIN_ID, IMPORTED_SNIPPET_FILES, null, scopes);

		if (!CoreUtil.isNullOrEmpty(importedSnippetFiles)) {
			String[] fileNames = importedSnippetFiles.split(",");

			if (ListUtil.isNotEmpty(fileNames)) {
				for (String fileName : fileNames) {
					if (fileName.equals(importFile.getName())) {
						return false;
					}
				}
			}
		}

		return true;
	}

	private void _storeFileImported(File importedFile) {
		if (importedFile == null) {
			return;
		}

		IScopeContext[] scopes = {InstanceScope.INSTANCE};

		String importedSnippetFiles = Platform.getPreferencesService().getString(
			PLUGIN_ID, IMPORTED_SNIPPET_FILES, null, scopes);

		String newImportedSnippetFiles = importedSnippetFiles;

		if (CoreUtil.isNullOrEmpty(importedSnippetFiles)) {
			newImportedSnippetFiles = importedFile.getName();
		}
		else {
			newImportedSnippetFiles = importedSnippetFiles + "," + importedFile.getName();
		}

		getPreferences().put(IMPORTED_SNIPPET_FILES, newImportedSnippetFiles);
	}

	private static StudioPlugin _plugin;

}