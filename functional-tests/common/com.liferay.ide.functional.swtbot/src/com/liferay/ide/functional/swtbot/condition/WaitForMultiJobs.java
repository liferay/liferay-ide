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

package com.liferay.ide.functional.swtbot.condition;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Terry Jia
 */
public abstract class WaitForMultiJobs extends JobCondition {

	public WaitForMultiJobs(Object family, String readableJobFamily) {
		super(family, readableJobFamily);
	}

	@Override
	public String getFailureMessage() {
		if (readableJobFamily != null) {
			return readableJobFamily + " jobs are still running.";
		}

		return "Wait for jobs failed: ";
	}

	public abstract String[] getJobNames();

	@Override
	public void init(SWTBot bot) {
	}

	@Override
	public abstract boolean test();

}