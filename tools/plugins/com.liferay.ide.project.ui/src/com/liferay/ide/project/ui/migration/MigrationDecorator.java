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

package com.liferay.ide.project.ui.migration;

import com.liferay.blade.api.Problem;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.core.upgrade.FileProblems;
import com.liferay.ide.project.core.upgrade.ProblemsContainer;
import com.liferay.ide.project.core.upgrade.UpgradeProblems;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class MigrationDecorator extends BaseLabelProvider implements ILightweightLabelDecorator {

	@Override
	public void decorate(Object element, IDecoration decoration) {
		final List<Problem> problems = new ArrayList<>();

		final List<Problem> resolvedProblems = new ArrayList<>();

		final List<Problem> ignoreProblems = new ArrayList<>();

		if (element instanceof FileProblems) {
			final FileProblems fileProblems = (FileProblems)element;

			problems.addAll(fileProblems.getProblems());

			resolvedProblems.addAll(fileProblems.getProblems(Problem.STATUS_RESOLVED));

			ignoreProblems.addAll(fileProblems.getProblems(Problem.STATUS_IGNORE));
		}
		else if (element instanceof UpgradeProblems) {
			final UpgradeProblems upgradeProblems = (UpgradeProblems)element;

			for (FileProblems fileProblems : upgradeProblems.getProblems()) {
				problems.addAll(fileProblems.getProblems());

				resolvedProblems.addAll(fileProblems.getProblems(Problem.STATUS_RESOLVED));

				ignoreProblems.addAll(fileProblems.getProblems(Problem.STATUS_IGNORE));
			}
		}
		else if (element instanceof ProblemsContainer) {
			ProblemsContainer problemsContainer = (ProblemsContainer)element;

			for (UpgradeProblems upgradeProblems : problemsContainer.getProblemsArray()) {
				for (FileProblems fileProblems : upgradeProblems.getProblems()) {
					problems.addAll(fileProblems.getProblems());

					resolvedProblems.addAll(fileProblems.getProblems(Problem.STATUS_RESOLVED));

					ignoreProblems.addAll(fileProblems.getProblems(Problem.STATUS_IGNORE));
				}
			}
		}

		if (ListUtil.isNotEmpty(problems)) {
			StringBuilder sb = new StringBuilder();

			sb.append("[");

			sb.append(problems.size()).append(" total");

			if (ListUtil.isNotEmpty(resolvedProblems)) {
				sb.append(", ");
				sb.append(resolvedProblems.size());
				sb.append(" resolved");
			}

			if (ListUtil.isNotEmpty(ignoreProblems)) {
				sb.append(", ");
				sb.append(ignoreProblems.size());
				sb.append(" ignored");
			}

			sb.append("]");

			decoration.addSuffix(sb.toString());
		}
	}

}