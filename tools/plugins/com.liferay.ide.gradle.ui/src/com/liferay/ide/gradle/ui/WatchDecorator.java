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

package com.liferay.ide.gradle.ui;

import com.liferay.ide.core.util.ListUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;

/**
 * @author Terry Jia
 */
public class WatchDecorator extends LabelProvider implements ILightweightLabelDecorator {

	@Override
	public void decorate(Object element, IDecoration decoration) {
		if (!(element instanceof IProject)) {
			return;
		}

		IProject project = (IProject)element;

		String jobName = project.getName() + " - watch";

		Job[] jobs = Job.getJobManager().find(jobName);

		if (ListUtil.isNotEmpty(jobs)) {
			decoration.addSuffix(" [watching]");
		}
		else {
			decoration.addSuffix("");
		}
	}

}
