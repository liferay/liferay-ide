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
public interface TimerAction extends Timer {

	public ElementType TYPE = new ElementType(TimerAction.class);

	public ElementList<Action> getTimerActions();

	public ElementList<Notification> getTimerNotifications();

	@Label(standard = "timer actions")
	@Type(base = Action.class)
	@XmlListBinding(
		mappings = @XmlListBinding.Mapping(element = "timer-action", type = Action.class), path = "timer-actions"
	)
	public ListProperty PROP_TIMER_ACTIONS = new ListProperty(TYPE, "TimerActions");

	@Label(standard = "timer notifications")
	@Type(base = Notification.class)
	@XmlListBinding(
		mappings = @XmlListBinding.Mapping(element = "timer-notification", type = Notification.class),
		path = "timer-actions"
	)
	public ListProperty PROP_TIMER_NOTIFICATIONS = new ListProperty(TYPE, "TimerNotifications");

}