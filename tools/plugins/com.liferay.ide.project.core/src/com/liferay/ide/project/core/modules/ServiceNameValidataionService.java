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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
public class ServiceNameValidataionService extends ValidationService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		NewLiferayModuleProjectOp op = _op();

		if (_listener != null) {
			SapphireUtil.detachListener(op.getProjectTemplateName(), _listener);

			_listener = null;
		}

		super.dispose();
	}

	@Override
	protected Status compute() {
		Status retVal = Status.createOkStatus();

		NewLiferayModuleProjectOp op = _op();

		String projectTemplate = get(op.getProjectTemplateName());

		if (projectTemplate.equals("service") || projectTemplate.equals("service-wrapper")) {
			String serviceName = get(op.getServiceName());

			if (CoreUtil.isNullOrEmpty(serviceName)) {
				retVal = Status.createErrorStatus("The service name must be specified.");
			}
		}

		return retVal;
	}

	@Override
	protected void initValidationService() {
		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		NewLiferayModuleProjectOp op = _op();

		SapphireUtil.attachListener(op.getProjectTemplateName(), _listener);
	}

	private NewLiferayModuleProjectOp _op() {
		return context(NewLiferayModuleProjectOp.class);
	}

	private Listener _listener;

}