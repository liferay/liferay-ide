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

package com.liferay.blade.api;

import java.io.File;

import java.util.List;
import java.util.Set;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public interface Migration {

	public List<Problem> findProblems(File projectDir, ProgressMonitor monitor);

	public List<Problem> findProblems(File projectDir, String version, ProgressMonitor monitor);

	public List<Problem> findProblems(Set<File> files, ProgressMonitor monitor);

	public List<Problem> findProblems(Set<File> files, String version, ProgressMonitor monitor);

	public void reportProblems(List<Problem> problems, int detail, String format, Object... args);

	public int DETAIL_LONG = 1 << 2;

	public int DETAIL_SHORT = 1 << 1;

}