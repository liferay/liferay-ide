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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.DerivedValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;

/**
 * @author Andy Wu
 */
public class ImportModuleProjectBuildTypeDerivedValueService
	extends DerivedValueService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		ImportLiferayModuleProjectOp op = _op();

		if (op != null) {
			SapphireUtil.detachListener(op.property(ImportLiferayModuleProjectOp.PROP_LOCATION), _listener);
		}

		super.dispose();
	}

	@Override
	protected String compute() {
		String retVal = "";

		ImportLiferayModuleProjectOp op = _op();

		if (op.getLocation() == null) {
			return retVal;
		}

		Path path = get(op.getLocation());

		if ((path != null) && !path.isEmpty()) {
			String location = path.toOSString();

			IStatus status = ImportLiferayModuleProjectOpMethods.getBuildType(location);

			if (status.isOK()) {
				retVal = status.getMessage();
			}
			else if (status.getSeverity() == IStatus.WARNING) {
				retVal = "gradle";
			}
			else {
				retVal = "";
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

		ImportLiferayModuleProjectOp op = _op();

		SapphireUtil.attachListener(op.property(ImportLiferayModuleProjectOp.PROP_LOCATION), _listener);
	}

	private ImportLiferayModuleProjectOp _op() {
		return context(ImportLiferayModuleProjectOp.class);
	}

	private FilteredListener<PropertyContentEvent> _listener;

}