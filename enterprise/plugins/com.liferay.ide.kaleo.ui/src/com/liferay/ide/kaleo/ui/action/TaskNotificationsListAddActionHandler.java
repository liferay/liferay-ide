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

package com.liferay.ide.kaleo.ui.action;

import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.model.TaskActionNotification;

/**
 * @author Gregory Amerson
 */
public class TaskNotificationsListAddActionHandler extends NotificationsListAddActionHandler {

	public TaskNotificationsListAddActionHandler() {
		super(TaskActionNotification.TYPE, Task.PROP_TASK_NOTIFICATIONS);
	}

}