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

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;

import java.util.Set;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.Version;

/**
 * @author Gregory Amerson
 */
public class TransitionPossibleValuesService extends PossibleValuesService {

	protected void addNodeNames(Set<String> values, ElementList<?> nodeList) {
		Node[] nodes = nodeList.toArray(new Node[0]);

		for (Node node : nodes) {
			Value<String> nodeName = node.getName();

			String name = nodeName.content();

			if (!empty(name)) {
				values.add(name);
			}
		}
	}

	@Override
	protected void compute(Set<String> values) {
		/*
		 * if we are a task return states and tasks, if we are a state, find
		 * tasks.
		 */
		Element modelElement = context(Element.class);

		WorkflowDefinition workflow = modelElement.nearest(WorkflowDefinition.class);

		if (workflow == null) {
			workflow = modelElement.adapt(WorkflowDefinition.class);
		}

		if (workflow != null) {
			addNodeNames(values, workflow.getTasks());
			addNodeNames(values, workflow.getStates());
			addNodeNames(values, workflow.getConditions());
			addNodeNames(values, workflow.getForks());
			addNodeNames(values, workflow.getJoins());

			Value<Version> schemaVersion = workflow.getSchemaVersion();

			Version version = schemaVersion.content();

			if (version.compareTo(new Version("6.2")) >= 0) {
				addNodeNames(values, workflow.getJoinXors());
			}
		}
	}

}