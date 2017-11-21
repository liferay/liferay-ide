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

import com.liferay.ide.kaleo.core.model.State;
import com.liferay.ide.kaleo.core.model.WorkflowNodeMetadata;

import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;

/**
 * @author Gregory Amerson
 */
public class StateEndValueBinding extends XmlValueBindingImpl {

	@Override
	public String read() {
		ElementHandle<WorkflowNodeMetadata> nodeMetadata = state().getMetadata();

		WorkflowNodeMetadata metadata = nodeMetadata.content();

		if (metadata != null) {
			Value<Boolean> isTerminal = metadata.isTerminal();

			boolean terminal = isTerminal.content();

			if (terminal) {
				return "true";
			}
		}

		return null;
	}

	@Override
	public void write(String value) {
		ElementHandle<WorkflowNodeMetadata> nodeMetadata = state().getMetadata();

		WorkflowNodeMetadata workflowNodeMetadata = nodeMetadata.content(true);

		workflowNodeMetadata.setTerminal(Boolean.parseBoolean(value));
	}

	protected State state() {
		return property().nearest(State.class);
	}

}