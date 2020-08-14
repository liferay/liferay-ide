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

package com.liferay.ide.project.core.modules.fragment;

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.modules.BaseModuleOp;

import java.util.Objects;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Joye Luo
 */
@SuppressWarnings("restriction")
public class ModuleFragmentProjectGroupIdValidationService
	extends ValidationService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		if ((_listener != null) && (_op() != null) && !_op().disposed()) {
			Value<String> projectName = _op().getProjectName();

			projectName.detach(_listener);

			Value<Path> location = _op().getLocation();

			location.detach(_listener);

			_listener = null;
		}

		super.dispose();
	}

	@Override
	protected Status compute() {
		NewModuleFragmentOp op = _op();

		NewLiferayProjectProvider<BaseModuleOp> provider = get(op.getProjectProvider());

		if (Objects.equals("maven-module-fragment", provider.getShortName())) {
			Value<String> groupIdValue = _op().getGroupId();

			String groupId = groupIdValue.content(true);

			IStatus javaStatus = JavaConventions.validatePackageName(
				groupId, CompilerOptions.VERSION_1_7, CompilerOptions.VERSION_1_7);

			if (!javaStatus.isOK()) {
				return StatusBridge.create(javaStatus);
			}
		}

		return Status.createOkStatus();
	}

	@Override
	protected void initValidationService() {
		super.initValidationService();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		Value<NewLiferayProjectProvider<BaseModuleOp>> provider = _op().getProjectProvider();

		provider.attach(_listener);

		Value<Path> location = _op().getLocation();

		location.attach(_listener);
	}

	private NewModuleFragmentOp _op() {
		return context(NewModuleFragmentOp.class);
	}

	private Listener _listener;

}