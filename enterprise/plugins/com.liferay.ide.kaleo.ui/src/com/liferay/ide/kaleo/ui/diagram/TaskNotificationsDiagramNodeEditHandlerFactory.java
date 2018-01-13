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

import com.liferay.ide.kaleo.core.model.ActionNotification;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.model.TaskActionNotification;

import java.util.List;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.util.ListFactory;

/**
 * @author Gregory Amerson
 */
public class TaskNotificationsDiagramNodeEditHandlerFactory extends NotificationsDiagramNodeEditHandlerFactory {

	protected Element getElement() {
		return getModelElement().nearest(Task.class);
	}

	@Override
	protected String getListPropertyName() {
		return Task.PROP_TASK_NOTIFICATIONS.name();
	}

	@Override
	protected List<ActionNotification> getNotifications() {
		ListFactory<ActionNotification> factory = ListFactory.start();

		Task task = getModelElement().nearest(Task.class);

		if (task != null) {
			ElementList<TaskActionNotification> taskNotifiations = task.getTaskNotifications();

			for (TaskActionNotification notification : taskNotifiations) {
				factory.add(notification);
			}
		}

		return factory.result();
	}

}