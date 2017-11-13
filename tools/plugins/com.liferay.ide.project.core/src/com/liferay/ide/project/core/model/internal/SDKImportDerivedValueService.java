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

import com.liferay.ide.project.core.model.ParentSDKProjectImportOp;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.sapphire.DerivedValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Simon Jiang
 */
public class SDKImportDerivedValueService extends DerivedValueService {

	@Override
	public void dispose() {
		ParentSDKProjectImportOp op = _op();

		if (op != null) {
			op.property(ParentSDKProjectImportOp.PROP_SDK_LOCATION).detach(_listener);
		}

		super.dispose();
	}

	@Override
	protected String compute() {
		String retVal = null;

		ParentSDKProjectImportOp op = _op();

		Value<Path> path = op.getSdkLocation();

		if ((path != null) && (path.content() != null) && !path.content().isEmpty()) {
			Path sdkPath = path.content();

			SDK sdk = SDKUtil.createSDKFromLocation(PathBridge.create(sdkPath));

			if (sdk != null) {
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

		ParentSDKProjectImportOp op = _op();

		op.property(ParentSDKProjectImportOp.PROP_SDK_LOCATION).attach(_listener);
	}

	private ParentSDKProjectImportOp _op() {
		return context(ParentSDKProjectImportOp.class);
	}

	private FilteredListener<PropertyContentEvent> _listener;

}