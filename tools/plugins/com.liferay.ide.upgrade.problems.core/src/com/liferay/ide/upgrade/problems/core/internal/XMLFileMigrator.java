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

package com.liferay.ide.upgrade.problems.core.internal;

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.XMLFile;

import java.io.File;
import java.io.IOException;

import java.util.Collection;

import org.eclipse.core.runtime.Path;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public abstract class XMLFileMigrator extends AbstractFileMigrator<XMLFile> {

	public XMLFileMigrator() {
		super(XMLFile.class);
	}

	@Override
	public int reportProblems(File file, Collection<UpgradeProblem> upgradeProblems) {
		Path path = new Path(file.getAbsolutePath());

		XMLFile xmlFile = createFileService(type, file, path.getFileExtension());

		xmlFile.setFile(file);

		return upgradeProblems.stream(
		).map(
			problem -> {
				try {
					xmlFile.appendComment(problem.getLineNumber(), problem.getTicket());

					return 1;
				}
				catch (IOException e) {
					e.printStackTrace(System.err);
				}

				return 0;
			}
		).reduce(
			0, Integer::sum
		);
	}

}