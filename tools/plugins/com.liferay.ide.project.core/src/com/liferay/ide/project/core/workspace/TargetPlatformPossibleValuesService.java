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

package com.liferay.ide.project.core.workspace;

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.release.util.ReleaseEntry;
import com.liferay.release.util.ReleaseUtil;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class TargetPlatformPossibleValuesService extends PossibleValuesService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		NewLiferayWorkspaceOp op = context(NewLiferayWorkspaceOp.class);

		if (op != null) {
			SapphireUtil.detachListener(op.property(NewLiferayWorkspaceOp.PROP_LIFERAY_VERSION), _listener);
		}

		super.dispose();
	}

	@Override
	public boolean ordered() {
		return true;
	}

	@Override
	protected void compute(Set<String> values) {
		Stream<ReleaseEntry> releaseEntryStream = ReleaseUtil.getReleaseEntryStream();

		values.addAll(
			releaseEntryStream.filter(
				releaseEntry -> Objects.equals(releaseEntry.getProductGroupVersion(), get(_op.getLiferayVersion()))
			).map(
				ReleaseEntry::getTargetPlatformVersion
			).collect(
				Collectors.toUnmodifiableList()
			));
	}

	@Override
	protected void initPossibleValuesService() {
		Job getTargetPlatformVersions = new Job("Get target platform versions") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					refresh();
				}
				catch (Exception exception) {
					ProjectCore.logError("Failed to init product version list.", exception);
				}

				return Status.OK_STATUS;
			}

		};

		getTargetPlatformVersions.setSystem(true);

		getTargetPlatformVersions.schedule();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		_op = context(NewLiferayWorkspaceOp.class);

		SapphireUtil.attachListener(_op.property(NewLiferayWorkspaceOp.PROP_LIFERAY_VERSION), _listener);
	}

	private FilteredListener<PropertyContentEvent> _listener;
	private NewLiferayWorkspaceOp _op;

}