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

package com.liferay.ide.kaleo.core.model;

import com.liferay.ide.kaleo.core.model.internal.TaskValidationService;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Gregory Amerson
 */
@Image(path = "images/task_16x16.png")
public interface Task extends WorkflowNode, Assignable, MustTransition {

	public ElementType TYPE = new ElementType(Task.class);

	public ElementList<Action> getTaskActions();

	public ElementList<TaskActionNotification> getTaskNotifications();

	public ElementList<TaskTimer> getTaskTimers();

	@Service(impl = TaskValidationService.class)
	public ValueProperty PROP_NAME = new ValueProperty(TYPE, Node.PROP_NAME);

	@Label(standard = "task actions")
	@Type(base = Action.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "action", type = Action.class), path = "actions")
	public ListProperty PROP_TASK_ACTIONS = new ListProperty(TYPE, "TaskActions");

	@Label(standard = "task notifications")
	@Type(base = TaskActionNotification.class)
	@XmlListBinding(
		mappings = @XmlListBinding.Mapping(element = "notification", type = TaskActionNotification.class),
		path = "actions"
	)
	public ListProperty PROP_TASK_NOTIFICATIONS = new ListProperty(TYPE, "TaskNotifications");

	@Label(standard = "task timers")
	@Type(base = TaskTimer.class)
	@XmlListBinding(
		mappings = @XmlListBinding.Mapping(element = "task-timer", type = TaskTimer.class), path = "task-timers"
	)
	public ListProperty PROP_TASK_TIMERS = new ListProperty(TYPE, "TaskTimers");

}