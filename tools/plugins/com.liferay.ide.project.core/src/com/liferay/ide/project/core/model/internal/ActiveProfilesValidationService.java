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

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Tao Tao
 */
public class ActiveProfilesValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	protected Status compute() {
		NewLiferayPluginProjectOp op = _op();

		String activeProfileId = get(op.getActiveProfilesValue());

		NewLiferayProjectProvider<NewLiferayPluginProjectOp> provider = get(op.getProjectProvider());

		if ("maven".equals(provider.getShortName()) && (activeProfileId != null) &&
			activeProfileId.contains(StringPool.SPACE)) {

			return Status.createErrorStatus("No spaces are allowed in profile id values.");
		}

		return Status.createOkStatus();
	}

	@Override
	protected void initValidationService() {
		super.initValidationService();

		_listener = new FilteredListener<Event>() {

			protected void handleTypedEvent(Event event) {
				refresh();
			}

		};

		NewLiferayPluginProjectOp op = _op();

		SapphireUtil.attachListener(op.getProjectProvider(), _listener);
	}

	private NewLiferayPluginProjectOp _op() {
		return context(NewLiferayPluginProjectOp.class);
	}

	private Listener _listener = null;

}