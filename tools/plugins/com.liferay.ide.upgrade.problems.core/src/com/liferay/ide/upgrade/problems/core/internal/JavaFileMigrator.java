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
import com.liferay.ide.upgrade.problems.core.JavaFile;

import java.io.File;
import java.io.IOException;

import java.util.Collection;

import org.eclipse.core.runtime.Path;

/**
 * @author Gregory Amerson
 */
public abstract class JavaFileMigrator extends AbstractFileMigrator<JavaFile> {

	public JavaFileMigrator() {
		super(JavaFile.class);
	}

	@Override
	public int reportProblems(File file, Collection<UpgradeProblem> upgradeProblems) {
		Path path = new Path(file.getAbsolutePath());

		JavaFile javaFile = createFileService(type, file, path.getFileExtension());

		javaFile.setFile(file);

		return upgradeProblems.stream(
		).map(
			problem -> {
				try {
					javaFile.appendComment(problem.getLineNumber(), "Breaking change: " + problem.getTicket());

					return 1;
				}
				catch (IOException ioe) {
				}

				return 0;
			}
		).reduce(
			0, Integer::sum
		);
	}

}