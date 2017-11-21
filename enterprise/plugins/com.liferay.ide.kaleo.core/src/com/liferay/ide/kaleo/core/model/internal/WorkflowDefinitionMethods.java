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

package com.liferay.ide.kaleo.core.model.internal;

import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.model.WorkflowNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Amerson
 */
public class WorkflowDefinitionMethods {

	public static List<WorkflowNode> getDiagramNodes(WorkflowDefinition definition) {
		List<WorkflowNode> nodes = new ArrayList<>();

		nodes.addAll(definition.getStates());
		nodes.addAll(definition.getTasks());
		nodes.addAll(definition.getConditions());
		nodes.addAll(definition.getForks());
		nodes.addAll(definition.getJoins());
		nodes.addAll(definition.getJoinXors());

		return nodes;
	}

}