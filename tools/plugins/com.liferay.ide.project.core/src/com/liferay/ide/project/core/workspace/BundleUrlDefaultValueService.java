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

import com.liferay.ide.core.util.ReleaseUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.release.util.ReleaseEntry;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;

/**
 * @author Seiphon Wang
 */
public class BundleUrlDefaultValueService extends DefaultValueService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		NewLiferayWorkspaceOp op = _op();

		if (op != null) {
			SapphireUtil.detachListener(op.property(NewLiferayWorkspaceOp.PROP_LIFERAY_VERSION), _listener);
			SapphireUtil.detachListener(op.property(NewLiferayWorkspaceOp.PROP_PROJECT_PROVIDER), _listener);
			SapphireUtil.detachListener(op.property(NewLiferayWorkspaceOp.PROP_PROVISION_LIFERAY_BUNDLE), _listener);
		}

		super.dispose();
	}

	@Override
	protected String compute() {
		NewLiferayWorkspaceOp op = _op();

		NewLiferayWorkspaceProjectProvider<NewLiferayWorkspaceOp> workspaceProjectProvider = get(
			op.getProjectProvider());

		String buildType = workspaceProjectProvider.getDisplayName();

		if (buildType.equals("Gradle")) {
			return null;
		}

		String targetPlatform = get(op.getTargetPlatform());

		ReleaseEntry releaseEntry = ReleaseUtil.getReleaseEntry(targetPlatform);

		if (releaseEntry != null) {
			return releaseEntry.getBundleURL();
		}

		return null;
	}

	protected void initDefaultValueService() {
		_listener = new Listener() {

			@Override
			public void handle(Event event) {
				refresh();
			}

		};

		SapphireUtil.attachListener(_op().property(NewLiferayWorkspaceOp.PROP_PROJECT_PROVIDER), _listener);
		SapphireUtil.attachListener(_op().property(NewLiferayWorkspaceOp.PROP_LIFERAY_VERSION), _listener);
		SapphireUtil.attachListener(_op().property(NewLiferayWorkspaceOp.PROP_PROVISION_LIFERAY_BUNDLE), _listener);
	}

	private NewLiferayWorkspaceOp _op() {
		return context(NewLiferayWorkspaceOp.class);
	}

	private Listener _listener;

}