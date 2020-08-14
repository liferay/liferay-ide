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

package com.liferay.ide.server.core.portal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.JavaUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.model.RuntimeDelegate;

import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Yuqiang Wang
 * @author Charles Wu
 */
public class PortalRuntime extends RuntimeDelegate implements ILiferayRuntime, PropertyChangeListener {

	public static final String ID = "com.liferay.ide.server.portal.runtime";

	public static final String PROP_VM_INSTALL_ID = "vm-install-id";

	public static final String PROP_VM_INSTALL_TYPE_ID = "vm-install-type-id";

	@Override
	public void dispose() {
		super.dispose();

		if (getRuntimeWorkingCopy() != null) {
			getRuntimeWorkingCopy().removePropertyChangeListener(this);
		}
	}

	public IPath getAppServerDeployDir() {
		PortalBundle portalBundle = getPortalBundle();

		return portalBundle.getAppServerDeployDir();
	}

	public IPath getAppServerDir() {
		PortalBundle portalBundle = getPortalBundle();

		return portalBundle.getAppServerDir();
	}

	public IPath getAppServerLibGlobalDir() {
		PortalBundle portalBundle = getPortalBundle();

		return portalBundle.getAppServerLibGlobalDir();
	}

	public IPath getAppServerPortalDir() {
		PortalBundle portalBundle = getPortalBundle();

		return portalBundle.getAppServerPortalDir();
	}

	public String getAppServerType() {
		PortalBundle portalBundle = getPortalBundle();

		return portalBundle.getType();
	}

	public String[] getHookSupportedProperties() {

		// TODO Auto-generated method stub

		return null;
	}

	public String getJavadocURL() {

		// TODO Auto-generated method stub

		return null;
	}

	public IPath getLiferayHome() {
		PortalBundle portalBundle = getPortalBundle();

		if (portalBundle != null) {
			return portalBundle.getLiferayHome();
		}

		return null;
	}

	public PortalBundle getPortalBundle() {
		if (_portalBundle == null) {
			_initPortalBundle();
		}

		return _portalBundle;
	}

	public String getPortalVersion() {
		PortalBundle tempPortalBundle = getPortalBundle();

		if (tempPortalBundle != null) {
			return tempPortalBundle.getVersion();
		}

		return null;
	}

	public Properties getPortletCategories() {

		// TODO Auto-generated method stub

		return null;
	}

	public Properties getPortletEntryCategories() {

		// TODO Auto-generated method stub

		return null;
	}

	public List<IRuntimeClasspathEntry> getRuntimeClasspathEntries() {
		List<IRuntimeClasspathEntry> entries = new ArrayList<>();

		IPath[] paths = getPortalBundle().getRuntimeClasspath();

		for (IPath path : paths) {
			if (FileUtil.exists(path)) {
				entries.add(JavaRuntime.newArchiveRuntimeClasspathEntry(path));
			}
		}

		return entries;
	}

	public IPath getRuntimeLocation() {
		return getRuntime().getLocation();
	}

	public IPath getSourceLocation() {

		// TODO Auto-generated method stub

		return null;
	}

	public IPath[] getUserLibs() {
		return _portalBundle.getUserLibs();
	}

	public IVMInstall getVMInstall() {
		if (getVMInstallTypeId() == null) {
			return JavaRuntime.getDefaultVMInstall();
		}

		try {
			IVMInstallType vmInstallType = JavaRuntime.getVMInstallType(getVMInstallTypeId());

			IVMInstall[] vmInstalls = vmInstallType.getVMInstalls();

			int size = vmInstalls.length;

			String id = getVMInstallId();

			for (int i = 0; i < size; i++) {
				if (id.equals(vmInstalls[i].getId())) {
					return vmInstalls[i];
				}
			}
		}
		catch (Exception e) {

			// ignore

		}

		return null;
	}

	public boolean isUsingDefaultJRE() {
		if (getVMInstallTypeId() == null) {
			return true;
		}

		return false;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (Objects.equals("location", evt.getPropertyName())) {
			_portalBundle = null;

			if (evt.getNewValue() != null) {
				_initPortalBundle();
			}
		}
	}

	@Override
	public void setDefaults(IProgressMonitor monitor) {
		IRuntimeType type = getRuntimeWorkingCopy().getRuntimeType();

		if (type != null) {
			getRuntimeWorkingCopy().setLocation(new Path(LiferayServerCore.getPreference("location." + type.getId())));
		}
	}

	public void setVMInstall(IVMInstall vmInstall) {
		if (vmInstall == null) {
			setVMInstall(null, null);
		}
		else {
			IVMInstallType vmInstallType = vmInstall.getVMInstallType();

			setVMInstall(vmInstallType.getId(), vmInstall.getId());
		}
	}

	@Override
	public IStatus validate() {
		IStatus status = super.validate();

		if (!status.isOK()) {
			return status;
		}

		if (_portalBundle == null) {
			return new Status(IStatus.ERROR, LiferayServerCore.PLUGIN_ID, 0, Msgs.errorPortalNotExisted, null);
		}

		String portalBundleVersion = _portalBundle.getVersion();

		if (!portalBundleVersion.startsWith("7")) {
			return new Status(IStatus.ERROR, LiferayServerCore.PLUGIN_ID, 0, Msgs.errorPortalVersion70, null);
		}

		if (getVMInstall() == null) {
			return new Status(IStatus.ERROR, LiferayServerCore.PLUGIN_ID, 0, Msgs.errorJRE, null);
		}

		if (portalBundleVersion.startsWith("7")) {
			Version jdkVersion = Version.parseVersion(JavaUtil.getJDKVersion(getVMInstall()));

			Version jdk8Version = Version.parseVersion("1.8");

			if (jdkVersion.compareTo(jdk8Version) < 0) {
				return new Status(IStatus.ERROR, LiferayServerCore.PLUGIN_ID, 0, Msgs.errorJRE80, null);
			}
		}

		File jdkInstallLocation = getVMInstall().getInstallLocation();

		if (jdkInstallLocation != null) {
			String rootPath = jdkInstallLocation.getAbsolutePath();

			StringBuilder javacPath = new StringBuilder(rootPath);

			javacPath.append(File.separator + "bin" + File.separator);

			if (CoreUtil.isWindows()) {
				javacPath.append("javac.exe");
			}
			else if (CoreUtil.isLinux() || CoreUtil.isMac()) {
				javacPath.append("javac");
			}

			if (FileUtil.notExists(new File(javacPath.toString()))) {
				return new Status(IStatus.WARNING, LiferayServerCore.PLUGIN_ID, 0, Msgs.warningjre, null);
			}
		}

		return Status.OK_STATUS;
	}

	protected String getVMInstallId() {
		return getAttribute(PROP_VM_INSTALL_ID, (String)null);
	}

	protected String getVMInstallTypeId() {
		return getAttribute(PROP_VM_INSTALL_TYPE_ID, (String)null);
	}

	@Override
	protected void initialize() {
		super.initialize();

		if (getRuntimeWorkingCopy() != null) {
			getRuntimeWorkingCopy().addPropertyChangeListener(this);
		}

		if (_portalBundle == null) {
			_initPortalBundle();
		}
	}

	protected void setVMInstall(String typeId, String id) {
		if (typeId == null) {
			setAttribute(PROP_VM_INSTALL_TYPE_ID, (String)null);
		}
		else {
			setAttribute(PROP_VM_INSTALL_TYPE_ID, typeId);
		}

		if (id == null) {
			setAttribute(PROP_VM_INSTALL_ID, (String)null);
		}
		else {
			setAttribute(PROP_VM_INSTALL_ID, id);
		}
	}

	private void _initPortalBundle() {
		if (getRuntime().getLocation() != null) {
			PortalBundleFactory[] factories = LiferayServerCore.getPortalBundleFactories();

			for (PortalBundleFactory factory : factories) {
				IPath path = factory.canCreateFromPath(getRuntime().getLocation());

				if (path != null) {
					_portalBundle = factory.create(path);

					return;
				}
			}
		}
	}

	private PortalBundle _portalBundle;

	private static class Msgs extends NLS {

		public static String errorJRE;
		public static String errorJRE80;
		public static String errorPortalNotExisted;
		public static String errorPortalVersion70;
		public static String warningjre;

		static {
			initializeMessages(PortalRuntime.class.getName(), Msgs.class);
		}

	}

}