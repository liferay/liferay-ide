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

import com.liferay.ide.kaleo.core.model.CanTransition;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.op.NewNodeOp;

import java.lang.reflect.Method;

import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.UniversalConversionService;

/**
 * @author Gregory Amerson
 */
public class NewNodeOpAdapter extends UniversalConversionService {

	@Override
	public <T> T convert(Object object, Class<T> type) {
		if (type.equals(WorkflowDefinition.class)) {
			NewNodeOp op = context().find(NewNodeOp.class);

			if (op.getWorkflowDefinition().content(false) != null) {
				ElementHandle<WorkflowDefinition> workflowDefinition = op.getWorkflowDefinition();

				return type.cast(workflowDefinition.content(false));
			}
		}
		else if (type.equals(CanTransition.class)) {
			Class<?> clazz = object.getClass();

			String simpleName = clazz.getSimpleName();

			int index = simpleName.indexOf("Op$Impl");

			if (index > -1) {
				try {
					String wholeSimpleName = clazz.getSimpleName();

					String simpleNamePrefix = wholeSimpleName.substring(0, index);

					String methodName = "get" + simpleNamePrefix;

					Method method = clazz.getMethod(methodName);

					return type.cast(method.invoke(object));
				}
				catch (Exception e) {
				}
			}
		}

		return null;
	}

}