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

package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.sdk.core.ISDKListener;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.DefaultValueService;

/**
 * @author Gregory Amerson
 */
public class PluginsSDKNameDefaultValueService extends DefaultValueService implements ISDKListener {

	@Override
	public void dispose() {
		SDKManager sdkManager = SDKManager.getInstance();

		sdkManager.removeSDKListener(this);

		super.dispose();
	}

	public void sdksAdded(SDK[] sdk) {
		_refreshSafe();
	}

	public void sdksChanged(SDK[] sdk) {
		_refreshSafe();
	}

	public void sdksRemoved(SDK[] sdk) {
		_refreshSafe();
	}

	@Override
	protected String compute() {
		String value = null;

		SDKManager sdkManager = SDKManager.getInstance();

		SDK defaultSDK = sdkManager.getDefaultSDK();

		if (defaultSDK != null) {
			value = defaultSDK.getName();
		}
		else {
			value = NONE;
		}

		return value;
	}

	@Override
	protected void initDefaultValueService() {
		super.initDefaultValueService();

		SDKManager sdkManager = SDKManager.getInstance();

		sdkManager.addSDKListener(this);
	}

	protected static final String NONE = "<None>";

	private void _refreshSafe() {
		new Job("refreshing") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				refresh();

				return Status.OK_STATUS;
			}

		}.schedule();
	}

}