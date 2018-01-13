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

package com.liferay.ide.kaleo.core.op.internal;

import com.liferay.ide.kaleo.core.op.NewWorkflowDefinitionOp;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.UniversalConversionService;

/**
 * @author Gregory Amerson
 */
public class NewWorkflowDefinitionAdapter extends UniversalConversionService {

	@Override
	public <A> A convert(Object object, Class<A> adapterType) {
		if (adapterType.equals(IProject.class)) {
			NewWorkflowDefinitionOp op = context().find(NewWorkflowDefinitionOp.class);

			ReferenceValue<String, IProject> referProject = op.getProject();

			if (referProject != null) {
				IProject project = referProject.target();

				if (project != null) {
					return adapterType.cast(project);
				}
			}
		}

		return null;
	}

}