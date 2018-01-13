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

import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.model.ActionNotification;
import com.liferay.ide.kaleo.core.model.ActionTimer;
import com.liferay.ide.kaleo.core.model.Executable;
import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.NotificationTransport;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.model.TaskActionNotification;
import com.liferay.ide.kaleo.core.model.TemplateLanguageType;
import com.liferay.ide.kaleo.core.util.KaleoModelUtil;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.ui.Presentation;

/**
 * @author Gregory Amerson
 */
public class NotificationsListAddActionHandler extends DefaultListAddActionHandler {

	public static void addNotificationDefaults(ActionNotification newNotification) {
		String defaultTemplateLanguage = KaleoModelUtil.getDefaultValue(
			newNotification, KaleoCore.DEFAULT_TEMPLATE_LANGUAGE_KEY, TemplateLanguageType.FREEMARKER);

		Node[] nodes = new Node[0];

		if (newNotification.nearest(Task.class) != null) {
			Task task = newNotification.nearest(Task.class);

			ElementList<TaskActionNotification> taskNotifications = task.getTaskNotifications();

			nodes = taskNotifications.toArray(new Node[0]);
		}
		else {
			ActionTimer actionTimer = newNotification.nearest(ActionTimer.class);

			ElementList<ActionNotification> actionNotifications = actionTimer.getNotifications();

			nodes = actionNotifications.toArray(new Node[0]);
		}

		String newName = getDefaultName("newNotification1", newNotification, nodes);

		newNotification.setName(newName);

		newNotification.setTemplateLanguage(defaultTemplateLanguage);
		newNotification.setExecutionType(Executable.DEFAULT_EXECUTION_TYPE);

		if (newNotification.nearest(Task.class) != null) {
			newNotification.setTemplate("/* specify task notification template */");
		}
		else {
			newNotification.setTemplate("/* specify notification template */");
		}

		ElementList<NotificationTransport> notificationTransports = newNotification.getNotificationTransports();

		NotificationTransport insertTransport = notificationTransports.insert();

		insertTransport.setNotificationTransport("email");
	}

	public NotificationsListAddActionHandler() {
		super(ActionNotification.TYPE, ActionTimer.PROP_NOTIFICATIONS);
	}

	public NotificationsListAddActionHandler(ElementType type, ListProperty listProperty) {
		super(type, listProperty);
	}

	@Override
	protected Object run(Presentation context) {
		Element newElement = (Element)super.run(context);

		ActionNotification newNotification = newElement.nearest(ActionNotification.class);

		addNotificationDefaults(newNotification);

		return newNotification;
	}

}