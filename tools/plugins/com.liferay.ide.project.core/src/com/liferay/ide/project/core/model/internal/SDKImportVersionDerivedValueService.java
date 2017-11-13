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

import com.liferay.ide.project.core.model.SDKProjectsImportOp;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.DerivedValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Simon Jiang
 */
public class SDKImportVersionDerivedValueService extends DerivedValueService {

	@Override
	public void dispose() {
		SDKProjectsImportOp op = _op();

		if (op != null) {
			op.property(SDKProjectsImportOp.PROP_SDK_LOCATION).detach(_listener);
		}

		super.dispose();
	}

	@Override
	protected String compute() {
		String retVal = null;

		SDKProjectsImportOp op = _op();

		Value<Path> sdkLocation = op.getSdkLocation();

		if ((sdkLocation != null) && (sdkLocation.content() != null) && !sdkLocation.content().isEmpty()) {
			Path sdkPath = sdkLocation.content();

			IStatus status = ProjectImportUtil.validateSDKPath(sdkLocation.content().toPortableString());

			if (status.isOK()) {
				SDK sdk = SDKUtil.createSDKFromLocation(PathBridge.create(sdkPath));

				retVal = sdk.getVersion();
			}
		}

		return retVal;
	}

	@Override
	protected void initDerivedValueService() {
		super.initDerivedValueService();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		SDKProjectsImportOp op = _op();

		op.property(SDKProjectsImportOp.PROP_SDK_LOCATION).attach(_listener);
	}

	private SDKProjectsImportOp _op() {
		return context(SDKProjectsImportOp.class);
	}

	private FilteredListener<PropertyContentEvent> _listener;

}