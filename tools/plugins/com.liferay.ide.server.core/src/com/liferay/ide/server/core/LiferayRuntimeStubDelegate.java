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

package com.liferay.ide.server.core;

import com.liferay.ide.core.util.StringPool;

import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.RuntimeDelegate;

/**
 * @author Gregory Amerson
 */
public class LiferayRuntimeStubDelegate extends RuntimeDelegate implements ILiferayRuntime {

	public LiferayRuntimeStubDelegate() {
	}

	public IPath getAppServerDeployDir() {
		return getLiferayRuntime().getAppServerDeployDir();
	}

	public IPath getAppServerDir() {
		return getLiferayRuntime().getAppServerDir();
	}

	public IPath getAppServerLibGlobalDir() {
		return getLiferayRuntime().getAppServerLibGlobalDir();
	}

	public IPath getAppServerPortalDir() {
		return getLiferayRuntime().getAppServerPortalDir();
	}

	public String getAppServerType() {
		return getLiferayRuntime().getAppServerType();
	}

	public String[] getHookSupportedProperties() {
		return getLiferayRuntime().getHookSupportedProperties();
	}

	public String getJavadocURL() {
		return getLiferayRuntime().getJavadocURL();
	}

	public IPath getLiferayHome() {
		return getLiferayRuntime().getLiferayHome();
	}

	public ILiferayRuntime getLiferayRuntime() {
		return (ILiferayRuntime)getTempRuntime().loadAdapter(ILiferayRuntime.class, new NullProgressMonitor());
	}

	public String getName() {
		return getRuntime().getName();
	}

	public String getPortalVersion() {
		return getLiferayRuntime().getPortalVersion();
	}

	public Properties getPortletCategories() {
		return getLiferayRuntime().getPortletCategories();
	}

	public Properties getPortletEntryCategories() {
		return getLiferayRuntime().getPortletEntryCategories();
	}

	public IPath getRuntimeLocation() {
		return getRuntime().getLocation();
	}

	public String getRuntimeStubTypeId() {
		return getAttribute(PROP_STUB_TYPE_ID, StringPool.EMPTY);
	}

	public IPath getSourceLocation() {
		return getLiferayRuntime().getSourceLocation();
	}

	public IPath[] getUserLibs() {
		return getLiferayRuntime().getUserLibs();
	}

	public IVMInstall getVMInstall() {
		return JavaRuntime.getDefaultVMInstall();
	}

	public boolean isUsingDefaultJRE() {
		return true;
	}

	public void setRuntimeStubTypeId(String typeId) {
		setAttribute(PROP_STUB_TYPE_ID, typeId);
		tempRuntime = null;
	}

	@Override
	public IStatus validate() {
		IStatus status = super.validate();

		if (!status.isOK()) {
			return status;
		}

		IRuntimeWorkingCopy tempRuntimeWorkingCopy = getTempRuntime();

		RuntimeDelegate runtimeDelegate = (RuntimeDelegate)tempRuntimeWorkingCopy.loadAdapter(
			RuntimeDelegate.class, new NullProgressMonitor());

		return runtimeDelegate.validate();
	}

	protected IRuntimeWorkingCopy getTempRuntime() {
		IRuntime runtime = getRuntime();

		if ((tempRuntime == null) && (runtime.getLocation() != null)) {
			IRuntimeType runtimeType = ServerCore.findRuntimeType(getRuntimeStubTypeId());

			try {
				tempRuntime = runtimeType.createRuntime(getRuntimeStubTypeId() + "-stub", new NullProgressMonitor());

				tempRuntime.setLocation(runtime.getLocation());
			}
			catch (CoreException ce) {
				LiferayServerCore.logError("Error creating runtime", ce);
			}
		}

		IPath tempRuntimeLocation = tempRuntime.getLocation();

		if ((tempRuntime.getLocation() == null) || !tempRuntimeLocation.equals(runtime.getLocation())) {
			tempRuntime.setLocation(runtime.getLocation());
		}

		return tempRuntime;
	}

	protected static final String PROP_STUB_TYPE_ID = "stub-type-id";

	protected IRuntimeWorkingCopy tempRuntime = null;

}