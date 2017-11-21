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

package com.liferay.ide.kaleo.core.op;

import com.liferay.ide.kaleo.core.model.ActionNotification;
import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.op.internal.NewNodeNameValidationService;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Length;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Gregory Amerson
 */
public interface NewTaskNode extends Task {

	public ElementType TYPE = new ElementType(NewTaskNode.class);

	public ElementList<INewTaskNotification> getNewTaskNotifications();

	@DefaultValue(text = "New Task")
	@Service(impl = NewNodeNameValidationService.class)
	public ValueProperty PROP_NAME = new ValueProperty(TYPE, Node.PROP_NAME);

	@Label(standard = "task notifications")
	@Type(base = INewTaskNotification.class)
	@XmlListBinding(
		mappings = {@XmlListBinding.Mapping(element = "notification", type = INewTaskNotification.class)},
		path = "actions"
	)
	public ListProperty PROP_NEW_TASK_NOTIFICATIONS = new ListProperty(TYPE, "NewTaskNotifications");

	@Length(min = 0)
	public ListProperty PROP_TRANSITIONS = new ListProperty(TYPE, Task.PROP_TRANSITIONS);

	public interface INewTaskNotification extends ActionNotification {

		public ElementType TYPE = new ElementType(INewTaskNotification.class);

		@Length(min = 0)
		public ListProperty PROP_NOTIFICATION_TRANSPORTS = new ListProperty(
			TYPE, ActionNotification.PROP_NOTIFICATION_TRANSPORTS);

		@DefaultValue(text = "")
		public ValueProperty PROP_TEMPLATE = new ValueProperty(TYPE, "Template");

	}

}