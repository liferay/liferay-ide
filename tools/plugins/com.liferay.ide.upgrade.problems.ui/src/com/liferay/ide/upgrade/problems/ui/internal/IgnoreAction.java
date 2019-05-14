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

package com.liferay.ide.upgrade.problems.ui.internal;

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.plan.ui.util.UIUtil;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.actions.SelectionProviderAction;

/**
 * @author Seiphon Wang
 * @author Terry Jia
 */
public class IgnoreAction extends SelectionProviderAction implements ProblemUISupport {

	public IgnoreAction(ISelectionProvider provider) {
		super(provider, "Ignore");
	}

	@Override
	public void run() {
		List<UpgradeProblem> upgradeProblems = getUpgradeProblems(getSelection());

		Stream<UpgradeProblem> stream = upgradeProblems.stream();

		stream.forEach(this::ignore);

		Viewer viewer = (Viewer)getSelectionProvider();

		UIUtil.async(() -> viewer.refresh());
	}

}