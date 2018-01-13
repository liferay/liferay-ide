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

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Gregory Amerson
 */
public interface ActionTimer extends WorkflowNode {

	public ElementType TYPE = new ElementType(ActionTimer.class);

	public ElementList<Action> getActions();

	public ElementList<ActionNotification> getNotifications();

	public ElementList<Timer> getTimers();

	@Label(standard = "action")
	@Type(base = Action.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "action", type = Action.class), path = "actions")
	public ListProperty PROP_ACTIONS = new ListProperty(TYPE, "Actions");

	@Label(standard = "notification")
	@Type(base = ActionNotification.class)
	@XmlListBinding(
		mappings = @XmlListBinding.Mapping(element = "notification", type = ActionNotification.class), path = "actions"
	)
	public ListProperty PROP_NOTIFICATIONS = new ListProperty(TYPE, "Notifications");

	@Label(standard = "timer")
	@Type(base = Timer.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "timer", type = Timer.class), path = "timers")
	public ListProperty PROP_TIMERS = new ListProperty(TYPE, "Timers");

}