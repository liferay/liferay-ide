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
public class PackageNameValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	protected Status compute() {
		NewLiferayModuleProjectOp op = _op();

		String packageName = get(op.getPackageName());

		Status retval = Status.createOkStatus();

		int packageNameStatus = IStatus.OK;

		if (!CoreUtil.isNullOrEmpty(packageName)) {
			IStatus status = JavaConventions.validatePackageName(
				packageName, CompilerOptions.VERSION_1_7, CompilerOptions.VERSION_1_7);

			packageNameStatus = status.getSeverity();

			if (packageNameStatus == IStatus.ERROR) {
				retval = Status.createErrorStatus("Invalid package name");
			}
		}

		return retval;
	}

	private NewLiferayModuleProjectOp _op() {
		return context(NewLiferayModuleProjectOp.class);
	}

}