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

package com.liferay.ide.idea.ui.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;

import java.util.stream.Stream;

/**
 * @author Andy Wu
 */
public class LiferayActionGroup extends DefaultActionGroup {

	public void update(AnActionEvent e) {
		Presentation presentation = e.getPresentation();

		AnAction[] actions = getChildren(e);

		Stream<AnAction> stream = Stream.of(actions);

		long count = stream.filter(
			action -> action instanceof AbstractLiferayGradleTaskAction
		).map(
			action -> (AbstractLiferayGradleTaskAction)action
		).filter(
			action -> action.isEnableAndVisible(e)
		).count();

		presentation.setEnabledAndVisible(count > 0);
	}

}