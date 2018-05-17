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

import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.model.WorkflowNode;

import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Gregory Amerson
 */
public class NewNodeNameValidationService extends ValidationService {

	@Override
	public Status compute() {
		Status retval = Status.createOkStatus();

		WorkflowNode newNode = context(WorkflowNode.class);

		WorkflowDefinition workflowDefinition = newNode.adapt(WorkflowDefinition.class);

		if (workflowDefinition != null) {
			for (WorkflowNode node : workflowDefinition.getDiagramNodes()) {
				Value<String> nodeName = node.getName();

				String name = nodeName.content();

				if ((name != null) && name.equals(newNode.getName().content())) {
					retval = Status.createErrorStatus("Name already in use.");

					break;
				}
			}
		}

		return retval;
	}

}