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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.actions.SelectionProviderAction;

/**
 * @author Seiphon Wang
 * @author Terry Jia
 */
public class MarkDoneAction extends SelectionProviderAction implements UpgradeProblemUISupport {

	public MarkDoneAction(ISelectionProvider provider) {
		super(provider, "Mark done");
	}

	@Override
	public void run() {
		List<UpgradeProblem> upgradeProblems = getUpgradeProblems(getSelection());

		Stream<UpgradeProblem> stream = upgradeProblems.stream();

		stream.forEach(this::_resolve);

		Viewer viewer = (Viewer)getSelectionProvider();

		UIUtil.async(() -> viewer.refresh());
	}

	private void _resolve(UpgradeProblem upgradeProblem) {
		upgradeProblem.setStatus(UpgradeProblem.STATUS_RESOLVED);

		IMarker marker = findMarker(upgradeProblem);

		if (marker != null) {
			try {
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
				marker.setAttribute("upgradeProblem.resolved", Boolean.TRUE);
			}
			catch (CoreException ce) {
			}
		}
	}

}