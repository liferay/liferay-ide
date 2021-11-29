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

package com.liferay.ide.server.ui.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;
import java.io.InputStream;

import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;

import java.nio.file.Files;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.ConnectionProfileConstants;
import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.drivers.DriverInstance;
import org.eclipse.datatools.connectivity.drivers.DriverManager;
import org.eclipse.datatools.connectivity.drivers.IPropertySet;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IViewPart;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * @author Simon Jiang
 * @author Charles Wu
 * @author Seiphon Wang
 */
public class CreateDBConnectAction extends AbstractServerRunningAction {

	public CreateDBConnectAction() {
	}

	@SuppressWarnings("resource")
	public void runAction(IAction action) {
		if (selectedServer != null) {
			ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime(selectedServer);

			Properties pluginPackageProperties = _getDatabaseProperties(liferayRuntime.getLiferayHome());

			String driverName = pluginPackageProperties.getProperty(_JDBC_DRIVER_CLASS_NAME, "org.hsqldb.jdbcDriver");

			IRuntime runtime = liferayRuntime.getRuntime();

			String connectionName = runtime.getName();

			String userName = pluginPackageProperties.getProperty("jdbc.default.username");
			String connectionUrl = pluginPackageProperties.getProperty("jdbc.default.url");
			String password = pluginPackageProperties.getProperty("jdbc.default.password");

			try {
				URL[] runtimeLibs = _getLiferayRuntimeLibs(liferayRuntime);

				new Job(
					Msgs.addDBConnnection
				) {

					@Override
					protected IStatus run(IProgressMonitor monitor) {
						try {
							Class<?> classRef = new URLClassLoader(
								runtimeLibs
							).loadClass(
								driverName
							);

							if (classRef != null) {
								ProtectionDomain protectionDomain = classRef.getProtectionDomain();

								CodeSource codeResource = protectionDomain.getCodeSource();

								URL codeResourceLocation = codeResource.getLocation();

								String libPath = codeResourceLocation.getPath();

								String jarPath = URLDecoder.decode(libPath, "UTF-8");

								String driverPath = new File(
									jarPath
								).getAbsolutePath();

								LiferayDatabaseConnection dbConnection = _getLiferayDBConnection(
									driverName, userName, password, connectionUrl, liferayRuntime);

								if (dbConnection != null) {
									if (!dbConnection.addDatabaseConnectionProfile(connectionName, driverPath)) {
										return LiferayServerUI.createErrorStatus(
											"An error happened when create connection profile");
									}

									UIUtil.async(
										new Runnable() {

											public void run() {
												IViewPart dbView = UIUtil.showView(
													"org.eclipse.datatools.connectivity.DataSourceExplorerNavigator");

												dbView.setFocus();
											}

										});
								}
							}
						}
						catch (Exception e) {
							LiferayServerCore.logError(Msgs.addProfileError, e);

							return LiferayServerUI.createErrorStatus("The " + driverName + " class can not be found.");
						}

						return Status.OK_STATUS;
					}

				}.schedule();
			}
			catch (Exception e) {
				LiferayServerCore.logError(Msgs.noDBConnectDriver, e);
			}
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);

		if (!selection.isEmpty() && (selection instanceof IStructuredSelection)) {
			IStructuredSelection sel = (IStructuredSelection)selection;

			List<?> selList = sel.toList();

			if (selList.size() == 1) {
				IProduct product = Platform.getProduct();

				Bundle bundle = product.getDefiningBundle();

				Version version = bundle.getVersion();

				action.setEnabled(version.compareTo(new Version("4.14.0")) < 0);
			}
		}
	}

	@Override
	protected int getRequiredServerState() {
		return IServer.STATE_STARTED | IServer.STATE_STARTING | IServer.STATE_STOPPING | IServer.STATE_STOPPED;
	}

	private String _generateUniqueConnectionProfileName(String connectionProfileName) {
		int index = 1;
		String testName = connectionProfileName;

		ProfileManager profileManagerInstance = ProfileManager.getInstance();

		while (profileManagerInstance.getProfileByName(testName) != null) {
			index++;

			testName = connectionProfileName + String.valueOf(index);
		}

		return testName;
	}

	private Properties _getDatabaseProperties(IPath bundlePath) {
		IPath bundleExtPath = bundlePath.append(_PORTAL_EXT_PROPERTIES);
		Properties pluginPackageProperties = new Properties();

		File bundleExtFile = bundleExtPath.toFile();

		try {
			if (FileUtil.exists(bundleExtFile)) {
				try (InputStream extInputStream = Files.newInputStream(bundleExtFile.toPath())) {
					pluginPackageProperties.load(extInputStream);
				}

				String driverName = (String)pluginPackageProperties.getProperty(_JDBC_DRIVER_CLASS_NAME);

				if (CoreUtil.isNullOrEmpty(driverName)) {
					IPath setupWizardPath = bundlePath.append(_PORTAL_SETUP_PROPERTIES);

					File setupWizardFile = setupWizardPath.toFile();

					if (FileUtil.exists(setupWizardFile)) {
						File file = setupWizardPath.toFile();

						try (InputStream setupInputStream = Files.newInputStream(file.toPath())) {
							pluginPackageProperties.load(setupInputStream);
						}
					}
				}
			}
			else {
				IPath setupWizardPath = bundlePath.append(_PORTAL_SETUP_PROPERTIES);

				File setupWizardFile = setupWizardPath.toFile();

				if (FileUtil.exists(setupWizardFile)) {
					File file = setupWizardPath.toFile();

					try (InputStream setupInputStream = Files.newInputStream(file.toPath())) {
						pluginPackageProperties.load(setupInputStream);
					}
				}
			}
		}
		catch (Exception extException) {
			LiferayServerCore.logError(Msgs.noDatabasePropertyFile, extException);
		}

		return pluginPackageProperties;
	}

	private LiferayDatabaseConnection _getLiferayDBConnection(
		String driverClass, String userName, String password, String connectionUrl, ILiferayRuntime liferayRuntime) {

		if (driverClass.startsWith("com.mysql.")) {
			if (CoreUtil.compareVersions(_getMysqlBundleVersion(liferayRuntime), new Version("8.0")) < 0) {
				if (driverClass.equals("com.mysql.jdbc.Driver")) {
					String providerId = "org.eclipse.datatools.enablement.mysql.connectionProfile";
					String connectionDesc = "Mysql Connection Profile";
					String driverTemplate = "org.eclipse.datatools.enablement.mysql.5_1.driverTemplate";

					return new MysqlLiferayDatabaseConnection(
						driverClass, providerId, connectionDesc, driverTemplate, userName, password, connectionUrl);
				}
			}
			else {
				if (driverClass.equals("com.mysql.cj.jdbc.Driver")) {
					String providerId = "org.eclipse.datatools.enablement.mysql.connectionProfile";
					String connectionDesc = "Mysql Connection Profile";
					String driverTemplate = "org.eclipse.datatools.enablement.mysql.5_1.driverTemplate";

					return new MysqlLiferayDatabaseConnection(
						driverClass, providerId, connectionDesc, driverTemplate, userName, password, connectionUrl);
				}
			}
		}
		else if (driverClass.equals("org.postgresql.Driver")) {
			String providerId = "org.eclipse.datatools.enablement.postgresql.connectionProfile";
			String connectionDesc = "Posgresql Connection Profile";
			String driverTemplate = "org.eclipse.datatools.enablement.postgresql.postgresqlDriverTemplate";

			return new PostgresqlLiferayDatabaseConnection(
				driverClass, providerId, connectionDesc, driverTemplate, userName, password, connectionUrl);
		}
		else if ((driverClass == null) || driverClass.equals("org.hsqldb.jdbcDriver")) {
			String providerId = "org.eclipse.datatools.enablement.hsqldb.connectionProfile";
			String connectionDesc = "Hsql Connection Profile";
			String driverTemplate = "org.eclipse.datatools.enablement.hsqldb.1_8.driver";

			return new HsqlLiferayDatabaseConnection(driverClass, providerId, connectionDesc, driverTemplate);
		}

		return null;
	}

	private URL[] _getLiferayRuntimeLibs(ILiferayRuntime liferayRuntime) throws Exception {
		IPath[] extraLibs = liferayRuntime.getUserLibs();
		List<URL> libUrlList = new ArrayList<>();

		if (ListUtil.isNotEmpty(extraLibs)) {
			for (IPath url : extraLibs) {
				URI uri = new File(
					url.toOSString()
				).toURI();

				libUrlList.add(uri.toURL());
			}
		}

		return libUrlList.toArray(new URL[0]);
	}

	private Version _getMysqlBundleVersion(ILiferayRuntime liferayRuntime) {
		IPath libGlobalDir = liferayRuntime.getAppServerLibGlobalDir();

		IPath mysqlJarPath = libGlobalDir.append("mysql.jar");

		if (!FileUtil.exists(mysqlJarPath)) {
			IPath appServerDir = liferayRuntime.getAppServerDir();

			mysqlJarPath = appServerDir.append("webapps/ROOT/WEB-INF/shielded-container-lib/mysql.jar");
		}

		File mysqlJar = mysqlJarPath.toFile();

		String[] version = FileUtil.readMainFestProsFromJar(mysqlJar, "Bundle-Version");

		if (Objects.isNull(version)) {
			return new Version("0.0.0");
		}

		return new Version(version[0]);
	}

	private static final String _JDBC_DRIVER_CLASS_NAME = "jdbc.default.driverClassName";

	private static final String _PORTAL_EXT_PROPERTIES = "portal-ext.properties";

	private static final String _PORTAL_SETUP_PROPERTIES = "portal-setup-wizard.properties";

	private static class Msgs extends NLS {

		public static String addDBConnnection;
		public static String addProfileError;
		public static String noDatabasePropertyFile;
		public static String noDBConnectDriver;

		static {
			initializeMessages(CreateDBConnectAction.class.getName(), Msgs.class);
		}

	}

	private class HsqlLiferayDatabaseConnection extends LiferayDatabaseConnection {

		public HsqlLiferayDatabaseConnection(
			String driverClass, String providerId, String connectinDesc, String driverTemplate) {

			super(
				driverClass, providerId, connectinDesc, driverTemplate, _DEFAULT_USER_NAME, _DEFAULT_PASSWORD,
				_DEFAULT_CONNECTION_URL);
		}

		@Override
		protected String getDatabaseName(String connectionUrl) {
			String retval = "lportal";

			if (!CoreUtil.isNullOrEmpty(connectionUrl)) {
				int databaseNameBegin = connectionUrl.lastIndexOf("/");

				if (databaseNameBegin > 0) {
					String databaseName = connectionUrl.substring(databaseNameBegin + 1);

					if (!CoreUtil.isNullOrEmpty(databaseName)) {
						retval = databaseName;
					}
				}
			}

			return retval;
		}

		private static final String _DEFAULT_CONNECTION_URL = "jdbc:hsqldb:lportal";

		private static final String _DEFAULT_PASSWORD = "";

		private static final String _DEFAULT_USER_NAME = "sa";

	}

	private abstract class LiferayDatabaseConnection {

		public LiferayDatabaseConnection(
			String driverClass, String providerId, String connectinDesc, String driverTemplate, String userName,
			String password, String connectionUrl) {

			_driverClass = driverClass;
			_providerId = providerId;
			_connectionDesc = connectinDesc;
			_driverTemplate = driverTemplate;
			_userName = userName;
			_password = password;
			_connectionUrl = connectionUrl;
		}

		public boolean addDatabaseConnectionProfile(String connectionName, String driverPath)
			throws ConnectionProfileException {

			String uniqueDriverInstanceName = _generateUniqueDriverDefinitionName(connectionName);

			DriverManager driverManagerInstance = DriverManager.getInstance();

			DriverInstance driverInstance = driverManagerInstance.createNewDriverInstance(
				_driverTemplate, uniqueDriverInstanceName, driverPath, _driverClass);

			if (driverInstance == null) {
				return false;
			}

			String vendor = driverInstance.getProperty(IJDBCDriverDefinitionConstants.DATABASE_VENDOR_PROP_ID);

			String uniqueConnectionProfileName = _generateUniqueConnectionProfileName(connectionName + " " + vendor);

			IPropertySet propertySet = driverInstance.getPropertySet();

			Properties connectionProfileProperties = propertySet.getBaseProperties();

			connectionProfileProperties.setProperty(
				ConnectionProfileConstants.PROP_DRIVER_DEFINITION_ID, driverInstance.getId());
			connectionProfileProperties.setProperty(
				IJDBCDriverDefinitionConstants.DATABASE_NAME_PROP_ID, getDatabaseName(_connectionUrl));
			connectionProfileProperties.setProperty(IJDBCDriverDefinitionConstants.USERNAME_PROP_ID, _userName);
			connectionProfileProperties.setProperty(IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID, _password);
			connectionProfileProperties.setProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID, _connectionUrl);

			ProfileManager profileManager = ProfileManager.getInstance();

			profileManager.createProfile(
				uniqueConnectionProfileName, _connectionDesc, _providerId, connectionProfileProperties, "", false);

			return true;
		}

		protected abstract String getDatabaseName(final String connectionUrl);

		private String _generateUniqueDriverDefinitionName(String driverDefinitionNameBase) {
			int index = 1;
			String testName = driverDefinitionNameBase;
			DriverManager driverManager = DriverManager.getInstance();

			while (driverManager.getDriverInstanceByName(testName) != null) {
				index++;

				testName = driverDefinitionNameBase + String.valueOf(index);
			}

			return testName;
		}

		private String _connectionDesc;
		private String _connectionUrl;
		private String _driverClass;
		private String _driverTemplate;
		private String _password;
		private String _providerId;
		private String _userName;

	}

	private class MysqlLiferayDatabaseConnection extends LiferayDatabaseConnection {

		public MysqlLiferayDatabaseConnection(
			String driverClass, String providerId, String connectinDesc, String driverTemplate, String userName,
			String password, String connectionUrl) {

			super(driverClass, providerId, connectinDesc, driverTemplate, userName, password, connectionUrl);
		}

		protected String getDatabaseName(String connectionUrl) {
			String retval = "lportal";

			if (!CoreUtil.isNullOrEmpty(connectionUrl)) {
				int databaseNameEnd = connectionUrl.indexOf("?");

				if (databaseNameEnd > 0) {
					String databaseNameTmp = connectionUrl.substring(0, databaseNameEnd);

					if (!CoreUtil.isNullOrEmpty(databaseNameTmp)) {
						int databaseNameBegin = databaseNameTmp.lastIndexOf("/");

						if (databaseNameBegin > 0) {
							String databaseName = connectionUrl.substring(databaseNameBegin + 1, databaseNameEnd);

							if (!CoreUtil.isNullOrEmpty(databaseName)) {
								retval = databaseName;
							}
						}
					}
				}
			}

			return retval;
		}

	}

	private class PostgresqlLiferayDatabaseConnection extends LiferayDatabaseConnection {

		public PostgresqlLiferayDatabaseConnection(
			String driverClass, String providerId, String connectinDesc, String driverTemplate, String userName,
			String password, String connectionUrl) {

			super(driverClass, providerId, connectinDesc, driverTemplate, userName, password, connectionUrl);
		}

		@Override
		protected String getDatabaseName(String connectionUrl) {
			String retval = "lportal";

			if (!CoreUtil.isNullOrEmpty(connectionUrl)) {
				int databaseNameBegin = connectionUrl.lastIndexOf("/");

				if (databaseNameBegin > 0) {
					String databaseName = connectionUrl.substring(databaseNameBegin + 1);

					if (!CoreUtil.isNullOrEmpty(databaseName)) {
						retval = databaseName;
					}
				}
			}

			return retval;
		}

	}

}