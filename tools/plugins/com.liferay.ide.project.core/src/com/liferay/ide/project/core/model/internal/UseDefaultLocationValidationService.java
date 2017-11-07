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

import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Kuo Zhang
 */
public class UseDefaultLocationValidationService extends ValidationService {

	@Override
	public void dispose() {
		NewLiferayPluginProjectOp op = _op();

		if ((op != null) && !op.disposed()) {
			op.getProjectProvider().detach(_listener);
			op.getProjectName().detach(_listener);
		}
	}

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		NewLiferayPluginProjectOp op = _op();

		NewLiferayProjectProvider<NewLiferayPluginProjectOp> provider = op.getProjectProvider().content();

		if ((op.getProjectName().content() != null) && "ant".equals(provider.getShortName()) &&
			!op.getUseDefaultLocation().content() && !NewLiferayPluginProjectOpMethods.canUseCustomLocation(op)) {

			retval = Status.createErrorStatus("The specified SDK version is not allowed to use custom location.");
		}

		return retval;
	}

	@Override
	protected void initValidationService() {
		super.initValidationService();

		_listener = new FilteredListener<Event>() {

			@Override
			protected void handleTypedEvent(Event event) {
				refresh();
			}

		};

		final NewLiferayPluginProjectOp op = _op();

		op.getProjectProvider().attach(_listener);
		op.getProjectName().attach(_listener);
	}

	private NewLiferayPluginProjectOp _op() {
		return context(NewLiferayPluginProjectOp.class);
	}

	private Listener _listener = null;

}