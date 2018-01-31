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

import com.liferay.ide.kaleo.core.model.Condition;
import com.liferay.ide.kaleo.core.model.Fork;
import com.liferay.ide.kaleo.core.model.Join;
import com.liferay.ide.kaleo.core.model.State;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.model.Transition;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Haoyi Sun
 */
public class NodeNameListener extends FilteredListener<PropertyContentEvent> {

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		Property property = event.property();

		Element element = property.element();

		WorkflowDefinition workflow = element.adapt(WorkflowDefinition.class);

		Map<String, String> eventInfo = new HashMap<>();

		event.fillTracingInfo(eventInfo);

		String oldName = eventInfo.get("before");

		if (oldName == null) {
			oldName = _lastNodeNames.get(property);
		}

		String newName = eventInfo.get("after");

		List<Transition> allTransitions = new ArrayList<>();

		if (workflow != null) {
			ElementList<Condition> allConditions = workflow.getConditions();
			ElementList<Fork> allForks = workflow.getForks();
			ElementList<Join> allJoins = workflow.getJoins();
			ElementList<State> allStates = workflow.getStates();
			ElementList<Task> allTasks = workflow.getTasks();

			for (Condition condition : allConditions) {
				ElementList<Transition> conditionTransitions = condition.getTransitions();

				for (Transition transition : conditionTransitions) {
					allTransitions.add(transition);
				}
			}

			for (Fork fork : allForks) {
				ElementList<Transition> forkTransitions = fork.getTransitions();

				for (Transition transition : forkTransitions) {
					allTransitions.add(transition);
				}
			}

			for (Join join : allJoins) {
				ElementList<Transition> joinTransitions = join.getTransitions();

				for (Transition transition : joinTransitions) {
					allTransitions.add(transition);
				}
			}

			for (State state : allStates) {
				ElementList<Transition> stateTransitions = state.getTransitions();

				for (Transition transition : stateTransitions) {
					allTransitions.add(transition);
				}
			}

			for (Task task : allTasks) {
				ElementList<Transition> taskTransitions = task.getTransitions();

				for (Transition transition : taskTransitions) {
					allTransitions.add(transition);
				}
			}
		}

		for (Transition transition : allTransitions) {
			String targetName = transition.getTarget().toString();

			if (targetName.equals(oldName)) {
				if (newName != null) {
					transition.setTarget(newName);

					if (_lastNodeNames.containsKey(property)) {
						_lastNodeNames.remove(property);
					}
				}
				else {
					_lastNodeNames.put(property, oldName);
				}
			}
		}
	}

	private static final Map<Property, String> _lastNodeNames = new HashMap<>();

}