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

package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;

/**
 * @author Gregory Amerson
 */
public class LiferayVersionPossibleValuesService extends PossibleValuesService {

	@Override
	public boolean ordered() {
		return true;
	}

	@Override
	public org.eclipse.sapphire.modeling.Status problem(Value<?> value) {
		return org.eclipse.sapphire.modeling.Status.createOkStatus();
	}

	@Override
	protected void compute(Set<String> values) {
		if (_versions != null) {
			values.addAll(_versions);
		}
		else if (_versionsJob == null) {
			_versionsJob = new Job("Determining possible Liferay versions.") {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					NewLiferayPluginProjectOp op = _op();

					if (!op.disposed()) {
						ILiferayProjectProvider projectProvider = SapphireUtil.getContent(op.getProjectProvider());

						try {
							_versions = projectProvider.getData("liferayVersions", String.class, _groupId, _artifactId);
						}
						catch (Exception e) {
							ProjectCore.logError("Could not determine possible versions.", e);
						}

						refresh();
					}

					return Status.OK_STATUS;
				}

			};

			_versionsJob.schedule();
		}
	}

	@Override
	protected void initPossibleValuesService() {
		super.initPossibleValuesService();

		_groupId = param("groupId");
		_artifactId = param("artifactId");
	}

	private NewLiferayPluginProjectOp _op() {
		return context(NewLiferayPluginProjectOp.class);
	}

	private String _artifactId;
	private String _groupId;
	private List<String> _versions = null;
	private Job _versionsJob = null;

}