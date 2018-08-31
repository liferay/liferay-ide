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

package com.liferay.ide.project.core.upgrade;

import com.liferay.blade.api.Problem;

import java.io.File;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class FileProblemsUtil {

	public static FileProblems[] newFileProblemsListFrom(Problem[] problems) {
		Map<File, FileProblems> fileProblemsMap = new HashMap<>();

		for (Problem problem : problems) {
			FileProblems fileProblem = fileProblemsMap.get(problem.getFile());

			if (fileProblem == null) {
				fileProblem = new FileProblems();
			}

			fileProblem.addProblem(problem);
			fileProblem.setFile(problem.getFile());

			fileProblemsMap.put(problem.getFile(), fileProblem);
		}

		Collection<FileProblems> values = fileProblemsMap.values();

		return values.toArray(new FileProblems[0]);
	}

}