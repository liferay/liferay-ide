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

import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.Version;
import org.eclipse.sapphire.services.ReferenceService;

/**
 * @author Gregory Amerson
 */
public class TransitionReferenceService extends ReferenceService<Node> {

	@Override
	public Node compute() {
		String reference = context(Value.class).text();

		if (reference != null) {
			WorkflowDefinition workflow = context(WorkflowDefinition.class);

			List<Node> nodes = new ArrayList<>();

			if (workflow != null) {
				nodes.addAll(workflow.getTasks());
				nodes.addAll(workflow.getStates());
				nodes.addAll(workflow.getConditions());
				nodes.addAll(workflow.getForks());
				nodes.addAll(workflow.getJoins());

				Value<Version> schemaVersion = workflow.getSchemaVersion();

				Version version = schemaVersion.content();

				if (version.compareTo(new Version("6.2")) >= 0) {
					nodes.addAll(workflow.getJoinXors());
				}

				for (Node node : nodes) {
					if (reference.equals(node.getName().content())) {
						return node;
					}
				}
			}
		}

		return null;
	}

}