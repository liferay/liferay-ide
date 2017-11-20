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

package com.liferay.ide.kaleo.ui.editor;

import com.liferay.ide.kaleo.core.model.Scriptable;
import com.liferay.ide.kaleo.core.model.Task;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireCondition;

/**
 * @author Gregory Amerson
 */
public class TaskScriptedAssignmentCondition extends SapphireCondition {

	@Override
	protected boolean evaluate() {
		if (_task() != null) {
			ElementHandle<Scriptable> scriptable = _task().getScriptedAssignment();

			if (scriptable.content(false) != null) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void initCondition(ISapphirePart part, String parameter) {
		super.initCondition(part, parameter);

		Task task = _task();

		Listener assignmentTypeListener = new FilteredListener<PropertyContentEvent>() {

			@Override
			public void handleTypedEvent(PropertyContentEvent event) {
				updateConditionState();
			}

		};

		if (task != null) {
			task.attach(assignmentTypeListener, Task.PROP_SCRIPTED_ASSIGNMENT.name());
		}

		updateConditionState();
	}

	private Task _task() {
		Task retval = null;

		Element modelElement = getPart().getLocalModelElement();

		retval = modelElement.nearest(Task.class);

		return retval;
	}

}