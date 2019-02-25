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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class ComponentNameValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	protected Status compute() {
		NewLiferayModuleProjectOp op = _op();

		String className = get(op.getComponentName());

		Status retval = Status.createOkStatus();

		if (CoreUtil.isNotNullOrEmpty(className)) {
			IStatus status = JavaConventions.validateJavaTypeName(
				className, CompilerOptions.VERSION_1_7, CompilerOptions.VERSION_1_7);

			int classNameStatus = status.getSeverity();

			if (className.indexOf('.') != -1) {
				classNameStatus = IStatus.ERROR;
			}

			if (classNameStatus == IStatus.ERROR) {
				retval = Status.createErrorStatus("Invalid class name");
			}
		}

		return retval;
	}

	private NewLiferayModuleProjectOp _op() {
		return context(NewLiferayModuleProjectOp.class);
	}

}