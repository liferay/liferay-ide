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
import com.liferay.ide.kaleo.core.model.Action;
import com.liferay.ide.kaleo.core.model.ActionTimer;
import com.liferay.ide.kaleo.core.model.Executable;
import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.ScriptLanguageType;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.util.KaleoModelUtil;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;

/**
 * @author Gregory Amerson
 */
public class ActionsListAddActionHandler extends DefaultListAddActionHandler {

	public static void addActionDefaults(Action newAction) {
		Node[] actions = new Node[0];

		if (newAction.nearest(Task.class) != null) {
			Task task = newAction.nearest(Task.class);

			actions = task.getTaskActions().toArray(new Node[0]);
		}
		else {
			ActionTimer actionTimer = newAction.nearest(ActionTimer.class);

			actions = actionTimer.getActions().toArray(new Node[0]);
		}

		String newName = getDefaultName("newAction1", newAction, actions);
		String defaultScriptLanguage = KaleoModelUtil.getDefaultValue(
			newAction, KaleoCore.DEFAULT_SCRIPT_LANGUAGE_KEY, ScriptLanguageType.GROOVY);

		newAction.setName(newName);
		newAction.setScriptLanguage(defaultScriptLanguage);
		newAction.setExecutionType(Executable.DEFAULT_EXECUTION_TYPE);

		if (newAction.nearest(Task.class) != null) {
			newAction.setScript("/* specify task action script */");
		}
		else {
			newAction.setScript("/* specify action script */");
		}
	}

	public ActionsListAddActionHandler() {
		super(Action.TYPE, ActionTimer.PROP_ACTIONS);
	}

	public ActionsListAddActionHandler(ElementType type, ListProperty listProperty) {
		super(type, listProperty);
	}

	@Override
	public void init(SapphireAction action, ActionHandlerDef def) {
		super.init(action, def);
	}

	@Override
	protected Object run(Presentation context) {
		Element newElement = (Element)super.run(context);

		Action newAction = newElement.nearest(Action.class);

		addActionDefaults(newAction);

		return newAction;
	}

}