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

/**
 * @author Terry Jia
 */
public class MigrationProblems implements UpgradeProblems {

	@Override
	public FileProblems[] getProblems() {
		return problems;
	}

	public String getSuffix() {
		return suffix;
	}

	@Override
	public String getType() {
		return type;
	}

	public void setProblems(FileProblems[] problems) {
		this.problems = problems;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void setType(String type) {
		this.type = type;
	}

	public FileProblems[] problems;
	public String suffix;
	public String type;

}