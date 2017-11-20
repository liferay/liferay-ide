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

package com.liferay.ide.kaleo.ui.diagram;

import com.liferay.ide.kaleo.core.model.Action;
import com.liferay.ide.kaleo.core.model.Task;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;

/**
 * @author Gregory Amerson
 */
public class TaskActionsDiagramNodeEditHandlerFactory extends ActionsDiagramNodeEditHandlerFactory {

	@Override
	protected ElementList<Action> getActions() {
		ElementList<Action> actions = null;

		Task task = getModelElement().nearest(Task.class);

		if (task != null) {
			actions = task.getTaskActions();
		}

		return actions;
	}

	protected Element getElement() {
		return getModelElement().nearest(Task.class);
	}

	@Override
	protected String getListPropertyName() {
		return Task.PROP_TASK_ACTIONS.name();
	}

}