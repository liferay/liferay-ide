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
import com.liferay.ide.kaleo.core.model.ActionNotification;
import com.liferay.ide.kaleo.core.model.ActionTimer;
import com.liferay.ide.kaleo.core.model.Notification;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.ui.action.ActionsListAddActionHandler;
import com.liferay.ide.kaleo.ui.action.NotificationsListAddActionHandler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFactory;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;

/**
 * @author Gregory Amerson
 */
public class WorkflowNodeAddHandlerFactory extends SapphireActionHandlerFactory {

	@Override
	public List<SapphireActionHandler> create() {
		List<SapphireActionHandler> handlers = new ArrayList<>();

		if (getModelElement() instanceof Task) {
			Task task = getModelElement().nearest(Task.class);

			handlers.add(
				new SapphireActionHandler() {

					@Override
					public void init(SapphireAction action, ActionHandlerDef def) {
						super.init(action, def);

						addImage(Action.TYPE.image());
						setLabel("Task Action");
					}

					@Override
					protected Object run(Presentation context) {
						Action newAction = task.getTaskActions().insert();

						ActionsListAddActionHandler.addActionDefaults(newAction);

						return newAction;
					}

				});

			handlers.add(
				new SapphireActionHandler() {

					@Override
					public void init(SapphireAction action, ActionHandlerDef def) {
						super.init(action, def);

						addImage(ActionNotification.TYPE.image());
						setLabel("Task Notification");
					}

					@Override
					protected Object run(Presentation context) {
						ActionNotification newTaskNotificaction = task.getTaskNotifications().insert();

						NotificationsListAddActionHandler.addNotificationDefaults(newTaskNotificaction);

						return newTaskNotificaction;
					}

				});
		}
		else {
			ActionTimer actionTimer = getModelElement().nearest(ActionTimer.class);

			handlers.add(
				new SapphireActionHandler() {

					@Override
					public void init(SapphireAction action, ActionHandlerDef def) {
						super.init(action, def);

						addImage(Action.TYPE.image());
						setLabel("Action");
					}

					@Override
					protected Object run(Presentation context) {
						Action newAction = actionTimer.getActions().insert();

						ActionsListAddActionHandler.addActionDefaults(newAction);

						return newAction;
					}

				});

			handlers.add(
				new SapphireActionHandler() {

					@Override
					public void init(SapphireAction action, ActionHandlerDef def) {
						super.init(action, def);

						addImage(Notification.TYPE.image());
						setLabel("Notification");
					}

					@Override
					protected Object run(Presentation context) {
						ActionNotification newNotificaction = actionTimer.getNotifications().insert();

						NotificationsListAddActionHandler.addNotificationDefaults(newNotificaction);

						return newNotificaction;
					}

				});
		}

		return handlers;
	}

}