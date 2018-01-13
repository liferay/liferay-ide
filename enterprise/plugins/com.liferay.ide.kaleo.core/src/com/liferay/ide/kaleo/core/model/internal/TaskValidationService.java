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

package com.liferay.ide.kaleo.core.model.internal;

import com.liferay.ide.kaleo.core.model.Assignable;
import com.liferay.ide.kaleo.core.op.NewNodeOp.TaskForOp;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Gregory Amerson
 */
public class TaskValidationService extends ValidationService {

	@Override
	public Status compute() {
		Assignable assignable = assignable();

		if ((assignable != null) && (assignable.nearest(TaskForOp.class) == null)) {
			Value<String> currentAssignments = assignable.getCurrentAssignments();

			if (currentAssignments.content(false) == null) {
				return Status.createErrorStatus("Task assignments have not been set.");
			}
		}

		return Status.createOkStatus();
	}

	@Override
	public void dispose() {
		Assignable assignable = assignable();

		if (assignable != null) {
			assignable.detach(_listener);
		}
	}

	protected Assignable assignable() {
		return context(Assignable.class);
	}

	@Override
	protected void initValidationService() {
		Assignable assignable = assignable();

		if (assignable != null) {
			_listener = new FilteredListener<PropertyContentEvent>() {

				@Override
				protected void handleTypedEvent(PropertyContentEvent event) {
					refresh();
				}

			};

			assignable.attach(_listener, "*");
		}
	}

	private Listener _listener;

}